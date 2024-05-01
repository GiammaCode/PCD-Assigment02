package pcd.part2.virtualThread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
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
        //creo documento dove inserire html
        StringBuilder content = new StringBuilder();

        try {
            URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            while ((line = bufferedReader.readLine()) != null) {
                //aggiungo linea da buffer a documento
                content.append(line + "\n");

                //conto parole
                String[] words = line.split(" ");
                for (String w : words) {
                    wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                }

            }
            bufferedReader.close();
            result.put(entryPoint, wordCount);

            //ho contato le ricorrenze cerco i sottolink e li addo in una lista
            Matcher m = this.pattern.matcher(content);
            while (m.find()) {
                this.subLinks.add(m.group());
            }

            //ho una lista di sottolink e se depth > 1 entro nella ricorrenza
            if (depth > 0) {
                this.depth--;
                //System.out.println(depth);
                for (String link : subLinks) {
                    Thread vt = Thread.ofVirtual().unstarted(new RecursiveWordCountTask(link, word, this.depth, result, pattern));
                    threads.add(vt);
                    vt.start();
                }
                for (Thread thread : threads) {
                    thread.join();
                }
            }

        }
        catch (Exception e) {
            System.out.println("[" + Thread.currentThread() + "]" + " connection failed: " + entryPoint);
        }
    }
}
