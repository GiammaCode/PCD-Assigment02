package pcd.part2.vertX;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.util.regex.Pattern;

public class SubLinker extends AbstractVerticle {
        private String entrypoint;

        private Integer depth;

        private Pattern pattern;
        public SubLinker(String entrypoint, Integer depth, Pattern pattern){
            this.entrypoint=entrypoint;
            this.depth=depth;
            this.pattern=pattern;

        }

        public void start() {
            log("started.");
            EventBus eb = this.getVertx().eventBus();

            eb.publish("my-topic", "test");
        }

        private void log(String msg) {
            System.out.println("[REACTIVE AGENT #2]["+Thread.currentThread()+"] " + msg);
        }
    }