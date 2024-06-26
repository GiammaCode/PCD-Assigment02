package pcd.part2.cli.ev;

import io.vertx.core.Vertx;
import pcd.part2.Flag;
import pcd.part2.Report;
import java.util.HashMap;
import java.util.regex.Pattern;


public class CrawlerVertX {
    public Report getWordOccurrences (String entryPoint, String word, int depth) throws InterruptedException {
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
        vertx.deployVerticle(new WordCounter(word,result,flag), res -> {
            /* deploy the second verticle only when the first has completed */
            vertx.deployVerticle(new SubLinker(entryPoint,depth,pattern));
        });
        while(!flag.isSet()){}
        return new Report(word, result);
    }

}
