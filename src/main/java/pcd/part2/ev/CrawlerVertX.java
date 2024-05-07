package pcd.part2.ev;

import io.vertx.core.Vertx;
import pcd.part2.Report;
import java.util.HashMap;


public class CrawlerVertX {
    public static Report getWordOccurrences(String entryPoint, String word, int depth) throws InterruptedException {
        HashMap<String, Integer> result = new HashMap<>();

        /*
        Un’applicazione Vert.x consiste di uno o più componenti chiamati Verticle. questi sono pezzi di
        codice che il motore di Vert.x esegue.
        Ogni verticle viene eseguito in maniera concorrente rispetto agli altri e non c’è uno stato che viene condiviso.
        Tradotto in parole semplici, si riesce a creare applicazioni multi-threaded senza dover gestire problematiche
        di concorrenza come la sincronizzazione o i lock tra thread.
        * */
        Vertx vertx = Vertx.vertx();

        CountWordVerticle verticle = new CountWordVerticle(entryPoint, word, result);
        vertx.deployVerticle(verticle);



        return new Report(word, result);
    }

}
