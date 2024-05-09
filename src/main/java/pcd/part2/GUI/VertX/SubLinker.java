package pcd.part2.GUI.VertX;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
            Future<List<String>> future = getVertx().executeBlocking(() -> {
                return  SubLinks(entrypoint,depth,subLinks);
                    }).onComplete((result)-> {
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

    private List<String> SubLinks(String entryPoint, int depth,List<String> subLinks) {

        String line;
        StringBuilder content = new StringBuilder();
        try {

            URL url = new URL(entryPoint);
            //open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                if(depth>0) {
                    content.append(line).append("\n");
                }
            }
            bufferedReader.close();

            subLinks.add(entryPoint);

            //MA SE IO HO MESSO hasToFindSublink = false analizzo una content vuoto? che succede?
            Matcher m = this.pattern.matcher(content);
            while (m.find()) {
                SubLinks(m.group(),depth--,subLinks);
            }

        } catch (Exception e) {
            System.out.println("Impossibile connettersi a " + entryPoint);
        }
        return subLinks;
    }
    }