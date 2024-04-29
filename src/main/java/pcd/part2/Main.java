package pcd.part2;



import pcd.part2.virtualThread.CrawlerVT;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        //entry point to start
        //String URL = "https://www.facebook.com/";
        String URL = "https://www.rugbyforli.net/about/";
        //word to find and count occurrences
        String wordToFind = "storia";
        //dept to search, depth = 0 to count only entrypoint
        int depth = 0;

        //Report report  = RecursiveCounter.getWordOccurrences(URL, wordToFind, depth);
        Report report  = CrawlerVT.getWordOccurrences(URL, wordToFind, depth);

        //Decomment to get a report.txt
        //report.getTxtReport();
        report.logResult();

    }
}
