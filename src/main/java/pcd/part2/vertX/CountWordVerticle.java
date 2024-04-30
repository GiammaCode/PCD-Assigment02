package pcd.part2.vertX;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
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
            return 50;
        });


    }

}

