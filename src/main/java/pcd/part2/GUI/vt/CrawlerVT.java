package pcd.part2.GUI.vt;

import pcd.part2.Crowler;
import pcd.part2.Flag;
import pcd.part2.MyMonitor;

import java.util.HashMap;
import java.util.regex.Pattern;


public class CrawlerVT implements Crowler {
    static HashMap<String, Integer> result = new HashMap<>();
    Flag stopFlag = new Flag();

    public void getWordOccurrences(String entryPoint, String word, int depth) throws InterruptedException {
        String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);
        MyMonitor monitor = new MyMonitor();


        Thread vt = Thread.ofVirtual().unstarted(new RecursiveWordCountTask(entryPoint,word, depth, result,pattern,monitor, stopFlag));
        vt.start();
        vt.join();
    }
    public HashMap<String,Integer> getMap(){
        return result;
    }

    public void stop(){
        stopFlag.set();
        //System.out.println(flag);
    }

    @Override
    public void reset() {
        stopFlag.reset();
    }
}
