package com.kaer.logx.utils;

import android.content.Context;
import android.util.Pair;

import com.kaer.logx.LogXConfig;
import com.kaer.logx.bean.LogFileBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogFileUtils {


    public static boolean needCreateNewFile(LogFileBean currentBean, LogXConfig config) {
        if (currentBean == null) {
            return true;
        }
        int split = DateUtils.getCurrentSpliter(config.getSplitUnit());
        return currentBean.getSplitValue() != split;
    }

    public static LogFileBean getLogFileBean(LogXConfig config) throws IOException {
        mkLogFilePath(config);

        LogFileBean fileBean = new LogFileBean();
        Pair<String, Integer> pair = DateUtils.getDateStr(config.getSplitUnit());
        File file = new File(config.getLogPath(), pair.first
                + ".log");
        //若存在日志文件则追加
        boolean isAppend = file.exists();
        FileOutputStream out = new FileOutputStream(file, isAppend);
        if (isAppend) {
            out.write("**********************************日志追加分割线******************************************\n".getBytes());
        }
        fileBean.setFileName(pair.first);
        fileBean.setSplitValue(pair.second);
        fileBean.setOutputStream(out);
        return fileBean;

    }


    /**
     * 若日志存储路径尚未初始化，则初始化其路径
     */
    private static void mkLogFilePath(LogXConfig config) {
        File file = new File(config.getLogPath());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 初始化Log文件保存路径
     */
    public static String getDefaultLogPath(Context context) {
        return context.getCacheDir() + File.separator + "logX";
    }

}
