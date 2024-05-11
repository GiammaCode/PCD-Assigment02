package pcd.part2.GUI;

import pcd.part2.Crowler;
import pcd.part2.GUI.ev.CrawlerVertX;
import pcd.part2.GUI.rx.CrawlerRx;
import pcd.part2.GUI.vt.CrawlerVT;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //Crowler crow = new CrawlerVT();
        Crowler crow = new CrawlerVertX();
        //Crowler crow = new CrawlerRx();
        MyModel model = new MyModel();
        MyController controller = new MyController(model,crow);
        MyView view = new MyView(controller,crow);
        model.addObserver(view);
        view.setVisible(true);
        new Worker(model).start();
    }
}
