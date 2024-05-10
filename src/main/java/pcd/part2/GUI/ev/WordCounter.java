package pcd.part2.GUI.ev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import pcd.part2.Flag;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class WordCounter extends AbstractVerticle {
    private String word;

    List<String> receivedList;

    String receivedJsonList;

    ObjectMapper objectMapper = new ObjectMapper();

    HashMap<String,Integer> result;

    public WordCounter(String word, HashMap<String,Integer> result){
        this.word=word;
        this.result=result;
    }

    public void start(Promise<Void> startPromise) {
        log("started word counter");
        EventBus eb = this.getVertx().eventBus();
        eb.consumer("my-topic", message -> {
            receivedJsonList = (String) message.body();

            try {
                receivedList = objectMapper.readValue(receivedJsonList, new TypeReference<List<String>>(){});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            for (String element : receivedList) {
                Future<Integer> future = getVertx().executeBlocking(() -> {
                    log(element);
                    return countWord(element,word);
                }).onComplete((nWord)->{
                        result.put(element,nWord.result());
                });
            }
        });
        log("waiting for links");
        startPromise.complete();
    }

    private void log(String msg) {
        System.out.println("[REACTIVE AGENT #1][" + Thread.currentThread() + "] " + msg);
    }

    private int countWord(String entryPoint, String word) {

        int wordCount = 0;
        String line;
        try {


            URL url = new URL(entryPoint);
            //open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);

                String[] words = line.split(" ");
                for (String w : words) {
                    wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                }
            }
            bufferedReader.close();

            //MA SE IO HO MESSO hasToFindSublink = false analizzo una content vuoto? che succede?
        } catch (Exception e) {
            System.out.println("Impossibile connettersi a " + entryPoint);
        }
        log(String.valueOf(wordCount));
        return wordCount;

    }
}

