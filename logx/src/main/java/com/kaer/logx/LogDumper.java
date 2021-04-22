package com.kaer.logx;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.kaer.logx.bean.LogBean;
import com.kaer.logx.utils.DateUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 日志转储器
 *
 * @author Kaer
 */
public class LogDumper extends Thread {

    /**
     * 上下文Context的若引用
     */
    private WeakReference<Context> mWeakContext;

    /**
     * Log文件保存的路径
     */
    private String logFilePath;

    /**
     * 当前应用的进程
     */
    private String pid;

    /**
     * logCat进程
     */
    private Process logcatProc;

    /**
     * logcat日志读取者
     */
    private BufferedReader mReader = null;

    /**
     * adb命令，通过此命令抓取log
     */
    String cmds = null;

    /**
     * 日志输出流
     */
    private FileOutputStream out = null;

    /**
     * 用于控制抓取线程是否继续运行
     */
    private boolean mRunning = false;

    /**
     * 抓取日志的tags,即日志为此类型tag时，进行捕获输出
     * 若未指定，则抓所有日志
     */
    private ArrayList<String> filterTags;

    private String packageName;

    public LogDumper(Context context) {
        mWeakContext = new WeakReference<>(context);
        packageName = context.getPackageName();
        pid = String.valueOf(android.os.Process.myPid());
    }


    /**
     * 开启日志抓取
     *
     * @return
     */
    public LogDumper startDumper() {
        //没有指定日志的存储路径
        if (TextUtils.isEmpty(logFilePath)) {
            if (!initLogFilePath()) {
                throw new RuntimeException("LogX->初始化LogX的log存储路径失败");
            }
        }

        mkLogFilePath();

        try {

            File file = new File(logFilePath, "GPS-"
                    + DateUtils.getDateStr() + ".log");
            //若存在日志文件则追加
            boolean isAppend = file.exists();
            out = new FileOutputStream(file, isAppend);
            if (isAppend) {
                out.write("**********************************日志追加分割线******************************************\n".getBytes());
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


        /**
         *
         * 日誌等級：*:v , *:d , *:w , *:e , *:f , *:s
         *
         * 顯示當前mPID程序的E和W等級的日誌.
         *
         * */

        // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
        // cmds = "logcat -s way"; //打印標籤過濾信息
        // cmds = "logcat *:e *:i | grep \"(" + mPID + ")\""

        //打印所有日誌信息
        cmds = "logcat | grep \"(" + pid + ")\"";
        //启动抓取线程
        start();

        return this;
    }

    /**
     * 设置需要捕获异常的tag
     *
     * @param tags
     * @return
     */
    public LogDumper tags(String... tags) {
        if (tags.length > 0) {
            filterTags = new ArrayList<>();
            for (String tag : tags) {
                filterTags.add(tag);
            }
        }
        return this;
    }


    @Override
    public void run() {
        mRunning = true;
        try {
            logcatProc = Runtime.getRuntime().exec(cmds);
            mReader = new BufferedReader(new InputStreamReader(
                    logcatProc.getInputStream()), 1024);
            String line = null;
            while (mRunning && (line = mReader.readLine()) != null) {
                if (!mRunning) {
                    break;
                }
                if (line.length() == 0) {
                    continue;
                }
                if (out != null && line.contains(pid)) {

                    LogBean logBean = LogBean.parse(line);

                    //解析失败的日志直接保存
                    if (!logBean.isParseSuccess()) {
                        out.write((line + "(解析失败输出)\n")
                                .getBytes());
                    }
                    //解析成功的日志，判断是否可以需要保存，需要则进行保存
                    else if (canLogOutPut(logBean)) {
                        out.write((line + "\n")
                                .getBytes());
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.d("hf", "退出日志抓取");
            if (logcatProc != null) {
                logcatProc.destroy();
                logcatProc = null;
            }
            if (mReader != null) {
                try {
                    mReader.close();
                    mReader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
        }
    }


    /**
     * 停止日志的抓取
     */
    public void reStartDumper() {
        mRunning = true;

        start();
    }

    /**
     * 停止日志的抓取
     */
    public void stopLogs() {
        mRunning = false;
    }

    /**
     * 释放资源
     */
    public void close() {
        mRunning = false;
        mWeakContext.clear();
    }

    /**
     * 若日志存储路径尚未初始化，则初始化其路径
     */
    private void mkLogFilePath() {
        File file = new File(logFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 指定日志文件的保存路径
     * 若未调用此方法，则会调用initLogFilePath方法，初始化默认保存路径
     *
     * @param logFilePath
     */
    public LogDumper setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
        return this;
    }

    /**
     * 初始化Log文件保存路线
     */
    private boolean initLogFilePath() {
        boolean initOk = false;
        //優先保存到SD卡中
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            logFilePath = mWeakContext.get().getExternalFilesDir(null).getAbsolutePath()+ File.separator + "miniGPS";
            initOk = true;
        } else { //如果SD卡不存在，就保存到本應用的目錄下
            if (mWeakContext.get() != null) {
                logFilePath = mWeakContext.get().getFilesDir().getAbsolutePath()
                        + File.separator + "miniGPS";
                initOk = true;
            }
        }
        return initOk;
    }

    /**
     * 获取日志文件保存路径
     *
     * @return
     */
    public String getLogFilePath() {
        return logFilePath;
    }

    /**
     * 判单是否输出此条日志
     *
     * @param bean
     * @return
     */
    private boolean canLogOutPut(LogBean bean) {
        if (filterTags == null) {
            return true;
        }

        for (String tag : filterTags) {
            if (tag.equals(bean.getTag())) {
                return true;
            }
        }

        return false;
    }
}
