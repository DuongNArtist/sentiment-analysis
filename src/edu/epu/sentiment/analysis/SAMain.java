package edu.epu.sentiment.analysis;

import edu.epu.sentiment.analysis.crawler.SACrawler;
import edu.epu.sentiment.analysis.utils.SALog;

/**
 * Created by duong on 3/27/16.
 */
public class SAMain {

    public static void main(String[] args) {
        SALog.prln("Sentiment Analysis");
        int sleep = 60 * 60 * 1000;
        SACrawler crawler = new SACrawler(sleep);
        crawler.execute();
    }

}
