package pcd.part2;



import pcd.part2.virtualThread.CrawlerVT;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String URL = "https://www.rugbyforli.net/about/";
        String wordToFind = "storia";
        //depth = 0 to analyze only entrypoint page
        int depth = 0;

        //Vertx  vertx = Vertx.vertx();

        //Report report  = RecursiveCounter.getWordOccurrences(URL, wordToFind, depth);
        Report report  = CrawlerVT.getWordOccurrences(URL, wordToFind, depth);

        //Decomment to get a report.txt
        //report.getTxtReport();
        report.logResult();

    }
}
