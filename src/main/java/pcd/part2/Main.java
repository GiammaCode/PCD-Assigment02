package pcd.part2;

import pcd.part2.vertX.CrawlerVertX;
import pcd.part2.virtualThread.CrawlerVT;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String URL = "https://finagricola.it/il-fresco/pomodori/pomodoro-ciliegino/";
        String wordToFind = "pomodoro";
        //depth = 0 to analyze only entrypoint page
        //depth = 2 ti bloccano
        int depth = 0;

        long startTime = System.currentTimeMillis();

        //Report report  = RecursiveCounter.getWordOccurrences(URL, wordToFind, depth);
        //Report report  = CrawlerVT.getWordOccurrences(URL, wordToFind, depth);
        Report report = CrawlerVertX.getWordOccurrences(URL, wordToFind, depth);

        System.out.println("[TIME OF EXECUTION] : " + (System.currentTimeMillis()-startTime) + "Milliseconds");

        //Decomment to get a report.txt
        //report.getTxtReport();
        report.logResult();


    }
}
