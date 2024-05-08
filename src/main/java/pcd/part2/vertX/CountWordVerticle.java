package pcd.part2.vertX;

import io.netty.util.Timeout;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.sql.Time;
import java.util.*;
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
       this.entryPoint=entryPoint;
        this.word = word;
        this.result = result;
        this.pattern = pattern;
        this.flag = flag;
        this.depth=depth;
    }

    public void start() throws InterruptedException {
        //finche ci sono sotto link o non è finito
        //while(!true || !false) quindi lui rimane sempre nel while
            //questo lo aggiunto io perchè non esece mai dal ciclo while
            //perche te setti la flag dentro la future ma la future non la completa mai
            //perchè non arrivi mai a return wordCount
            //entro nelle altre pagine
            subLinks.addAll(SubLinks(entryPoint,depth));
            for (String link : subLinks) {
                Future<Integer> future = getVertx().executeBlocking(() -> {
                    System.out.println("mid part" + link);
                    return countWord(link, word);
                }).onComplete((r) -> {
                    System.out.println(r);
                    result.put(link, r.result());
                    System.out.println(r.result());
                });
            }
        Thread.sleep(100000);
        flag.set();
    }


    private int countWord(String entryPoint, String word) {

        int wordCount = 0;
        String line;
        try {

            URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);

                String[] words = line.split(" ");
                for (String w : words) {
                    wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                }
            }
            System.out.println("while finito");
            bufferedReader.close();

            //MA SE IO HO MESSO hasToFindSublink = false analizzo una content vuoto? che succede?
        } catch (Exception e) {
            System.out.println("Impossibile connettersi a " + entryPoint);
        }
        System.out.println(wordCount);
        return wordCount;

    }
    public Collection<String> SubLinks(String entryPoint, int depth) {
        String line;
        StringBuilder content = new StringBuilder();
        try {

            URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                if(depth>0) {
                    content.append(line).append("\n");
                }
            }
            System.out.println("while finito");
            bufferedReader.close();

            subLinks.add(entryPoint);

            //MA SE IO HO MESSO hasToFindSublink = false analizzo una content vuoto? che succede?
            Matcher m = this.pattern.matcher(content);
            while (m.find()) {
                System.out.println(m.group());
                SubLinks(m.group(),depth--);
            }

        } catch (Exception e) {
            System.out.println("Impossibile connettersi a " + entryPoint);
        }
        return subLinks;
    }

}


