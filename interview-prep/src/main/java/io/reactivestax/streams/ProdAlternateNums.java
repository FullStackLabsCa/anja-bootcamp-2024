package io.reactivestax.streams;

import java.util.List;
import java.util.stream.IntStream;

public class ProdAlternateNums {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7);

        IntStream.range(0, list.size() / 2 + 1).map(index -> list.get(index) * list.get(list.size() - index - 1)).forEach(num -> System.out.println(num));
    }
}
