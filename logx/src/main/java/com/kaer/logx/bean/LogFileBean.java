package com.kaer.logx.bean;

import java.io.FileOutputStream;

/**
 * 日志文件的对象
 */
public class LogFileBean {

    /**
     * 日志文件名，无后缀
     */
    private String fileName;


    /**
     * 文件输出流
     */
    private FileOutputStream outputStream;


    /**
     * 日志文件生成的分隔符
     * EnumSplitUnit-》Minute为生成的分钟
     * .....
     */
    private int SplitValue;




    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(FileOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public int getSplitValue() {
        return SplitValue;
    }

    public void setSplitValue(int splitValue) {
        SplitValue = splitValue;
    }

}
