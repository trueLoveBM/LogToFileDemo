package com.kaer.logx;

import android.content.Context;

/**
 * LogX:用于帮助捕获Logcat日志，并保存到Android客户端下
 * @author huangfan
 */
public class LogX {

    /**
     * 初始化LogX
     *
     * @param context
     */
    public static LogDumper init(Context context) {
        return new LogDumper(context);
    }

}
