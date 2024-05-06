package pcd.part2.vertX;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import pcd.part2.virtualThread.WordCountTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountWordVerticle extends AbstractVerticle {
    String entryPoint;
    String word;
    HashMap<String, Integer> result;
    Pattern pattern;

    List<String> subLinks = new LinkedList<>();
    List<String> newSubLinks = new LinkedList<>();

    private boolean isFinished = false;
    private int depth;

    Flag flag;

    public CountWordVerticle(String entryPoint, String word, int depth,HashMap<String, Integer> result,Pattern pattern, Flag flag) {
        this.newSubLinks.add(entryPoint);
        this.word = word;
        this.result = result;
        this.pattern = pattern;
        this.flag = flag;
        this.depth=depth;
    }

    public void start() {

            if (depth > 0) {
                depth--;
                for (String link : subLinks) {

                    Future<Integer> future = getVertx().executeBlocking(() -> {
                        System.out.println("mid part" + link);
                        return extracted(link, word, depth);
                    });

                    future.onComplete((r) -> {
                        System.out.println(r);
                        result.put(link, r.result());
                        System.out.println(r.result());
                    });

                }
            } else {

                Future<Integer> future = this.getVertx().executeBlocking(() -> {
                    System.out.println("end part");
                    return extracted(newSubLinks.get(0), word, depth);
                }).onComplete((r) -> {
                    System.out.println(r);
                    result.put(newSubLinks.get(0), r.result());
                    flag.set();
                    isFinished = true;
                });
            }
    }

    private int extracted(String entryPoint, String word, int depth) {
        int wordCount = 0;
        String line;
        StringBuilder content = new StringBuilder();
        try {
                URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = bufferedReader.readLine()) != null) {
                    if(depth>0)
                        content.append(line + "\n");
                    String[] words = line.split(" ");
                    for (String w : words) {
                        wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                    }
                }
            bufferedReader.close();
                if(depth>0) {
                    Matcher m = pattern.matcher(content);
                    while (m.find()) {
                        newSubLinks.add(m.group());
                    }
                }

        } catch (Exception e) {
            System.out.println("Impossibile connettersi a " + entryPoint);
        }
        System.out.println(wordCount);
        return wordCount;
    }

}

