package pcd.part2.GUI.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.part2.Crowler;
import pcd.part2.Report;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerRx implements Crowler {
    HashMap<String, Integer> result = new HashMap<>();

    public void getWordOccurrences(String entrypoint, String word, int depth) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        depth++;

        crawlRecursive(entrypoint, word, depth, result)
                .subscribeOn(Schedulers.io())  //fa scegliere allo scheduler I/O la gestione dei Thread da un pool
                .doFinally(() -> latch.countDown()) // Chiamiamo countDown() quando il flusso è completato
                .subscribe();

        latch.await(); // Attendiamo che il flusso sia completato
        System.out.println("finished");

        //return new Report(word, result);
    }

    private Observable<List<String>> crawlRecursive(String url, String word, int depth, HashMap<String, Integer> result) {
        if (depth == 0) {
            return Observable.just(new ArrayList<>());
        }

        return Observable.fromCallable(() -> {
            String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
            Pattern pattern = Pattern.compile(regex);
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
                //TO DO: è un operazione critica ????
                result.put(url,wordCount);
                //System.out.println(Thread.currentThread() + " analyzed: " + url + " count: " + wordCount + " depth: " + depth);
                reader.close();

                // Estrai i link usando un'espressione regolare
                String pageContent = content.toString();
                int index = 0;
                //search sublinks
                Matcher m = pattern.matcher(content);
                while(m.find()) {
                    subLinks.add(m.group());
                }
            }  catch (Exception e) {
                System.out.println("[" + Thread.currentThread() + "]" + " connection failed: " + url);
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
                                        crawlRecursive(subLink, word,depth - 1, result))
                                .reduce(new ArrayList<>(), (acc, list) -> {
                                    acc.addAll(list);
                                    return acc;
                                })
                        )
        );
    }

    public HashMap<String,Integer> getMap(){
        return result;
    }

    @Override
    public void stop() {

    }
}