package pcd.part2.GUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        MyModel model = new MyModel();
        MyController controller = new MyController(model);
        MyView view = new MyView(controller);
        model.addObserver(view);
        view.setVisible(true);
        new Worker(model).start();
    }
}
