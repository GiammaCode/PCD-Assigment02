package pcd.part2.cli.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.part2.Report;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerRx {
    public Report getWordOccurrences(String entrypoint, String word, int depth){
        List<String> subLinks = new ArrayList<>();
        getSubLinks(entrypoint,depth,subLinks);

        Observable<Pair<String, Integer>> observable = Observable
                .fromIterable(subLinks)
                .concatMap(link -> Observable
                        .just(link)
                        .delay(200, TimeUnit.MILLISECONDS)
                        .map(linkToCheck -> searchWordInLink(linkToCheck, word))
                        .subscribeOn(Schedulers.io())
                );

        HashMap<String, Integer> map = new HashMap<>();
        observable.subscribe(
                result -> {
                    if (result != null) {
                        map.put(result.getKey(), result.getValue());
                        //System.out.println(Thread.currentThread() + " Link: " + result.getKey() + ", Occorrenze di " + word + " " + result.getValue());
                    }
                },
                Throwable::printStackTrace,
                () -> {
                    System.out.println("Ricerca completata.");
                }
        );

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Report(word, map);
    }

    private static Pair<String, Integer> searchWordInLink(String link, String wordToFind) {
        int wordCount = 0;
        try {
            URLConnection urlConnection = new URI(link).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String w : words) {
                    if (w.toLowerCase().equals(wordToFind)) {
                        wordCount++;
                    }
                }
            }
            bufferedReader.close();
            return new Pair<>(link, wordCount);
        } catch (Exception e) {
            System.out.println("[" + Thread.currentThread() + "]" + " connection failed: " + link);
            return null;
        }
    }
    private void getSubLinks(String entryPoint, int depth, List<String> subLinks) {
        String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);
        String line;
        StringBuilder content = new StringBuilder();
        try {
            URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                if(depth>0) {
                    content.append(line).append("\n");
                }
            }
            bufferedReader.close();
            subLinks.add(entryPoint);
            Matcher m = pattern.matcher(content);
            while (m.find()) {
                getSubLinks(m.group(),depth--,subLinks);
            }
        } catch (Exception e) {
            System.out.println("Impossibile connettersi a " + entryPoint);
        }
    }

}
