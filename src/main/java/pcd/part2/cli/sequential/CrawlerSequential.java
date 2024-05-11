package pcd.part2.cli.sequential;

import pcd.part2.Report;
import pcd.part2.cli.ev.SubLinker;
import pcd.part2.cli.rx.CrawlerRx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
QUESTO CODICE NON è NE RICORSIVO, NE ASINCRONO, NE CONCORRENTE, mi serve solo per testare le performance da
aggiungere nella relazione.
Funziona solo con profondita 1
**/
public class CrawlerSequential {
    public static void main(String[] args) throws IOException {
        String URL = "https://www.rugbyforli.net/";
        String wordToFind = "il";
        int depth = 1;

        long startTime = System.currentTimeMillis();

        CrawlerSequential crawler = new CrawlerSequential();
        Report report = crawler.getWordOccurrences(URL, wordToFind, depth);

        System.out.println("[TIME OF EXECUTION] : " + (System.currentTimeMillis()-startTime) + " Milliseconds");

        report.logResult();
    }
    public Report getWordOccurrences(String entryPoint, String word, int depth) throws IOException {
        int wordCount = 0;
        HashMap<String, Integer> result = new HashMap<>();
        for (String link : getAllLinks(entryPoint)){
            //System.out.println(link);
            try {
                URLConnection urlConnection = new URI(link).toURL().openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                wordCount = 0;

                while ((line = bufferedReader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String w : words) {
                        wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                    }
                }
                bufferedReader.close();
            } catch (Exception e) {
                System.out.println(Thread.currentThread() + " impossible to connect : " + link);
            }
            //System.out.println(link + " "  + wordCount);
            result.put(link,wordCount);
        }

        return new Report(word, result);
    }

    //far ritornare una Lista<String>
    public List<String> getAllLinks(String entryPoint){
        String regex="\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);
        StringBuilder content = new StringBuilder();
        List<String> sublink = new ArrayList<>();
        try
        {
            URL url = new URL(entryPoint);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Matcher m = pattern.matcher(content);
        while(m.find()) {
           sublink.add(m.group());
        }
        return sublink;
    }
}
