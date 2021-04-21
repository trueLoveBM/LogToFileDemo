package com.kaer.logx.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式化类
 */
public  class MyDate {
    public  static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd" );
        String date = format.format( new Date(System.currentTimeMillis()));
        // 2012年10月03日23:41:31
        return date;
    }

    public  static String getDateEN() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        String date1 = format1.format( new Date(System.currentTimeMillis()));
        // 2012-10-03 23:41:31
        return date1;
    }

}