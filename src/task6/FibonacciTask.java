package task6;

import java.util.concurrent.RecursiveTask;

/**
 * Task 6
 * <p>
 * Cost: 1 points.
 * <p>
 * RecursiveTask
 * <p>
 * 1. Give example from RecursiveTask javadoc. <p>
 * 2. Write FibonacciTask that implements RecursiveTask. <p>
 * 3. Apply suggestion from javadoc to check minimum granularity size less or equal 10. And in that case use linear algorithm. <p>
 * 4. Using unit test check that your code works correctly: <p>
 * assertEquals(1134903170L, new ForkJoinPool().invoke(new FibonacciTask(45)).longValue());
 */
public class FibonacciTask extends RecursiveTask<Long> {
    final Long n;

    public FibonacciTask(long n) {
        this.n = n;
    }

    @Override
    protected Long compute() {
        if (n <= 1)
            return n;
        else if (n <= 10) {
            long fib = 1, prev = 1;
            for (int i = 3; i <= n; i++) {
                long temp = fib;
                fib += prev;
                prev = temp;
            }
            return fib;
        } else {
            FibonacciTask f1 = new FibonacciTask(n - 1);
            f1.fork();
            FibonacciTask f2 = new FibonacciTask(n - 2);
            return f2.compute() + f1.join();
        }
    }
}

