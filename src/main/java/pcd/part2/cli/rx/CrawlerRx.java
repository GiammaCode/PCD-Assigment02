package pcd.part2.cli.rx;

import pcd.part2.Report;

import java.util.HashMap;

public class CrawlerRx {
    public static Report getWordOccurrences(String url, String word, int depth) throws InterruptedException {
        HashMap<String, Integer> result = new HashMap<>();
        ObservableTask myObservable = new ObservableTask();

        myObservable.getResult(url, word, depth, result).subscribe();

        Thread.sleep(30000);

        return new Report(word, result);
    }
}
