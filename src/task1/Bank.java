package task1;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

//Wait-notify
class Bank {
    public static final int NTEST = 10000;
    private final int[] accounts;
    private AtomicLong ntransacts = new AtomicLong(0);

    private final ReentrantLock reentrantLock = new ReentrantLock();

    public Bank(int n, int initialBalance) {
        accounts = new int[n];
        int i;
        for (i = 0; i < accounts.length; i++) accounts[i] = initialBalance;
    }

    public synchronized void transferWaitNotify(int from, int to, int amount) {
        while (accounts[from] < amount) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        accounts[from] -= amount;
        accounts[to] += amount;
        ntransacts.incrementAndGet();
        if (ntransacts.get() % NTEST == 0) {
            test();
            Thread.currentThread().interrupt();
        }
        notifyAll();
    }

    public void transferSynch(int from, int to, int amount) {
        synchronized (this) {
            accounts[from] -= amount;
            accounts[to] += amount;
            ntransacts.incrementAndGet();
            if (ntransacts.get() % NTEST == 0) {
                test();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void transferReentrant(int from, int to, int amount) {
        reentrantLock.lock();
        try {
            accounts[from] -= amount;
            accounts[to] += amount;
            ntransacts.incrementAndGet();
            if (ntransacts.get() % NTEST == 0) {
                test();
                Thread.currentThread().interrupt();
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    public void test() {
        int sum = 0;
        for (int i = 0; i < accounts.length; i++) sum += accounts[i];
        System.out.println("Transactions:" + ntransacts + " Sum: " + sum);
    }

    public int size() {
        return accounts.length;
    }
}
