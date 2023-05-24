package task6;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;

/**
 * RecursiveAction
 * <p>
 * Sum of double squares
 * <p>
 * 1. Give last example from RecursiveAction javadoc about calculation of sum of squares in double[] array. <p>
 * 2. Use double array of half-billion size 500_000_000 filled by random doubles. <p>
 * 3. Compare speed with direct linear calculation (you may use Stream API as well): <p>
 * double sum = 0; for (double v : ARRAY) { sum += v * v; }
 */
public class Square {
    static double sumOfSquares(ForkJoinPool pool, double[] array) {
        int n = array.length;
        Applyer a = new Applyer(array, 0, n, null);
        pool.invoke(a);
        return a.result;
    }

    static class Applyer extends RecursiveAction {
        final double[] array;
        final int lo, hi;
        double result;
        Applyer next; // keeps track of right-hand-side tasks

        Applyer(double[] array, int lo, int hi, Applyer next) {
            this.array = array;
            this.lo = lo;
            this.hi = hi;
            this.next = next;
        }

        double atLeaf(int l, int h) {
            double sum = 0;
            for (int i = l; i < h; ++i) // perform leftmost base step
                sum += array[i] * array[i];
            return sum;
        }

        protected void compute() {
            int l = lo;
            int h = hi;
            Applyer right = null;
            while (h - l > 1 && getSurplusQueuedTaskCount() <= 3) {
                int mid = (l + h) >>> 1;
                right = new Applyer(array, mid, h, right);
                right.fork();
                h = mid;
            }
            double sum = atLeaf(l, h);
            while (right != null) {
                if (right.tryUnfork()) // directly calculate if not stolen
                    sum += right.atLeaf(right.lo, right.hi);
                else {
                    right.join();
                    sum += right.result;
                }
                right = right.next;
            }
            result = sum;
        }
    }

    public static void main(String[] args) {
        final ForkJoinPool pool = ForkJoinPool.commonPool();
        double[] randoms = ThreadLocalRandom.current().doubles(500_000_000, 1, 500_000_000).toArray();

        double sequentialTime = 0;
        long start = System.currentTimeMillis();
        double sumSeq = Arrays.stream(randoms).map(operand -> operand * operand).sum();
        sequentialTime += System.currentTimeMillis() - start;

        double parallelTime = 0;
        long start2 = System.currentTimeMillis();
        double sumPar = sumOfSquares(pool, randoms);
        parallelTime += System.currentTimeMillis() - start2;

        System.out.format("Average Sequential Time: %.1f ms\n", sequentialTime);
        System.out.format("Average Parallel Time: %.1f ms\n", parallelTime);
        System.out.format("Speedup: %.2f \n", sequentialTime / parallelTime);
        System.out.format("Efficiency: %.2f%%\n", 100 * (sequentialTime / parallelTime) / Runtime.getRuntime().availableProcessors());
        System.out.println("sumSeq = " + sumSeq);
        System.out.println("sumPar = " + sumPar);
    }
}
