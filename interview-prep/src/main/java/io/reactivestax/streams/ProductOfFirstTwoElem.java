package io.reactivestax.streams;

import java.util.Arrays;

public class ProductOfFirstTwoElem {

    public static void main(String[] args) {
        int[] arr = {100,234, 354, 34};

        System.out.println(Arrays.stream(arr).limit(2).reduce(Integer::sum));
    }

}
