package pcd.part2.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObservableTask {
    public Observable<HashMap<String, Integer>> getResult(String url, String word,
                                                          int depth, HashMap<String, Integer> result) {
        List<String> subLinks = new LinkedList<>();

        //creo un flusso -> creo un flusso per ogni consumer (observable)
        //per ogni uno mando la callback di crawlAndCount
        return Observable.defer(() -> Observable.just(crawlAndCount(url, word, depth, result, subLinks)))
                .subscribeOn(Schedulers.io());

    }

    private  HashMap<String, Integer> crawlAndCount(String entrypoint, String word, int depth,
                                                    HashMap<String, Integer> result, List<String> subLinks) {
        String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);

        int wordCount = 0;
        String line;

        StringBuilder content = new StringBuilder();

        try {
            //open connection
            URLConnection urlConnection = new URI(entrypoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
                String[] words = line.split(" ");
                for (String w : words) {
                    wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                }
            }
            bufferedReader.close();
            result.put(entrypoint, wordCount);
            //recursive
            if (depth > 0) {
                //search sublinks
                Matcher m = pattern.matcher(content);
                while(m.find()) {
                    subLinks.add(m.group());
                }
                depth --;
                for (String sublink : subLinks) {
                    crawlAndCount(sublink, word, depth, result, subLinks);
                }
            }
        }
        catch (Exception e){
            System.out.println("[" + Thread.currentThread() + "]" + " connection failed: " + entrypoint);
        }

        return result;
    }
}
