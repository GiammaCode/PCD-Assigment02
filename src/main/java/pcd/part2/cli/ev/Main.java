package pcd.part2.cli.ev;

import io.vertx.core.Vertx;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        // Definiamo la funzione asincrona e concorrente
        Runnable cicloAsincronoConcorrente = () -> {
            // Creiamo un ExecutorService per eseguire i compiti in modo parallelo
            ExecutorService executorService = Executors.newFixedThreadPool(5); // Ad esempio, utilizziamo 5 thread

            // Avviamo i compiti in modo parallelo
            for (int i = 0; i < 5; i++) {
                executorService.execute(() -> {
                    // Blocco di codice da eseguire in modo asincrono e parallelo
                    for (int j = 0; j < 5; j++) {
                        System.out.println("Eseguo qualcosa in modo asincrono e parallelo");
                        System.out.println("Thread: " + Thread.currentThread());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            // Ferma l'ExecutorService dopo che tutti i compiti sono stati completati
            executorService.shutdown();
        };

        // Avviamo la funzione asincrona e concorrente
        cicloAsincronoConcorrente.run();

        // Manteniamo l'applicazione in esecuzione
        vertx.deployVerticle(new io.vertx.core.AbstractVerticle() {});
    }
}
