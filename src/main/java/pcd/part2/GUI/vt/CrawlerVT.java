package pcd.part2.GUI.vt;

import pcd.part2.Crowler;
import pcd.part2.Flag;
import pcd.part2.MyMonitor;
import pcd.part2.Report;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;


public class CrawlerVT implements Crowler {
    static HashMap<String, Integer> result = new HashMap<>();
    Flag flag= new Flag();

    public void getWordOccurrences(String entryPoint, String word, int depth) throws InterruptedException {
        String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);
        MyMonitor monitor = new MyMonitor();


        Thread vt = Thread.ofVirtual().unstarted(new RecursiveWordCountTask(entryPoint,word, depth, result,pattern,monitor,flag));
        vt.start();
        vt.join();
    }
    public HashMap<String,Integer> getMap(){
        return result;
    }

    public void stop(){
        flag.set();
    }
}
