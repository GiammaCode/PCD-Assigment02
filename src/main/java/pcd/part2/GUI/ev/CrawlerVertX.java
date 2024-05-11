package pcd.part2.GUI.ev;

import io.vertx.core.Vertx;
import pcd.part2.Crowler;
import pcd.part2.Flag;
import pcd.part2.Report;

import java.util.HashMap;
import java.util.regex.Pattern;


public class CrawlerVertX implements Crowler {
    Flag stopFlag= new Flag();
    private final HashMap<String, Integer> result = new HashMap<>();

    public void getWordOccurrences(String entryPoint, String word, int depth) throws InterruptedException {
        /*
        Un’applicazione Vert.x consiste di uno o più componenti chiamati Verticle. questi sono pezzi di
        codice che il motore di Vert.x esegue.
        Ogni verticle viene eseguito in maniera concorrente rispetto agli altri e non c’è uno stato che viene condiviso.
        Tradotto in parole semplici, si riesce a creare applicazioni multi-threaded senza dover gestire problematiche
        di concorrenza come la sincronizzazione o i lock tra thread.
        * */
        String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);
        Vertx vertx = Vertx.vertx();
        //  CountWordVerticle verticle = new CountWordVerticle(entryPoint, word, depth,result,pattern,flag);
        // vertx.deployVerticle(verticle);
        vertx.deployVerticle(new WordCounter(word, result,stopFlag), res -> {
            /* deploy the second verticle only when the first has completed */
            vertx.deployVerticle(new SubLinker(entryPoint, depth, pattern));
        });
    }
    public HashMap<String, Integer> getMap() {
        return this.result;
    }

    public void stop() {
        stopFlag.set();
    }
}
