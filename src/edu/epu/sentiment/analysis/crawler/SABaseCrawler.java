package edu.epu.sentiment.analysis.crawler;

import edu.epu.sentiment.analysis.utils.SAFile;
import edu.epu.sentiment.analysis.utils.SALog;
import edu.epu.sentiment.analysis.utils.SATime;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by duong on 3/29/16.
 */
public abstract class SABaseCrawler implements Runnable, SADelegateCrawler {

    public static String rootDir = SAFile.dir + File.separator + "res";
    public static long SLEEP_PER_PARENT_URL = SATime.millisecondsInSeconds(5);
    public static long SLEEP_PER_CHILD_URL = SATime.millisecondsInSeconds(5);

    protected ArrayList<SAGroupCrawler> groups;
    protected long sleepTime;
    protected boolean running;
    protected String directory;

    public SABaseCrawler(long sleepTime, String[] folders) {
        this.sleepTime = sleepTime;
        groups = new ArrayList<SAGroupCrawler>();
        running = true;
        createDirectories(folders);
    }

    public ArrayList<SAGroupCrawler> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<SAGroupCrawler> groups) {
        this.groups = groups;
    }

    public abstract ArrayList<String> getChildUrls(String parentUrl);

    public abstract SADocumentCrawler getDocFromUrl(String url);

    public void createDirectories(String[] folders) {
        directory = rootDir;
        for (String folder : folders) {
            directory += File.separator;
            directory += folder;
            File file = new File(directory);
            if (file.exists() == false) {
                if (file.mkdir() == true) {
                    SALog.log("MKDIR", "Created folder " + directory);
                }
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            for (SAGroupCrawler group : groups) {
                for (String parentUrl : group.getParentUrls()) {
                    for (String childUrl : getChildUrls(parentUrl)) {
                        getDocFromUrl(childUrl);
                        try {
                            SALog.log("SLEEP", "Sleep " + SLEEP_PER_CHILD_URL + " ms per child url.");
                            Thread.sleep(SLEEP_PER_CHILD_URL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        SALog.log("SLEEP", "Sleep " + SLEEP_PER_PARENT_URL + " ms per parent url.");
                        Thread.sleep(SLEEP_PER_PARENT_URL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                SALog.log("SLEEP", sleepTime + " ms");
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void execute() {
        new Thread(this).start();
    }
}
