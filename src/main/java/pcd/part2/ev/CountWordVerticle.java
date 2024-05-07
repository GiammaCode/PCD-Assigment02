package pcd.part2.ev;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

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

        Future<Integer> future = this.getVertx().executeBlocking(()-> {
            result.put("prova", 5);
            return 50;
        });


    }

}

