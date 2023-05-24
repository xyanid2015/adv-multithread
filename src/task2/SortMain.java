package task2;

import java.util.Arrays;
import java.util.Random;

/**
 * Task 2 - Multithreading Sorting via FJP
 * <p>
 * Cost: 0.5 points.
 * <p>
 * Implement Merge Sort or Quick Sort algorithm that sorts huge array of integers in parallel using Fork/Join framework.
 */
class SortMain {
    private static final Random rng = new Random();

    public static void main(String[] args) throws Exception {
        final int elements = 100_000_000;
        int[] randomArray = createRandomArray(elements);
        int[] sortedCopy = Arrays.copyOf(randomArray, elements);
        Arrays.sort(sortedCopy);

        ParallelQuickSort.sort(randomArray);

        if (!Arrays.equals(randomArray, sortedCopy)) {
            throw new Exception("randomArray is not sorted");
        }
    }

    private static int[] createRandomArray(int n) {
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = rng.nextInt(n);
        }
        return array;
    }
}
