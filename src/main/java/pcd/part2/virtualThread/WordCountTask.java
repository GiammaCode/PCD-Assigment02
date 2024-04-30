package pcd.part2.virtualThread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCountTask implements Runnable {
    private String entryPoint;
    private String word;
    private int depth;
    private List<Thread> threads = new LinkedList<>();

    List<String> subLinks = new LinkedList<>();
    Pattern pattern;
    private HashMap<String, Integer> result;

    public WordCountTask(String entryPoint, String word, int depth, HashMap<String, Integer> result, Pattern pattern) {
        this.entryPoint = entryPoint;
        this.word = word;
        this.depth = depth;
        this.result = result;
        this.pattern = pattern;
    }

    @Override
    public void run() {
        int wordCount = 0;
        subLinks.clear();
        String line;
        StringBuilder content = new StringBuilder();
        try {
            URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

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

            if (depth > 0) {
                this.depth = depth - 1;
                System.out.println(depth);
                for (String link : subLinks) {
                    Thread vt = Thread.ofVirtual().unstarted(new WordCountTask(link, word, this.depth, result, pattern));
                    threads.add(vt);
                    vt.start();
                }

                result.put(entryPoint, wordCount);

                for (Thread thread : threads) {

                    thread.join();

                }

            }
        } catch (Exception e) {
            System.out.println("Impossibile connettersi a " + entryPoint);
        }
    }
}
