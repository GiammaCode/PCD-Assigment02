package pcd.part2;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyMonitor {
    Lock lockMap = new ReentrantLock();
    Lock lockDec = new ReentrantLock();
    public void syncPut(HashMap<String,Integer> result,String link, int countWord){
        try {
            lockMap.lock();
            result.put(link,countWord);
        }finally {
            lockMap.unlock();
        }
    }
    public int syncDec(int depth){
        try {
            lockMap.lock();
            depth--;
        }finally {
            lockMap.unlock();
        }
        return depth;
    }

}
