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
                Elements tagElements = element.getElementsByTag("a");
                if (tagElements != null) {
                    String href = tagElements.attr("href").trim();
                    urls.add(href);
                    SALog.log("GET", href);
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
            documentCrawler.setTitle(document.title().trim());
            //--------------------------------------------------------------------------------------------------------//
            Element bodyElement = document.body();
            Elements bodyElements = bodyElement.select("span.VB_noi_dung");
            if (bodyElements.first() != null) {
                Elements trashElements = bodyElements.first().getElementsByTag("div");
                if (trashElements != null) {
                    trashElements.remove();
                }
            }
            documentCrawler.setBody(bodyElements.text().trim());
            //--------------------------------------------------------------------------------------------------------//
            Elements dateTimeElements = bodyElement.select("div.postby.clearfix");
            if (dateTimeElements != null) {
                String dateTimeString = dateTimeElements.text().trim().toLowerCase();
                documentCrawler.setDateTime(dateTimeString.replaceAll("/", "-"));
            }
            //--------------------------------------------------------------------------------------------------------//
            Elements authorElements = bodyElement.select("div.vivamain181");
            if (authorElements != null) {
                String authorString = authorElements.text().trim();
                documentCrawler.setAuthor(authorString);
            }
            //--------------------------------------------------------------------------------------------------------//
            Elements tagElements = bodyElement.select("div.tag");
            if (tagElements != null) {
                Element tagElement = tagElements.first();
                if (tagElement != null) {
                    StringBuffer tagStrings = new StringBuffer();
                    for (Element aTagElement : tagElement.getElementsByTag("a")) {
                        tagStrings.append(aTagElement.text().trim());
                        tagStrings.append(",");
                    }
                    if (tagStrings.length() > 0) {
                        tagStrings.deleteCharAt(tagStrings.length() - 1);
                    }
                    documentCrawler.setTags(tagStrings.toString());
                }
            }
            //--------------------------------------------------------------------------------------------------------//
            documentCrawler.printDocument();
            documentCrawler.writeDocument(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return documentCrawler;
    }

}
