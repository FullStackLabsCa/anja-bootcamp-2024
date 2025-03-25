package io.reactivestax;

import java.util.List;
import java.util.stream.Stream;

public class GenerateFibonacci {
    public static void main(String[] args) {
//        9. Infinite Streams - Generate Fibonacci
//        Problem:
//        Generate the first 10 Fibonacci numbers using Stream.iterate().

        List<Integer> list = Stream.iterate(new int[]{0, 1}, fib -> new int[]{fib[1], fib[0] + fib[1]})
                .limit(10)
                .map(fib -> fib[0])
                .toList();

        System.out.println(list);
    }
}
