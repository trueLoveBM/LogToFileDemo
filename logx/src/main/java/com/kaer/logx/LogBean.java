package com.kaer.logx;

import com.kaer.logx.utils.StringUtils;

/**
 * log日志信息实体对象
 */
public class LogBean {

    private String level;
    private String time;
    private String pid;
    private String tid;
    private String tag;
    private String text;

    public LogBean() {
    }

    public LogBean(String log) {

        int in = StringUtils.ordinalIndexOf(log, ":", 3);
        if (in != -1) {
            LogBean logBean = new LogBean();
            //相关文本
            this.text = log.substring(in + 1);
            String loginfo = log.substring(0, in);
            String[] loginfoArray = loginfo.split(" ");
            if (loginfoArray.length == 6) {
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
}
