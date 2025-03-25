package io.reactivestax;

import java.util.Comparator;
import java.util.List;

public class IntegersReturn {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 42, 1, 32, 21, 34, 52);

        List<Integer> list1 = list.stream().filter(num -> num % 2 == 0).sorted(Comparator.reverseOrder()).limit(5).toList();

        System.out.println(list1);
    }
}
