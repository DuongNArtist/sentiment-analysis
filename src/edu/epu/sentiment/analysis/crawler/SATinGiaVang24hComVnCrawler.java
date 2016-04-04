package edu.epu.sentiment.analysis.crawler;

import edu.epu.sentiment.analysis.utils.SALog;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by duong on 3/29/16.
 */
public class SATinGiaVang24hComVnCrawler extends SABaseCrawler {

    public SATinGiaVang24hComVnCrawler(long sleepTime, String[] folders) {
        super(sleepTime, folders);
        groups.add(new SAGroupCrawler("http://www.24h.com.vn/tin-gia-vang-c424.html", "http://www.24h.com.vn/tin-gia-vang-c424.html/%s/", 0, 0, 1));
    }

    @Override
    public ArrayList<String> getChildUrls(String parentUrl) {
        ArrayList<String> urls = new ArrayList<String>();
        try {
            Document document = SADocumentCrawler.getDocumentFromUrl(parentUrl);
            Elements elements = document.select("span.news-title");
            for (Element element : elements) {
                Elements tagElements = element.getElementsByTag("a");
                if (tagElements != null) {
                    String href = tagElements.attr("href").trim();
                    if (href.length() > 0) {
                        href = "http://www.24h.com.vn" + href;
                        urls.add(href);
                        SALog.log("GET", href);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }

    @Override
    public SADocumentCrawler getDocFromUrl(String url) {
        SADocumentCrawler documentCrawler = new SADocumentCrawler(url);
        try {
            Document document = SADocumentCrawler.getDocumentFromUrl(url);
            Element bodyElement = document.body();
            //--------------------------------------------------------------------------------------------------------//
            Elements titleElements = bodyElement.select("h1.baiviet-title");
            String titleString = titleElements.text().trim();
            documentCrawler.setTitle(titleString);
            //--------------------------------------------------------------------------------------------------------//
            Elements dateTimeElements = bodyElement.select("div.baiviet-ngay");
            if (dateTimeElements != null) {
                //Chủ Nhật, ngày 03/04/2016 13:30 PM (GMT+7)
                String dateTimeString = dateTimeElements.text().trim();
                SALog.log("DT", dateTimeString);
                String[] dateTimeStrings = dateTimeString.split(" ");
                dateTimeString = dateTimeStrings[3] + " " + dateTimeStrings[4];
                DateFormat sourceDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                DateFormat targetDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                Date date = sourceDateFormat.parse(dateTimeString);
                dateTimeString = targetDateFormat.format(date).toString();
                documentCrawler.setDateTime(dateTimeString);
                dateTimeElements.remove();
            }
            //--------------------------------------------------------------------------------------------------------//
            String bodyString = "";
            Elements sumElements = bodyElement.select("p.baiviet-sapo");
            if (sumElements != null) {
                bodyString += sumElements.text();
            }
            //--------------------------------------------------------------------------------------------------------//
            Elements bodyElements = bodyElement.select("div.text-conent").select("p");
            if (bodyElements != null) {
                bodyString += bodyElements.text().trim();
                documentCrawler.setBody(bodyString);
            }
            //--------------------------------------------------------------------------------------------------------//
            Elements authorElements = bodyElement.select("div.nguontin");
            if (authorElements != null) {
                String authorString = authorElements.text().trim();
                int start = authorString.indexOf(" ");
                int end = authorString.indexOf("(");
                if (start > 0 && start < end) {
                    authorString = authorString.substring(start, end).trim();
                }
                documentCrawler.setAuthor(authorString);
            }
            documentCrawler.printDocument();
            documentCrawler.writeDocument(directory);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return documentCrawler;
    }

}
