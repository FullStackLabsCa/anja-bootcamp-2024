package io.reactivestax;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelStreamComparison {
    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 10_000_000)
                .boxed()
                .collect(Collectors.toList());

        // Measure time for Sequential Stream
        long startTime = System.currentTimeMillis();
        long sumSequential = numbers.stream().mapToLong(i->i).sum();
        long endTime = System.currentTimeMillis();
        System.out.println("Sequential Sum: " + sumSequential + " | Time taken: " + (endTime - startTime) + " ms");

        // Measure time for Parallel Stream
        long newstartTime = System.currentTimeMillis();
        long sumParallel = numbers.parallelStream().mapToLong(i -> i).sum();
        long newendTime = System.currentTimeMillis();
        System.out.println("Parallel Sum: " + sumParallel + " | Time taken: " + (newendTime - newstartTime) + " ms");
    }
}
