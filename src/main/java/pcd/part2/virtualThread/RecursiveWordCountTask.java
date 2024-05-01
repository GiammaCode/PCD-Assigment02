package pcd.part2.virtualThread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecursiveWordCountTask implements Runnable {
    private String entryPoint;
    private String word;
    private int depth;
    private List<Thread> threads = new LinkedList<>();
    List<String> subLinks = new LinkedList<>();
    Pattern pattern;
    private HashMap<String, Integer> result;

    public RecursiveWordCountTask(String entryPoint, String word, int depth, HashMap<String, Integer> result, Pattern pattern) {
        this.entryPoint = entryPoint;
        this.word = word;
        this.depth = depth;
        this.result = result;
        this.pattern = pattern;
    }

    @Override
    public void run() {
        int wordCount = 0;
        String line;
        StringBuilder content = new StringBuilder();
        try {
            //open connection
            URLConnection urlConnection = new URI(this.entryPoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            //count word occurrence
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
                String[] words = line.split(" ");
                for (String w : words) {
                    wordCount = w.toLowerCase().equals(this.word) ? wordCount + 1 : wordCount;
                }
            }
            bufferedReader.close();
            this.result.put(this.entryPoint, wordCount);
            //start recursive part
            if (this.depth > 0) {
                //search sublinks
                Matcher m = this.pattern.matcher(content);
                while(m.find()) {
                    this.subLinks.add(m.group());
                }
                this.depth--;
                for (String sublink : subLinks) {
                    Thread vt = Thread.ofVirtual()
                            .unstarted(new RecursiveWordCountTask(sublink, this.word, this.depth, this.result, this.pattern));
                    this.threads.add(vt);
                    vt.start();
                }
                for (Thread thread : this.threads) {
                    thread.join();
                }
            }

        }
        catch (Exception e) {
            System.out.println("[" + Thread.currentThread() + "]" + " connection failed: " + this.entryPoint);
        }
    }
}
