package pcd.part2.virtualThread;

import pcd.part2.Report;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


public class CrawlerVT {
    public static Report getWordOccurrences(String entryPoint, String word, int depth) throws InterruptedException {
        String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);
        HashMap<String, Integer> result = new HashMap<>();
            Thread vt = Thread.ofVirtual().unstarted(new WordCountTask(entryPoint,word, depth, result,pattern));
            vt.start();
            vt.join();


        return new Report(word, result);
    }

}
