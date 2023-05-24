package task1;

import java.math.BigInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class ParallelFactorial extends RecursiveTask<BigInteger> {
    private final int start;
    private final int n;
    private static final int SEQUENTIAL_THRESHOLD = 20;

    public ParallelFactorial(int n) {
        this(1, n);
    }

    private ParallelFactorial(int start, int n) {
        this.start = start;
        this.n = n;
    }

    @Override
    protected BigInteger compute() {
        if ((n - start) <= SEQUENTIAL_THRESHOLD) {
            BigInteger result = BigInteger.ONE;
            for (int i = start; i <= n; i++) {
                result = result.multiply(BigInteger.valueOf(i));
            }
            return result;
        } else {
            int mid = start + (n - start) / 2;
            ParallelFactorial firstSubtask = new ParallelFactorial(start, mid);
            ParallelFactorial secondSubtask = new ParallelFactorial(mid + 1, n);
            firstSubtask.fork();
            return secondSubtask.compute().multiply(firstSubtask.join());
        }
    }

    public static BigInteger getParallelFactorial(int n) {
        return ForkJoinPool.commonPool().invoke(new ParallelFactorial(n));
    }
}
