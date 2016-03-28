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
public class SADoc implements SACrawlerCallback {

    public static final long sleep = 5000;
    private String url;
    private String topic;
    private String title;
    private String author;
    private String date;
    private String time;
    private String body;
    private ArrayList<String> tags;
    private SACrawlerCallback callback;

    public SADoc(String url, String topic) {
        this.url = url;
        this.topic = topic;
        tags = new ArrayList<String>();
    }

    public static Document getDocument(String url) throws IOException {
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public SACrawlerCallback getCallback() {
        return callback;
    }

    public void setCallback(SACrawlerCallback callback) {
        this.callback = callback;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void write(String folder) {
        String[] dates = date.split("/");
        StringBuffer subFolder = new StringBuffer();
        for (int i = dates.length - 1; i >= 0; i--) {
            subFolder.append(dates[i]);
            subFolder.append("-");
        }
        subFolder.deleteCharAt(subFolder.length() - 1);
        folder += File.separator + subFolder;
        SALog.log("FOLDER", folder);
        File subFile = new File(folder);
        if (subFile.exists() == false) {
            subFile.mkdir();
        }
        String file = folder + File.separator + SAString.normalizeTitle(title) + ".txt";
        ArrayList<String> strings = new ArrayList<String>();
        strings.add(title);
        strings.add(SAFile.line);
        strings.add(author);
        strings.add(SAFile.line);
        strings.add(date);
        strings.add(SAFile.line);
        strings.add(time);
        strings.add(SAFile.line);
        strings.add(body);
        strings.add(SAFile.line);
        strings.addAll(tags);
        SAFile.writeStringsToFile(strings, file);
    }

    public void print() {
        SALog.log("URL", title);
        SALog.log("TOPIC", topic);
        SALog.log("TITLE", title);
        SALog.log("AUTHOR", author);
        SALog.log("DATE", date);
        SALog.log("TIME", time);
        SALog.log("BODY", body);
    }

    @Override
    public void execute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.execute();
                try {
                    SALog.log("SLEEP", sleep + " ms");
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
