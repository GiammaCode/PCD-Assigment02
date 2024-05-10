package pcd.part2.GUI;

public class Worker extends Thread {

    private MyModel model;

    public Worker(MyModel model){
        this.model = model;
    }

    public void run(){
        while (true){
            try {
                model.update();
                Thread.sleep(200);
            } catch (Exception ex){
            }
        }
    }
}
