package edu.epu.sentiment.analysis.crawler;

import java.util.ArrayList;

/**
 * Created by duong on 3/29/16.
 */
public class SAURL {

    private String url;
    private String format;
    private int start;
    private int end;
    private int step;

    public SAURL() {

    }

    public SAURL(String url, String format, int start, int end, int step) {
        this.url = url;
        this.format = format;
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public ArrayList<String> generateUrls() {
        ArrayList<String> urls = new ArrayList<String>();
        urls.add(url);
        for (int i = start; i <= end; i++) {
            urls.add(String.format(format, i));
        }
        return urls;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
