package edu.epu.sentiment.analysis.crawler;

import java.util.ArrayList;

/**
 * Created by duong on 3/29/16.
 */
public abstract class SABaseCrawler implements SACrawlerCallback {

    protected ArrayList<SAURL> saurls;
    protected long sleep;

    public SABaseCrawler(int sleep) {
        saurls = new ArrayList<SAURL>();
        this.sleep = sleep;
    }

    public ArrayList<SAURL> getSaurls() {
        return saurls;
    }

    public void setSaurls(ArrayList<SAURL> saurls) {
        this.saurls = saurls;
    }

    public abstract ArrayList<String> getDocUrls(String url);

    public abstract SADoc getDocData(String url);
}
