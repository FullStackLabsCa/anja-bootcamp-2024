package io.reactivestax.streams;

import java.util.Arrays;
import java.util.Comparator;

public class FindTheSecondHighestLength {

    public static void main(String[] args) {
        String str = "my name is anant jain";

        System.out.println(Arrays.stream(str.split(" ")).sorted(Comparator.comparing(st -> st.toString().length()).reversed()).skip(1).findFirst().get().length());
    }
}
