package pcd.part2.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyModel {
    private List<ModelObserver> observers;

    private HashMap<String,Integer> map = new HashMap<>();

    public MyModel(){
        observers = new ArrayList<ModelObserver>();
    }

    public synchronized void update(){
        notifyObservers();
    }


    public void addObserver(ModelObserver obs){
        observers.add(obs);
    }

    public synchronized void putMap(){

    }

    private void notifyObservers(){
        for (ModelObserver obs: observers){
            obs.modelUpdated(this);
        }
    }
}
