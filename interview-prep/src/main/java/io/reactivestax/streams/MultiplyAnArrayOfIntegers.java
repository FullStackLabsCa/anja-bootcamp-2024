package io.reactivestax.streams;

import java.util.List;

public class MultiplyAnArrayOfIntegers {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4);

        System.out.println(list.stream().reduce((prod, num) -> prod * num).get());
    }
}
