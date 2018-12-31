package com.gigaappz.vipani.models;

public class ForeignValueModel {
    private boolean isProfit;
    private String id;
    private String headText;
    private String subHeadText;
    private String valueText;
    private String valueSubText;
    private String valueRateText;
    private String valueDiffText;
    private String time;

    public String getTimetext() {
        return timetext;
    }

    public void setTimetext(String timetext) {
        this.timetext = timetext;
    }

    private String timetext;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeadText() {
        return headText;
    }

    public void setHeadText(String headText) {
        this.headText = headText;
    }

    public String getSubHeadText() {
        return subHeadText;
    }

    public void setSubHeadText(String subHeadText) {
        this.subHeadText = subHeadText;
    }

    public boolean isProfit() {
        return isProfit;
    }

    public void setProfit(boolean profit) {
        isProfit = profit;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public String getValueRateText() {
        return valueRateText;
    }

    public void setValueRateText(String valueRateText) {
        this.valueRateText = valueRateText;
    }

    public String getValueDiffText() {
        return valueDiffText;
    }

    public void setValueDiffText(String valueDiffText) {
        this.valueDiffText = valueDiffText;
    }

    public String getValueSubText() {
        return valueSubText;
    }

    public void setValueSubText(String valueSubText) {
        this.valueSubText = valueSubText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
