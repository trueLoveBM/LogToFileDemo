package com.kaer.logx.utils;

import android.util.Pair;

import com.kaer.logx.bean.EnumSplitUnit;

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

    public static Pair<String, Integer> getDateStr(EnumSplitUnit unit) {
        String pattern = "yyyy-MM-dd";
        switch (unit) {
            case Hour:
                pattern = "yyyy-MM-dd-hh";
                break;
            case Minute:
                pattern = "yyyy-MM-dd-hh-mm";
                break;
            case Day:
            default:
                pattern = "yyyy-MM-dd";
                break;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = new Date(System.currentTimeMillis());
        String dateStr = format.format(date);
        Integer tag = getCurrentSpliter(unit);

        return new Pair<>(dateStr, tag);
    }


    public static Integer getCurrentSpliter(EnumSplitUnit unit) {
        Integer tag = 0;
        Date date = new Date(System.currentTimeMillis());
        switch (unit) {
            case Hour:
                tag = date.getDay();
                break;
            case Minute:
                tag = date.getMinutes();
                break;
            case Day:
            default:
                tag = date.getDay();
                break;
        }
        return tag;
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