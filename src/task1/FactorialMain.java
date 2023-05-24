package task1;

import java.math.BigInteger;

/**
 * Task 1 (Optional) - Factorial via FJP
 * <p>
 * Cost: 0.5 point.
 * <p>
 * Use FJP to calculate factorial. Compare with the sequential implementation. Use BigInteger to keep values.
 */
class FactorialMain {
    public static BigInteger getSequentialFactorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        final int runs = 5;
        final int factorialNumber = 10000;

        // warm up
        BigInteger sequentialFactorial = getSequentialFactorial(factorialNumber);
        System.out.println("sequentialFactorial = " + sequentialFactorial);
        BigInteger parallelFactorial = ParallelFactorial.getParallelFactorial(factorialNumber);
        if (!sequentialFactorial.equals(parallelFactorial)) {
            throw new Exception("Parallel and sequential results are different.");
        }

        double sequentialTime = 0;
        for (int i = 0; i < runs; i++) {
            long start = System.currentTimeMillis();
            getSequentialFactorial(factorialNumber);
            sequentialTime += System.currentTimeMillis() - start;
        }
        sequentialTime /= runs;

        double parallelTime = 0;
        for (int i = 0; i < runs; i++) {
            long start = System.currentTimeMillis();
            ParallelFactorial.getParallelFactorial(factorialNumber);
            parallelTime += System.currentTimeMillis() - start;
        }
        parallelTime /= runs;

        // stats
        System.out.format("Average Sequential Time: %.1f ms\n", sequentialTime);
        System.out.format("Average Parallel Time: %.1f ms\n", parallelTime);
        System.out.format("Speedup: %.2f \n", sequentialTime / parallelTime);
        System.out.format("Efficiency: %.2f%%\n", 100 * (sequentialTime / parallelTime) / Runtime.getRuntime().availableProcessors());
    }
}
