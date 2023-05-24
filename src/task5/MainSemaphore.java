package task5;

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
class MainSemaphore {
    static class Producer implements Runnable {
        Queue q;

        Producer(Queue q) {
            this.q = q;
            new Thread(this, "Producer").start();
        }

        public void run() {
            for (int i = 0; i < 5; i++)
                q.put(i);
        }
    }

    static class Consumer implements Runnable {
        Queue q;

        Consumer(Queue q) {
            this.q = q;
            new Thread(this, "Consumer").start();
        }

        public void run() {
            for (int i = 0; i < 5; i++)
                q.get();
        }
    }

    public static void main(String[] args) {
        Queue q = new Queue();
        new Consumer(q);
        new Producer(q);
    }
}
