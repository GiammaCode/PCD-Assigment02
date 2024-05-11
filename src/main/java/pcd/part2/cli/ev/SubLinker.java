package pcd.part2.cli.ev;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SubLinker extends AbstractVerticle {
        private String entrypoint;

        private List<String> subLinks= new LinkedList<>();

        private Integer depth;

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonList;

        private Pattern pattern;
        public SubLinker(String entrypoint, Integer depth, Pattern pattern){
            this.entrypoint=entrypoint;
            this.depth=depth;
            this.pattern=pattern;

        }

        public void start() {
            log("started subLinkes");
            EventBus eb = this.getVertx().eventBus();
            Future<List<String>> future = getVertx().executeBlocking(
                    () -> subLinks(entrypoint,depth,subLinks)).onComplete((result)-> {
                        try {
                            jsonList = objectMapper.writeValueAsString(result.result());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        ;
                        eb.publish("my-topic", jsonList);
                    }
                    );

        }

        private void log(String msg) {
            System.out.println("[REACTIVE AGENT #2]["+Thread.currentThread()+"] " + msg);
        }

    private List<String> subLinks(String entryPoint, int depth, List<String> subLinks) {
            recursiveSearchSubLinks(entryPoint,depth,subLinks);
            return subLinks;
    }

    private void recursiveSearchSubLinks(String myLink, int depth, List<String> subLinks){
        String line;
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(myLink);
            //open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) content.append(line).append("\n");
            bufferedReader.close();
            subLinks.add(myLink);

            if(depth>0) {
                // Creiamo un ExecutorService per eseguire i compiti in modo parallelo
                ExecutorService executorService = Executors.newFixedThreadPool(10);
                Matcher m = this.pattern.matcher(content);
                while (m.find()) subLinks.add(m.group());
                for (String link : subLinks) {
                    executorService.execute(() -> subLinks(link, depth - 1, subLinks));
                }
                // Ferma l'ExecutorService dopo che tutti i compiti sono stati completati
                executorService.shutdown();
            }

        } catch (Exception e) {
            System.out.println("Impossibile connettersi a " + myLink);
        }
    }
    }