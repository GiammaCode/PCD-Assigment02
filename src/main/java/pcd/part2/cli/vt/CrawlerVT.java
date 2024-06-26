package pcd.part2.cli.vt;

import pcd.part2.MyMonitor;
import pcd.part2.Report;

import java.util.HashMap;
import java.util.regex.Pattern;


public class CrawlerVT {
    public Report getWordOccurrences(String entryPoint, String word, int depth) throws InterruptedException {
        HashMap<String, Integer> result = new HashMap<>();
        String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);
        MyMonitor monitor = new MyMonitor();

        Thread vt = Thread.ofVirtual().unstarted(new RecursiveWordCountTask(entryPoint,word, depth, result,pattern,monitor));
        vt.start();
        vt.join();

        return new Report(word, result);
    }
}
