package task2;

public class ProducerConsumerApplication {
    public static void main(String[] args) {
        Drop drop = new Drop();

        int arraySize = 1000;
        int[] array = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            array[i] = i;
        }

        Thread producerThread = new Thread(new Producer(drop, array));
        Thread consumerThread = new Thread(new Consumer(drop));

        producerThread.start();
        consumerThread.start();
    }
}
class Producer implements Runnable {
    private final Drop drop;
    private final int[] array;

    public Producer(Drop drop, int[] array) {
        this.drop = drop;
        this.array = array;
    }

    @Override
    public void run() {
        for (int number : array) {
            drop.put(String.valueOf(number));
        }
        drop.put(String.valueOf(-1));
    }
}

class Consumer implements Runnable {
    private final Drop drop;

    public Consumer(Drop drop) {
        this.drop = drop;
    }

    @Override
    public void run() {
        int number;
        do {
            number = Integer.parseInt(drop.take());
            if (number != -1) {
                System.out.println("consumed: " + number);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
        } while (number != -1);
    }
}

class Drop {
    // Message sent from producer
    // to consumer.
    private String message;
    // True if consumer should wait
    // for producer to send message,
    // false if producer should wait for
    // consumer to retrieve message.
    private boolean empty = true;

    public synchronized String take() {
        // Wait until message is
        // available.
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = true;
        // Notify producer that
        // status has changed.
        notifyAll();
        return message;
    }

    public synchronized void put(String message) {
        // Wait until message has
        // been retrieved.
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = false;
        // Store message.
        this.message = message;
        // Notify consumer that status
        // has changed.
        notifyAll();
    }
}
