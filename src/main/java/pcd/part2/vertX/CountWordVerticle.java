package pcd.part2.vertX;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

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
        //mancava entrypoint nel costruttore !!!!
        this.entryPoint = entryPoint;

        this.newSubLinks.add(entryPoint);
        this.word = word;
        this.result = result;
        this.pattern = pattern;
        this.flag = flag;
        this.depth=depth;
    }

    public void start() {
        //finche ci sono sotto link o non è finito
        //while(!true || !false) quindi lui rimane sempre nel while
        while (!newSubLinks.isEmpty() || !isFinished) {
            //pulisco sublink
            //sublink = newsublink
            System.out.println("[isfinished]" + isFinished);
            subLinks.clear();
            subLinks.addAll(newSubLinks);
            newSubLinks.clear();

            //questo lo aggiunto io perchè non esece mai dal ciclo while
            //perche te setti la flag dentro la future ma la future non la completa mai
            //perchè non arrivi mai a return wordCount
            isFinished = true;

            //entro nelle altre pagine
            if (depth > 0) {
                depth--;
                for (String link : subLinks) {

                    Future<Integer> future = getVertx().executeBlocking(() -> {
                        System.out.println("mid part" + link);
                        return extracted(link, word, subLinks,true);
                    });

                    future.onComplete((r) -> {
                        System.out.println(r);
                        result.put(link, r.result());
                        System.out.println(r.result());
                    });

                }
            }
            //controllo solo pagina principale
            else {
                Future<Integer> future = this.getVertx().executeBlocking(
                        () -> extracted(entryPoint, word, newSubLinks, false)
                );

                future.onComplete((r) -> {
                    System.out.println(r);
                    isFinished = true;
                    result.put(entryPoint, r.result());
                    System.out.println(r.result());
                });
            }
        }
    }


    private int extracted(String entryPoint, String word,List<String> newSubLinks, boolean hasToFindSublinks) {

        int wordCount = 0;
        String line;
        StringBuilder content = new StringBuilder();
        try {

            URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                if(hasToFindSublinks) content.append(line).append("\n");

                String[] words = line.split(" ");
                for (String w : words) {
                    wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                }
            }
            System.out.println("while finito");
            bufferedReader.close();

            //MA SE IO HO MESSO hasToFindSublink = false analizzo una content vuoto? che succede?
            Matcher m = this.pattern.matcher(content);
            while (m.find()) {
                System.out.println(m.group());
                newSubLinks.add(m.group());
            }

        } catch (Exception e) {
            System.out.println("Impossibile connettersi a " + entryPoint);
        }
        System.out.println(wordCount);
        return wordCount;
    }

}


