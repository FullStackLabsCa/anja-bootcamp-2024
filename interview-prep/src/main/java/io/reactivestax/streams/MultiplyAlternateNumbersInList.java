package io.reactivestax.streams;

import java.util.List;

public class MultiplyAlternateNumbersInList {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7);

        System.out.println(list.stream().filter(num -> list.indexOf(num) % 2 == 0).reduce((prod, num) -> prod*num).get());
    }
}
