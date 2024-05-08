package pcd.part2.rx;

import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.part2.Report;

import java.util.HashMap;

public class CrawlerRx {
    public static Report getWordOccurrences(String url, String word, int depth) throws InterruptedException {
        HashMap<String, Integer> result = new HashMap<>();
        ObservableTask myObservable = new ObservableTask();


        // Ottieni un Observable per il conteggio delle occorrenze
        myObservable.getResult(url, word, depth, result)
                // Ascolta sul thread di I/O
                .subscribeOn(Schedulers.io())
                // Aspetta fino a quando il conteggio è completato
                .blockingSubscribe();


        return new Report(word, result);
    }
}
