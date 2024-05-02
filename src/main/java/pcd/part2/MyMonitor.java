package pcd.part2;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyMonitor {
    Lock lock = new ReentrantLock();
    public synchronized void addResult(HashMap<String,Integer> result,String link, int countWord){
        try {
            lock.lock();
            result.put(link,countWord);
        }finally {
            lock.unlock();
        }
    }
}
