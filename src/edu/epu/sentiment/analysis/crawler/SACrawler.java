package edu.epu.sentiment.analysis.crawler;

import java.util.ArrayList;

/**
 * Created by duong on 3/29/16.
 */
public class SACrawler implements SACrawlerCallback {

    private ArrayList<SABaseCrawler> saCrawlers;
    private int sleep;

    public SACrawler(int sleep) {
        this.sleep = sleep;
        saCrawlers = new ArrayList<SABaseCrawler>();
        saCrawlers.add(new SAGiaVangNetCrawler(3000));
    }

    @Override
    public void execute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (SABaseCrawler crawler : saCrawlers) {
                        crawler.execute();
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}