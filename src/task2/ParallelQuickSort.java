package task2;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

class ParallelQuickSort extends RecursiveAction {
    private final int[] data;
    private final int left;
    private final int right;
    private static final int THRESHOLD = 1000;

    public ParallelQuickSort(int[] data, int left, int right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    @Override
    protected void compute() {
        if (right - left < THRESHOLD) {
            Arrays.sort(data, left, right);
        } else {
            int pivot = partition(data, left, right);
            ParallelQuickSort leftTask = new ParallelQuickSort(data, left, pivot);
            ParallelQuickSort rightTask = new ParallelQuickSort(data, pivot + 1, right);
            invokeAll(leftTask, rightTask);
        }
    }

    private int partition(int[] array, int left, int right) {
        int pivot = array[right - 1];
        int i = left - 1;
        for (int j = left; j < right - 1; j++) {
            if (array[j] <= pivot) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, right - 1);
        return i + 1;
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static void sort(int[] data) {
        ForkJoinPool.commonPool().invoke(new ParallelQuickSort(data, 0, data.length));
    }
}


