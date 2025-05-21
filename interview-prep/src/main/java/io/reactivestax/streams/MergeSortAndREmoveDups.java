package io.reactivestax.streams;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MergeSortAndREmoveDups {
    public static void main(String[] args) {
        int[] arr1 = {1,2,3,4,5};
        int[] arr2 = {2,3,5,6,7};

        IntStream concat = IntStream.concat(Arrays.stream(arr1), Arrays.stream(arr2)).sorted().distinct();

        int[] array = concat.toArray();

        Arrays.stream(array).forEach(num -> System.out.println(num));
    }
}
