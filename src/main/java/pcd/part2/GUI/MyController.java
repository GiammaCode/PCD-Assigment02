package pcd.part2.GUI;

import pcd.part2.Crowler;

import java.util.HashMap;

public class MyController {
    private MyModel model;
    private static Crowler crowler;
    public MyController(MyModel model, Crowler crowler){

        this.model = model;
        this.crowler=crowler;
    }

    public static void stop() {
    }

    public static HashMap<String, Integer> getMap() {
        return crowler.getMap();
    }


    public static void reset() {
        crowler.reset();
    }

    public static void getWordOccurrences(String link, String word, int depth) throws InterruptedException {
        crowler.getWordOccurrences(link,word,depth);
    }
}
