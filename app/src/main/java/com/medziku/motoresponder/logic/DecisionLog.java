package com.medziku.motoresponder.logic;

public class DecisionLog {

    private String logStr = "";

    public void add(String line) {
        this.logStr += line + " | ";
    }

    public String getLogStr() {
        return this.logStr;
    }

    public void clear() {
        this.logStr = "";
    }

}
