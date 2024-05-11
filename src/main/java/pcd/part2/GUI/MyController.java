package pcd.part2.GUI;

import pcd.part2.Crowler;

public class MyController {
    private MyModel model;
    public MyController(MyModel model, Crowler crow){
        this.model = model;
    }
    public void processEvent(String event) {
        try {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    model.update();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
