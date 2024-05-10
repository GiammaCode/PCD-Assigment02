package pcd.part2.cli.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.part2.Report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerRx {
    public Report getWordOccurrences(String entrypoint, String word, int depth) throws InterruptedException {
        List<String> subLinks = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        crawlRecursive(entrypoint, word, depth)
                .subscribeOn(Schedulers.io())
                .doFinally(() -> latch.countDown()) // Chiamiamo countDown() quando il flusso è completato
                .subscribe(links -> {
                    subLinks.addAll(links);
                    //System.out.println(myList);
                });

        latch.await(); // Attendiamo che il flusso sia completato

        HashMap<String, Integer> mappetta = new HashMap<>();

        Observable<Pair<String, Integer>> observable = Observable
                .fromIterable(subLinks)
                .concatMap(link -> Observable
                        .just(link)
                        .delay(200, TimeUnit.MILLISECONDS)
                        .map(linkToCheck -> searchWordInLink(linkToCheck, word, mappetta))
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
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Report(word, map);
    }

    private static Pair<String, Integer> searchWordInLink(String link, String wordToFind, HashMap<String, Integer> mapResult) {
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
            //System.out.println(Thread.currentThread() + " insert: " + link + " ==> " + wordCount );
            mapResult.put(link, wordCount);
            return new Pair<>(link, wordCount);
        } catch (Exception e) {
            System.out.println("[" + Thread.currentThread() + "]" + " connection failed: " + link);
            return null;
        }
    }
    private Observable<List<String>> crawlRecursive(String url, String word, int depth) {
        System.out.println(Thread.currentThread() + "analize: " + url);
        if (depth == 0) {
            List<String> subLinks = new ArrayList<>();
            subLinks.add(url);
            return Observable.just(subLinks);
        }

        return Observable.fromCallable(() -> {
            int wordCount = 0;
            List<String> subLinks = new ArrayList<>();
            HttpURLConnection connection = null;
            try {
                URL linkUrl = new URL(url);
                connection = (HttpURLConnection) linkUrl.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                    String[] words = line.split(" ");
                    for (String w : words) {
                        wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                    }
                }
                reader.close();


                // Estrai i link usando un'espressione regolare
                String pageContent = content.toString();
                int index = 0;
                while ((index = pageContent.indexOf("<a href=\"", index)) != -1) {
                    int endIndex = pageContent.indexOf("\"", index + 9);
                    if (endIndex != -1) {
                        String subLink = pageContent.substring(index + 9, endIndex);
                        subLinks.add(subLink);
                        index = endIndex;
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return subLinks;
        }).subscribeOn(Schedulers.io())
                .flatMap(subLinks ->
                Observable.just(subLinks)
                        .concatWith(Observable.fromIterable(subLinks)
                                .subscribeOn(Schedulers.io())
                                .flatMap(subLink ->
                                        crawlRecursive(subLink, word,depth - 1))
                                .reduce(new ArrayList<>(), (acc, list) -> {
                                    acc.addAll(list);
                                    return acc;
                                })
                        )
        );
    }

}