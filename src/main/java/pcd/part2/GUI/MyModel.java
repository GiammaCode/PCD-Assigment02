package pcd.part2.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyModel {
    private List<ModelObserver> observers;

    public MyModel(){
        observers = new ArrayList<>();
    }

    public synchronized void update() throws InterruptedException {
        notifyObservers();
    }


    public void addObserver(ModelObserver obs){
        observers.add(obs);
    }

    public synchronized void putMap(){

    }

    private void notifyObservers() throws InterruptedException {
        for (ModelObserver obs: observers){
            obs.modelUpdated(this);
        }
    }
}
