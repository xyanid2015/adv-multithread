package task6;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;

public class FibonacciTaskTest {

    @Test
    public void testFibonacciTask() {
        assertEquals(1134903170L, ForkJoinPool.commonPool().invoke(new FibonacciTask(45)));
    }
}

