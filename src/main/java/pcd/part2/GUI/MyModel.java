package pcd.part2.GUI;

import java.util.ArrayList;
import java.util.List;

public class MyModel {
    private List<ModelObserver> observers;

    public MyModel(){
        observers = new ArrayList<ModelObserver>();
    }

    public synchronized void update(){
        notifyObservers();
    }


    public void addObserver(ModelObserver obs){
        observers.add(obs);
    }

    private void notifyObservers(){
        for (ModelObserver obs: observers){
            obs.modelUpdated(this);
        }
    }
}
