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
public class SAGiaVangDojiVnCrawler extends SABaseCrawler {

    public SAGiaVangDojiVnCrawler(long sleepTime, String[] folders) {
        super(sleepTime, folders);
        groups.add(new SAGroupCrawler("http://giavang.doji.vn/doji/tintuc/", "http://giavang.doji.vn/doji/tintuc/%s/", 2, 130, 1));
    }

    @Override
    public ArrayList<String> getChildUrls(String parentUrl) {
        ArrayList<String> urls = new ArrayList<String>();
        try {
            Document document = SADocumentCrawler.getDocumentFromUrl(parentUrl);
            Elements elements = document.select("a.news_desktop");
            for (Element element : elements) {
                String href = element.attr("href").trim();
                if (href.length() > 0 && href.contains("kinhdoanh.vnexpress.net")) {
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
            Element bodyElement = document.body();
            //--------------------------------------------------------------------------------------------------------//
            Elements titleElements = bodyElement.select("div.title_news");
            String titleString = titleElements.text().trim();
            documentCrawler.setTitle(titleString);
            //--------------------------------------------------------------------------------------------------------//
            String bodyString = "";
            Elements shortIntroElements = bodyElement.select("div.short_intro.txt_666");
            if (shortIntroElements != null) {
                bodyString = shortIntroElements.text().trim();
            }
            Elements detailElements = bodyElement.select("div.fck_detail.width_common");
            if (detailElements != null) {
                Elements authorElements = detailElements.select("strong");
                if (authorElements != null) {
                    String authorString = authorElements.text().trim();
                    documentCrawler.setAuthor(authorString);
                    authorElements.remove();
                }
                bodyString += detailElements.text().trim();
                documentCrawler.setBody(bodyString);
            }
            //--------------------------------------------------------------------------------------------------------//
            Elements dateTimeElements = bodyElement.select("div.block_timer.left.txt_666");
            if (dateTimeElements != null) {
                //Thứ hai, 4/4/2016 | 08:26 GMT+7
                String dateTimeString = dateTimeElements.text().trim();
                dateTimeString = dateTimeString.substring(dateTimeString.indexOf(",") + 1, dateTimeString.indexOf("|")).trim();
                DateFormat sourceDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat targetDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = sourceDateFormat.parse(dateTimeString);
                dateTimeString = targetDateFormat.format(date).toString();
                documentCrawler.setDateTime(dateTimeString);
            }
            //--------------------------------------------------------------------------------------------------------//
            Elements tagElements = bodyElement.select("div.block_tag.width_common.space_bottom_20");
            if (tagElements != null) {
                Elements aElements = tagElements.select("a");
                if (aElements != null) {
                    StringBuffer buffer = new StringBuffer();
                    for (Element element : aElements) {
                        buffer.append(element.text().trim());
                        buffer.append(",");
                    }
                    if (buffer.length() > 0) {
                        buffer.deleteCharAt(buffer.length() - 1);
                    }
                    documentCrawler.setTags(buffer.toString());
                }
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
