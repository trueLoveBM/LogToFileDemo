package com.kaer.logx.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式化类
 *
 * @author huangfan
 */
public class DateUtils {
    /**
     * 获取当前时间yyyy-MM-dd格式
     *
     * @return
     */
    public static String getDateStr() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }

    /**
     * 获取当前时间yyyy-MM-dd HH:mm:ss格式，
     *
     * @return
     */
    public static String getTimeStr() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format1.format(new Date(System.currentTimeMillis()));
        return date1;
    }

}