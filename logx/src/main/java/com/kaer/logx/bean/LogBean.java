package com.kaer.logx.bean;

import android.os.Build;
import android.text.TextUtils;

import com.kaer.logx.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * log日志信息实体对象
 */
public class LogBean {
    /**
     * 用于解析Log字符的正则表达式
     */
    private static final String LOG_PATTERN_STR = "(?<time>\\d{2}[-]\\d{2}[\\s]+\\d{2}[\\:]\\d{2}[\\:]\\d{2}[\\.]\\d{3})[\\s]+(?<pid>\\d+)[\\s]+(?<tid>\\d+)[\\s]+(?<level>[V|D|I|W|E|F|S])[\\s]+(?<tag>[\\w||\\W]+?\\:)(?<text>[\\w||\\W||\\s]*)";

    /**
     * 用于解析Log字符的正则
     */
    private static final Pattern LOG_PATTERN = Pattern.compile(LOG_PATTERN_STR);

    private String level;
    private String time;
    private String pid;
    private String tid;
    private String tag;
    private String text;

    /**
     * 解析日志是否成功
     */
    private boolean parseSuccess = false;

    private LogBean() {
    }

    private LogBean(String log) {
//        parseLogByStringApi(log);
        parseLogByPattern(log);
    }


    public static LogBean Parse(String log) {
        LogBean bean = new LogBean(log);
        return bean;
    }

    /**
     * 解析log信息成实体对象
     * 实现方式为正则表达式
     *
     * @param log 要解析的日志
     */
    private void parseLogByPattern(String log) {
//        log = "04-22 11:50:57.922  2174  2332 I ActivityManager: Start proc 13828:com.example.logtofiledemo/u0a41 for activity com.example.logtofiledemo/.MainActivity";
//        String LOG_PATTERN_STR = "(?<time>\\d{2}[-]\\d{2}[\\s]\\d{2}[\\:]\\d{2}[\\:]\\d{2}[\\.]\\d{3})[\\s]+(?<pid>\\d+)";
//        Pattern LOG_PATTERN = Pattern.compile(LOG_PATTERN_STR);

        //04-22 09:54:41.961  5816  5816 D hf      : 测试
        Matcher m = LOG_PATTERN.matcher(log);
        if (m.find()) {
            parseSuccess = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                time = m.group("time");
                pid = m.group("pid");
                tid = m.group("tid");
                level = m.group("level");
                tag = m.group("tag");
                //去掉最后的冒号及空白字符
                tag = tag.substring(0, tag.length() - 1).trim();
                text = m.group("text");
            } else {
                time = m.group(1);
                pid = m.group(2);
                tid = m.group(3);
                level = m.group(4);
                tag = m.group(5);
                //去掉最后的冒号及空白字符
                tag = tag.substring(0, tag.length() - 1).trim();
                text = m.group(6);
            }
        }
    }

    /**
     * 解析log信息成实体对象
     * 实现方式为字符串的api，效率较低，出错概率大，已弃用
     * 请替换使用parseLogByPattern(str)发法
     *
     * @param log 要解析的日志
     */
    @Deprecated
    private void parseLogByStringApi(String log) {
        int in = StringUtils.ordinalIndexOf(log, ":", 3);
        if (in != -1) {
            LogBean logBean = new LogBean();
            //相关文本
            this.text = log.substring(in + 1);
            String loginfo = log.substring(0, in);
            String[] loginfoArray = loginfo.split(" ");
            //去掉分割后的数组中的空字符串
            List<String> strlist = new ArrayList<>();
            for (String info : loginfoArray) {
                if (!TextUtils.isEmpty(info)) {
                    strlist.add(info);
                }
            }

            loginfoArray = new String[strlist.size()];
            strlist.toArray(loginfoArray);

            if (loginfoArray.length == 6) {
                parseSuccess = true;
                //时间
                this.time = loginfoArray[0] + loginfoArray[1];
                //Pid
                this.pid = loginfoArray[2];
                //Tid
                this.tid = loginfoArray[3];
                //Level
                this.level = loginfoArray[4];
                //tag
                this.tag = loginfoArray[5];
            }
        }
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        text = text;
    }

    /**
     * 解析Log日志成实体对象是否成功
     *
     * @return true解析成功，当前对象可以使用；false，解析失败，当前对象无法使用
     */
    public boolean isParseSuccess() {
        return parseSuccess;
    }
}