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

    Flag flag;

    public CountWordVerticle(String entryPoint, String word, HashMap<String, Integer> result,Pattern pattern, Flag flag) {
        this.entryPoint = entryPoint;
        this.word = word;
        this.result = result;
        this.pattern = pattern;
        this.flag = flag;
    }

    public void start(){


        Future<Integer> future = this.getVertx().executeBlocking(()-> {
            int wordCount = 0;
            String line;
            StringBuilder content = new StringBuilder();
            try {
                URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                System.out.println("ciao");
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line + "\n");
                    String[] words = line.split(" ");
                    for (String w : words) {
                        wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                    }
                }

                bufferedReader.close();

                Matcher m = this.pattern.matcher(content);
                while (m.find()) {
                    this.subLinks.add(m.group());
                }

            } catch (Exception e) {
                System.out.println("Impossibile connettersi a " + entryPoint);
            }

            return wordCount;
        });

        future.onComplete((r)->{
            result.put(entryPoint,r.result());
            flag.set();
            System.out.println(r.result());
        });



    }

}

