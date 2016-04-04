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
import java.util.Locale;

/**
 * Created by duong on 3/29/16.
 */
public class SAWebGiaVangComCrawler extends SABaseCrawler {

    public SAWebGiaVangComCrawler(long sleepTime, String[] folders) {
        super(sleepTime, folders);
        groups.add(new SAGroupCrawler("http://webgiavang.com/News/", "http://webgiavang.com/News/%s/", 0, 0, 1));
    }

    @Override
    public ArrayList<String> getChildUrls(String parentUrl) {
        ArrayList<String> urls = new ArrayList<String>();
        try {
            Document document = SADocumentCrawler.getDocumentFromUrl(parentUrl);
            Elements elements = document.select("h3");
            for (Element element : elements) {
                Elements tagElements = element.getElementsByTag("a");
                if (tagElements != null) {
                    String href = tagElements.attr("href").trim();
                    if (href.length() > 0 && href.contains("News/Details")) {
                        href = "http://webgiavang.com" + href;
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
            Elements titleElements = bodyElement.select("h1.titnews");
            //--------------------------------------------------------------------------------------------------------//
            Elements dateTimeElements = titleElements.select("small");
            if (dateTimeElements != null) {
                //17:03 04/04/2016
                String dateTimeString = dateTimeElements.text().trim();
                DateFormat sourceDateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy", Locale.ENGLISH);
                DateFormat targetDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                Date date = sourceDateFormat.parse(dateTimeString);
                dateTimeString = targetDateFormat.format(date).toString();
                documentCrawler.setDateTime(dateTimeString);
                dateTimeElements.remove();
            }
            //--------------------------------------------------------------------------------------------------------//
            String titleString = titleElements.text().trim();
            documentCrawler.setTitle(titleString);
            //--------------------------------------------------------------------------------------------------------//
            Elements bodyElements = bodyElement.select("div.col-md-12.news-detail");
            if (bodyElements.first() != null) {
                String bodyString = bodyElements.first().select("div").first().text().trim();
                documentCrawler.setBody(bodyString);
            }
            //--------------------------------------------------------------------------------------------------------//
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
