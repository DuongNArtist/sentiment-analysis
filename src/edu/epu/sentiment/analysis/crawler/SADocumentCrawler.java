package edu.epu.sentiment.analysis.crawler;

import edu.epu.sentiment.analysis.utils.SAFile;
import edu.epu.sentiment.analysis.utils.SALog;
import edu.epu.sentiment.analysis.utils.SAString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by duong on 3/29/16.
 */
public class SADocumentCrawler {

    public static final String DOCUMENT_EXTENSION = ".txt";
    private String url;
    private String title;
    private String author;
    private String dateTime;
    private String body;
    private String tags;
    private SADelegateCrawler callback;

    public SADocumentCrawler(String url) {
        this.url = url;
        title = "";
        author = "";
        dateTime = "";
        body = "";
        tags = "";
    }

    public static Document getDocumentFromUrl(String url) throws IOException {
        return Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .referrer("http://www.google.com")
                .timeout(12000)
                .followRedirects(true)
                .get();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public SADelegateCrawler getCallback() {
        return callback;
    }

    public void setCallback(SADelegateCrawler callback) {
        this.callback = callback;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void writeDocument(String folder) {
        String file = creatStorageFolder(folder) + File.separator + SAString.normalizeTitle(title) + DOCUMENT_EXTENSION;
        ArrayList<String> strings = new ArrayList<String>();
        strings.add(title);
        strings.add(SAFile.line);
        strings.add(author);
        strings.add(SAFile.line);
        strings.add(dateTime);
        strings.add(SAFile.line);
        strings.add(body);
        strings.add(SAFile.line);
        strings.add(tags);
        File docFile = new File(file);
        if (docFile.exists() == true) {
            docFile.delete();
        }
        SAFile.writeStringsToFile(strings, file);
    }

    public String creatStorageFolder(String rootFolder) {
        String[] dateStrings = dateTime.split(" ")[0].split("-");
        StringBuffer newFolder = new StringBuffer();
        for (int i = dateStrings.length - 1; i >= 0; i--) {
            newFolder.append(dateStrings[i]);
            newFolder.append("-");
        }
        newFolder.deleteCharAt(newFolder.length() - 1);
        rootFolder += File.separator + newFolder;
        File subFile = new File(rootFolder);
        if (subFile.exists() == false) {
            subFile.mkdir();
        }
        SALog.log("FOLDER", "Created folder: " + rootFolder);
        return rootFolder;
    }

    public void printDocument() {
        SALog.log("URL", url);
        SALog.log("TITLE", title);
        SALog.log("AUTHOR", author);
        SALog.log("DATETIME", dateTime);
        SALog.log("BODY", body);
        SALog.log("TAGS", tags);
    }

}
