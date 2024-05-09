package pcd.part2.cli;

import pcd.part2.cli.ev.CrawlerVertX;
import pcd.part2.Report;
import pcd.part2.cli.vt.CrawlerVT;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
       // String URL = "https://scuola.eutampieri.eu/";
        String URL = "https://www.akwabaforli.com/";
        String wordToFind = "il";
        //depth = 0 to analyze only entrypoint page
        //depth = 2 ti bloccano
        int depth = 1;

        long startTime = System.currentTimeMillis();

        Report report  = CrawlerVT.getWordOccurrences(URL, wordToFind, depth);
       // Report report = CrawlerVertX.getWordOccurrences(URL, wordToFind, depth);

        System.out.println("[TIME OF EXECUTION] : " + (System.currentTimeMillis()-startTime) + " Milliseconds");

        //Decomment to get a report.txt
        //report.getTxtReport();
        report.logResult();


    }
}