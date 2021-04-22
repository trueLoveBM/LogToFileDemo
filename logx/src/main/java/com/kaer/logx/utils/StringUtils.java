package com.kaer.logx.utils;

/**
 * 字符串扩展方法
 * @author huangfan
 */
public class StringUtils {

    /**
     * 获取指定子串在字符串第n次出现的位置
     * @param str
     * @param substr
     * @param n
     * @return
     */
    public static int ordinalIndexOf(String str, String substr , int n) {
        int  pos = str.indexOf( substr );
        while (--n > 0 && pos != - 1 ) {
            pos = str.indexOf(substr, pos + 1);
        }
        return  pos ;
    }
}
