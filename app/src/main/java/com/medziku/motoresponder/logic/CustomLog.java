package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.R;

import java.util.Date;

public class CustomLog {

    private int MAX_LOG_LENGTH = 5000;

    private Settings settings;

    public CustomLog(Settings settings) {
        this.settings = settings;
    }

    public String getLogStr() {
        String logStr = this.settings.getStringValue(R.string.custom_log_key);

        if (logStr == null) {
            return "";
        }

        return logStr;
    }

    private void setLogStr(String logStr) {
        this.settings.setStringValue(R.string.custom_log_key, logStr);
    }

    public void add(String line) {
        String logStr = this.getLogStr();

        Date currentDate = new Date();

        logStr = currentDate.toString()+ ": " + line + " \r\n \r\n" + logStr;

        if (logStr.length() > MAX_LOG_LENGTH) {
            logStr = logStr.substring(0, MAX_LOG_LENGTH);
        }

        this.setLogStr(logStr);
    }


    public void clear() {
        this.setLogStr("");
    }

}
