package pcd.part2.GUI;

public interface ModelObserver {

    void modelUpdated(MyModel model) throws InterruptedException;
}
