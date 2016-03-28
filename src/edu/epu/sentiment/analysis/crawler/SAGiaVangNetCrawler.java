package edu.epu.sentiment.analysis.crawler;

import edu.epu.sentiment.analysis.utils.SAFile;
import edu.epu.sentiment.analysis.utils.SALog;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by duong on 3/29/16.
 */
public class SAGiaVangNetCrawler extends SABaseCrawler {

    private ArrayList<SAURL> saurls;

    public SAGiaVangNetCrawler(int sleep) {
        super(sleep);
        saurls = new ArrayList<SAURL>();
        saurls.add(new SAURL("http://www.giavang.net/category/vang-the-gioi/", "http://www.giavang.net/category/vang-the-gioi/page/%s/", 2, 198, 1));
        saurls.add(new SAURL("http://www.giavang.net/category/vang-trong-nuoc-2/", "http://www.giavang.net/category/vang-trong-nuoc-2/page/%s/", 2, 87, 1));
        saurls.add(new SAURL("http://www.giavang.net/category/phan-tich-vang/", "http://www.giavang.net/category/phan-tich-vang/page/%s/", 2, 232, 1));
    }

    @Override
    public ArrayList<String> getDocUrls(String url) {
        ArrayList<String> urls = new ArrayList<String>();
        try {
            Document document = SADoc.getDocument(url);
            Elements elements = document.select("div.category3-text");
            for (Element element : elements) {
                String link = element.getElementsByTag("a").attr("href");
                urls.add(link);
                SALog.log("GET", link);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }

    @Override
    public SADoc getDocData(String url) {
        SADoc doc = new SADoc(url, "Gold Price");
        doc.setCallback(new SACrawlerCallback() {
            @Override
            public void execute() {
                try {
                    Document document = SADoc.getDocument(url);
                    doc.setTitle(document.title());
                    Element body = document.body();
                    doc.setBody(body.select("div#content-area").text());
                    String dateTime = body.select("div#post-info-left").text();
                    dateTime = dateTime.substring(dateTime.indexOf("on ") + 3).replace("  ", " ");
                    String[] dateTimes = dateTime.split(" ");
                    doc.setDate(dateTimes[0]);
                    doc.setTime(dateTimes[1]);
                    Element tags = body.select("div.post-tags").first();
                    for (Element tag : tags.getElementsByTag("a")) {
                        doc.getTags().add(tag.text());
                    }
                    doc.setAuthor(body.select("div#author-desc").first().getElementsByTag("h4").text().replace("About ", ""));
                    doc.print();
                    doc.write(SAFile.dir + File.separator + "res" + File.separator + "goldprice" + File.separator + "giavangnet");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return doc;
    }

    @Override
    public void execute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (SAURL saurl : saurls) {
                    for (String url : saurl.generateUrls()) {
                        for (String link : getDocUrls(url)) {
                            SADoc doc = getDocData(link);
                            doc.execute();
                        }
                        try {
                            SALog.log("SLEEP", sleep + " ms");
                            Thread.sleep(sleep);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
}
