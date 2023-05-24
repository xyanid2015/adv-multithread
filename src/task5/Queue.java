package task5;

import java.util.concurrent.Semaphore;

class Queue {
    int item;
    static Semaphore semCon = new Semaphore(0);

    static Semaphore semProd = new Semaphore(1);

    void get() {
        try {
            semCon.acquire();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException caught");
        }
        System.out.println("Consumer consumed item : " + item);
        semProd.release();
    }

    void put(int item) {
        try {
            semProd.acquire();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException caught");
        }
        this.item = item;
        System.out.println("Producer produced item : " + item);
        semCon.release();
    }
}
