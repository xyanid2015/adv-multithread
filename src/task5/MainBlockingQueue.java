package task5;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Task 5 (prodcons module):
 * <p>
 * Solve producer–consumer problem
 * <p>
 * Cost: 1 points.
 * <p>
 * Using:
 * <p>
 * Semaphore
 * BlockingQueue
 */
public class MainBlockingQueue {
    static BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);

    static class Producer extends Thread {
        @Override
        public void run() {
            try {
                int value = 0;
                while (true) {
                    queue.put(value);
                    System.out.println("Produced " + value);
                    value++;
                    // Simulate work
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Consumer extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    int value = queue.take();
                    System.out.println("Consumed " + value);
                    // Simulate work
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Producer().start();
        new Consumer().start();
    }
}

