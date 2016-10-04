package com.example.username.forecast;

/**
 * Created by username on 10/3/2016.
 */
public class Forecast {
    private String text;

    private String high;

    private String day;

    private String code;

    private String low;

    private String date;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ClassPojo [text = " + text + ", high = " + high + ", day = " + day + ", code = " + code + ", low = " + low + ", date = " + date + "]";
    }
}
