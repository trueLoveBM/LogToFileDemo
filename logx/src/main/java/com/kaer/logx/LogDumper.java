package com.kaer.logx;

import android.content.Context;
import android.util.Log;

import com.kaer.logx.bean.LogBean;
import com.kaer.logx.bean.LogFileBean;
import com.kaer.logx.utils.LogFileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

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
    private LogFileBean currentFileBean = null;

    /**
     * 用于控制抓取线程是否继续运行
     */
    private boolean mRunning = false;

    private String packageName;

    private LogXConfig config;

    public LogDumper(Context context) {
        mWeakContext = new WeakReference<>(context);
        packageName = context.getPackageName();
        pid = String.valueOf(android.os.Process.myPid());
    }


    public LogDumper setConfig(LogXConfig config) {
        this.config = config;
        return this;
    }

    public LogDumper setConfig(String configFile) {
        Context context = mWeakContext.get();
        this.config = LogXConfig.parseFromConfig(context, configFile);
        return this;
    }


    /**
     * 开启日志抓取
     *
     * @return
     */
    public LogDumper startDumper() {
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
                //判断是否需要切换日志文件
                if (LogFileUtils.needCreateNewFile(currentFileBean, config)) {
                    if (currentFileBean != null && currentFileBean.getOutputStream() != null) {
                        currentFileBean.getOutputStream().close();
                    }

                    currentFileBean = LogFileUtils.getLogFileBean(config);
                }

                if (currentFileBean.getOutputStream() != null && line.contains(pid)) {

                    LogBean logBean = LogBean.parse(line);

                    //解析失败的日志直接保存
                    if (!logBean.isParseSuccess()) {
                        currentFileBean.getOutputStream().write((line + "(解析失败输出)\n")
                                .getBytes());
                    }
                    //解析成功的日志，判断是否可以需要保存，需要则进行保存
                    else if (config.canWriteLog(logBean)) {
                        currentFileBean.getOutputStream().write((line + "\n")
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
            if (currentFileBean != null && currentFileBean.getOutputStream() != null) {
                try {
                    currentFileBean.getOutputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentFileBean.setOutputStream(null);
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

    public String getLogFilePath() {
        return config.getLogPath();
    }
}
