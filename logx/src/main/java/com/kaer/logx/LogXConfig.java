package com.kaer.logx;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.kaer.logx.bean.EnumSplitUnit;
import com.kaer.logx.bean.LogBean;
import com.kaer.logx.utils.LogFileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * logX的配置文件
 */
public class LogXConfig {

    /**
     * 日志存储的路径
     */
    private String logPath;

    /**
     * 捕获的日志tag s
     */
    private List<String> eableTags;


    /**
     * 忽略的日志tag s
     */
    private List<String> ignoreTags;


    /**
     * 日志分割的依据
     */
    private EnumSplitUnit splitUnit;

    public LogXConfig() {

    }

    public LogXConfig(Builder builder) {
        this.logPath = builder.logPath;
        this.eableTags = builder.eableTags;
        this.ignoreTags = builder.ignoreTags;
        this.splitUnit = builder.splitUnit;
    }

    /**
     * 获取默认配置
     *
     * @return
     */
    public static LogXConfig getDefaultConfig(Context context) {
        LogXConfig config = new LogXConfig();
        config.logPath = LogFileUtils.getDefaultLogPath(context);
        config.eableTags = new ArrayList<>();
        config.ignoreTags = new ArrayList<>();
        config.splitUnit = EnumSplitUnit.Hour;
        return config;
    }

    public static LogXConfig parseFromConfig(Context context, String configFileName) {
        LogXConfig config = null;
        try {

            config = new LogXConfig();


            Properties props = new Properties();
            InputStream in = context.getAssets().open(configFileName);
            props.load(in);

            config.logPath = props.getProperty("Logx.LogPath");
            if (TextUtils.isEmpty(config.logPath)) {
                config.logPath = LogFileUtils.getDefaultLogPath(context);
            }

            String enableTags = props.getProperty("LogX.EnableTags");
            String ignoreTags = props.getProperty("LogX.IgnoreTags");
            String[] enableTagsArray = enableTags.split("|");
            String[] ignoreTagsArray = ignoreTags.split("|");

            config.eableTags = new ArrayList<>(enableTagsArray.length);
            for (String enableTag : enableTagsArray) {
                config.eableTags.add(enableTag);
            }
            config.ignoreTags = new ArrayList<>(ignoreTagsArray.length);
            for (String ignoreTag : ignoreTagsArray) {
                config.ignoreTags.add(ignoreTag);
            }

            String splitUnitStr = props.getProperty("LogX.SplitUnit");
            config.splitUnit = EnumSplitUnit.valueOf(splitUnitStr);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("hf", "从" + configFileName + "解析日志失败,使用默认配置");
            config = getDefaultConfig(context);
        }

        return config;
    }


    public boolean canWriteLog(LogBean log) {

        if (eableTags.size() > 0) {
            return eableTags.contains(log.getTag());
        }

        if (ignoreTags.size() > 0) {
            return !ignoreTags.contains(log.getTag());
        }

        return true;
    }


    public String getLogPath() {
        return logPath;
    }


    public List<String> getEableTags() {
        return eableTags;
    }


    public List<String> getIgnoreTags() {
        return ignoreTags;
    }


    public EnumSplitUnit getSplitUnit() {
        return splitUnit;
    }


    public static class Builder {

        /**
         * 日志存储的路径
         */
        private String logPath;

        /**
         * 捕获的日志tag s
         */
        private List<String> eableTags;


        /**
         * 忽略的日志tag s
         */
        private List<String> ignoreTags;

        /**
         * 日志分割的依据
         */
        private EnumSplitUnit splitUnit;


        private WeakReference<Context> contextWeakReference;

        public Builder(Context context) {
            contextWeakReference = new WeakReference<>(context);
            logPath = LogFileUtils.getDefaultLogPath(context);
            eableTags = new ArrayList<>();
            ignoreTags = new ArrayList<>();
            splitUnit = EnumSplitUnit.Hour;
        }

        public Builder setLogPath(String logPath) {
            this.logPath = logPath;
            return this;
        }

        public Builder setEableTags(String... enableTags) {
            if (enableTags.length > 0) {
                for (String tag : enableTags) {
                    this.eableTags.add(tag);
                }
            }
            return this;
        }

        public Builder setIgnoreTags(String... ignoreTags) {
            if (ignoreTags.length > 0) {
                for (String tag : ignoreTags) {
                    this.ignoreTags.add(tag);
                }
            }
            return this;
        }

        public Builder setSplitUnit(EnumSplitUnit splitUnit) {
            this.splitUnit = splitUnit;
            return this;
        }


        public Builder FromConfig(String configFileName) {
            Context context = contextWeakReference.get();
            try {
                Properties props = new Properties();
                InputStream in = context.getAssets().open(configFileName);
                props.load(in);

                String cfgPath = props.getProperty("Logx.LogPath");
                if (!TextUtils.isEmpty(cfgPath)) {
                    this.logPath = LogFileUtils.getDefaultLogPath(context);
                }

                String enableTags = props.getProperty("LogX.EnableTags");
                String ignoreTags = props.getProperty("LogX.IgnoreTags");
                String[] enableTagsArray = enableTags.split("|");
                String[] ignoreTagsArray = ignoreTags.split("|");

                for (String enableTag : enableTagsArray) {
                    this.eableTags.add(enableTag);
                }
                for (String ignoreTag : ignoreTagsArray) {
                    this.ignoreTags.add(ignoreTag);
                }

                String splitUnitStr = props.getProperty("LogX.SplitUnit");
                if (!TextUtils.isEmpty(splitUnitStr)) {
                    this.splitUnit = EnumSplitUnit.valueOf(splitUnitStr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return this;
        }

        public LogXConfig build() {
            return new LogXConfig(this);
        }

    }

}
