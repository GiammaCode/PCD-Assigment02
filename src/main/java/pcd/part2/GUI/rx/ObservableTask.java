package pcd.part2.GUI.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObservableTask {

    public Observable<HashMap<String, Integer>> getResult(String url, String word, int depth, HashMap<String, Integer> result) {
        return Observable.defer(() -> Observable.just(crawlAndCount(url, word, depth, result)))
                .subscribeOn(Schedulers.io());
    }

    private HashMap<String, Integer> crawlAndCount(String entrypoint, String word, int depth, HashMap<String, Integer> result) {
        Queue<String> queue = new LinkedList<>();
        queue.offer(entrypoint);

        ConcurrentHashMap<String, Integer> visited = new ConcurrentHashMap<>();

        /*
        Ho sostituito la ricorsione con un ciclo while che itera fino a raggiungere la profondità desiderata.
        Questo ciclo utilizza una coda per gestire gli URL da elaborare.
        **/
        while (!queue.isEmpty() && depth >= 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String currentUrl = queue.poll();
                if (!visited.containsKey(currentUrl)) {
                    visited.put(currentUrl, 1);
                    int wordCount = crawl(currentUrl, word);
                    result.put(currentUrl, wordCount);

                    if (depth > 0) {
                        List<String> subLinks = extractSubLinks(currentUrl);
                        for (String sublink : subLinks) {
                            queue.offer(sublink);
                        }
                    }
                }
            }
            depth--;
        }
        return result;
    }

    /*
    Il metodo crawl ora recupera il contenuto di un URL in modo sincrono ma può essere chiamato concorrentemente
    da più thread grazie alla natura asincrona di RxJava.
    * */
    private int crawl(String url, String word) {
        int wordCount = 0;
        try {
            URLConnection urlConnection = new URI(url).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String w : words) {
                    if (w.toLowerCase().equals(word)) {
                        wordCount++;
                    }
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("[" + Thread.currentThread() + "]" + " connection failed: " + url);
        }
        return wordCount;
    }

    /*
    recupera anche i sottolink in modo sincrono ma può essere eseguito concorrentemente.
    * */
    private List<String> extractSubLinks(String url) {
        List<String> subLinks = new LinkedList<>();
        try {
            URLConnection urlConnection = new URI(url).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
            Pattern pattern = Pattern.compile(regex);
            while ((line = bufferedReader.readLine()) != null) {
                Matcher m = pattern.matcher(line);
                while (m.find()) {
                    subLinks.add(m.group());
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("[" + Thread.currentThread() + "]" + " connection failed: " + url);
        }
        return subLinks;
    }
}