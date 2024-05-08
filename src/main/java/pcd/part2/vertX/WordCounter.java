package pcd.part2.vertX;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class WordCounter extends AbstractVerticle {
    private String word;

    ObjectMapper objectMapper = new ObjectMapper();

    HashMap<String,Integer> result = new HashMap<>();

    Flag flag;


    public WordCounter(String word, HashMap<String,Integer> result,Flag flag){
        this.word=word;
        this.result=result;
        this.flag=flag;
    }

    public void start(Promise<Void> startPromise) {
        log("started word counter");
        EventBus eb = this.getVertx().eventBus();
        eb.consumer("my-topic", message -> {
            String receivedJsonList = (String) message.body();
            List<String> receivedList;
            try {
                receivedList = objectMapper.readValue(receivedJsonList, new TypeReference<List<String>>(){});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            int size = receivedList.size();
            AtomicInteger count= new AtomicInteger();
            for (String element : receivedList) {
                Future<Integer> future = getVertx().executeBlocking(() -> {
                    return countWord(element,word);
                }).onComplete((nWord)->{
                        count.getAndIncrement();
                        result.put(element,nWord.result());
                        if (count.get() == size)
                            flag.set();
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

            URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
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
        return wordCount;

    }
}

