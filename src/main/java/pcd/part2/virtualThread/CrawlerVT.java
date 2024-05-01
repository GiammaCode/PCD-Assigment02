package pcd.part2.virtualThread;

import pcd.part2.Report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


public class CrawlerVT {
    public static Report getWordOccurrences(String entryPoint, String word, int depth) throws InterruptedException {
        HashMap<String, Integer> result = new HashMap<>();
        String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);

        Thread vt = Thread.ofVirtual().unstarted(new RecursiveWordCountTask(entryPoint,word, depth, result,pattern));
        vt.start();
        vt.join();

        return new Report(word, result);
    }

    private static List<String> getSubLinks(List<String> links) throws InterruptedException {
        String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);
        List<String> sublinks = new LinkedList<>();
        System.out.println("[analyze new list of web page]");

        var list = new ArrayList<Thread>();
        for (String l : links) {
            Thread vt = Thread.ofVirtual().unstarted(new FindSubLinksTask(l, sublinks, pattern));
            vt.start();
            list.add(vt);
        }
        list.forEach(t -> {
            try {
                t.join();
            } catch (Exception ex) {};
        });

        return sublinks;
    }
}
