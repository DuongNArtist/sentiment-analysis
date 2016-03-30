package edu.epu.sentiment.analysis.crawler;

import edu.epu.sentiment.analysis.utils.SADate;
import edu.epu.sentiment.analysis.utils.SALog;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by duong on 3/29/16.
 */
public class SAVietBaoVnCrawler extends SABaseCrawler {

    public SAVietBaoVnCrawler(long sleepTime, String[] folders) {
        super(sleepTime, folders);
        groups.add(new SAGroupCrawler("http://vietbao.vn/top/Thi-truong-vang/", "http://vietbao.vn/top/Thi-truong-vang/%s/", 2, 199, 1));
    }

    @Override
    public ArrayList<String> getChildUrls(String parentUrl) {
        ArrayList<String> urls = new ArrayList<String>();
        try {
            Document document = SADocumentCrawler.getDocumentFromUrl(parentUrl);
            Elements elements = document.select("div#vb-content-detailbox");
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
            //--------------------------------------------------------------------------------------------------------//
            Element body = document.body();
            Elements bodyElements = body.select("span.VB_noi_dung");
            if (bodyElements.first() != null) {
                Elements trashElements = bodyElements.first().getElementsByTag("div");
                if (trashElements != null) {
                    trashElements.remove();
                }
            }
            doc.setBody(bodyElements.text().trim());
            //--------------------------------------------------------------------------------------------------------//
            String dateTime = body.select("div.postby.clearfix").text().toLowerCase();
            String[] dateTimes = dateTime.split(",");
            if (dateTimes.length >= 2) {
                //--------------------------------------------------------------------------------------------------------//
                String date = dateTimes[1].trim();
                for (String key : SADate.months.keySet()) {
                    if (date.contains(key)) {
                        date = date.replace(key, SADate.months.get(key));
                    }
                }
                date = date.replace(" ", "/");
                doc.setDate(date);
                //--------------------------------------------------------------------------------------------------------//
                String time = dateTimes[2].trim().split(" ")[0];
                doc.setTime(time);
            }
            //--------------------------------------------------------------------------------------------------------//
            doc.setAuthor(body.select("div.vivamain181").text().trim());
            //--------------------------------------------------------------------------------------------------------//
            Element tags = body.select("div.tag").first();
            StringBuffer docTags = new StringBuffer();
            for (Element tag : tags.getElementsByTag("a")) {
                docTags.append(tag.text());
                docTags.append(",");
            }
            docTags.deleteCharAt(docTags.length() - 1);
            doc.setTags(docTags.toString());
            //--------------------------------------------------------------------------------------------------------//
            doc.printDocument();
            doc.writeDocument(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

}
