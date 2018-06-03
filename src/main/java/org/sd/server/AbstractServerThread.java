package org.sd.server;

import sun.awt.Mutex;

public abstract class AbstractServerThread extends Thread  {
    private Mutex mutex;

    public AbstractServerThread() {
        this.mutex = new Mutex();
    }

    public void Lock() {
        this.mutex.lock();
    }

    public void Unlock() {
        this.mutex.unlock();
    }
}
