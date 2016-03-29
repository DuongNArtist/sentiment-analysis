package edu.epu.sentiment.analysis.crawler;

import edu.epu.sentiment.analysis.utils.SALog;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by duong on 3/29/16.
 */
public class SAGiaVangNetCrawler extends SABaseCrawler {

    public SAGiaVangNetCrawler(long sleepTime, String[] folders) {
        super(sleepTime, folders);
        groups.add(new SAGroupCrawler("http://www.giavang.net/category/vang-the-gioi/", "http://www.giavang.net/category/vang-the-gioi/page/%s/", 2, 198, 1));
        groups.add(new SAGroupCrawler("http://www.giavang.net/category/vang-trong-nuoc-2/", "http://www.giavang.net/category/vang-trong-nuoc-2/page/%s/", 2, 87, 1));
        groups.add(new SAGroupCrawler("http://www.giavang.net/category/phan-tich-vang/", "http://www.giavang.net/category/phan-tich-vang/page/%s/", 2, 232, 1));
    }

    @Override
    public ArrayList<String> getChildUrls(String parentUrl) {
        ArrayList<String> urls = new ArrayList<String>();
        try {
            Document document = SADocumentCrawler.getDocumentFromUrl(parentUrl);
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
    public SADocumentCrawler getDocFromUrl(String url) {
        SADocumentCrawler doc = new SADocumentCrawler(url, "Gold Price");
        try {
            Document document = SADocumentCrawler.getDocumentFromUrl(url);
            doc.setTitle(document.title().trim());
            Element body = document.body();
            doc.setBody(body.select("div#content-area").text().trim());
            String dateTime = body.select("div#post-info-left").text();
            dateTime = dateTime.substring(dateTime.indexOf("on ") + 3).replace("  ", " ");
            String[] dateTimes = dateTime.split(" ");
            doc.setDate(dateTimes[0]);
            doc.setTime(dateTimes[1]);
            doc.setAuthor(body.select("div#author-desc").first().getElementsByTag("h4").text().replace("About ", "").trim());
            Element tags = body.select("div.post-tags").first();
            StringBuffer docTags = new StringBuffer();
            for (Element tag : tags.getElementsByTag("a")) {
                docTags.append(tag.text());
                docTags.append(",");
            }
            docTags.deleteCharAt(docTags.length() - 1);
            doc.setTags(docTags.toString());
            doc.printDocument();
            doc.writeDocument(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

}
