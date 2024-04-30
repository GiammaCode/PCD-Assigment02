package pcd.part2.vertX;

import io.vertx.core.AbstractVerticle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;

public class CountWordVerticle extends AbstractVerticle {
    String entryPoint;
    String word;
    HashMap<String, Integer> result;

    public CountWordVerticle(String entryPoint, String word, HashMap<String, Integer> result) {
        this.entryPoint = entryPoint;
        this.word = word;
        this.result = result;
    }

    public void start(){
        int wordCount = 0;
        String line;
        try {
            URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String w : words) {
                    wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("[WORD COUNT TASK] impossible to connect :" + entryPoint);
            //e.printStackTrace();
        }
        result.put(entryPoint, wordCount);
    }

}

