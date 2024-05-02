package pcd.part2.vertX;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import pcd.part2.Report;
import java.util.HashMap;
import java.util.regex.Pattern;


public class CrawlerVertX {
    public static Report getWordOccurrences (String entryPoint, String word, int depth) throws InterruptedException {
        HashMap<String, Integer> result = new HashMap<>();
        Flag flag = new Flag();
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

        CountWordVerticle verticle = new CountWordVerticle(entryPoint, word, depth,result,pattern,flag);
        vertx.deployVerticle(verticle);
        while(!flag.isSet()){
         System.out.println("im waiting");
        }

        return new Report(word, result);
    }

}
