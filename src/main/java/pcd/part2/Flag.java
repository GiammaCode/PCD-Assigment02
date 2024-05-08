package pcd.part2;

public class Flag {
    boolean  flag;
    public Flag(){
        flag=false;
    }

    public synchronized void set(){
        flag=true;
    }
    public synchronized boolean isSet(){
        return flag;
    }
}
