package pcd.part2;

import java.util.HashMap;

public interface Crowler {
    public void getWordOccurrences(String entryPoint, String word, int depth) throws InterruptedException;
    public HashMap<String, Integer> getMap();
    public void stop();
}
