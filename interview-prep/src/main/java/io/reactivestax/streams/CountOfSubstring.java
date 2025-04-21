package io.reactivestax.streams;

import java.util.stream.IntStream;

public class CountOfSubstring {
    public static void main(String[] args) {
        String str = "xoxoBirdiexoxo!";

        String subStr = "xo";

        System.out.println(IntStream.range(0, str.length() - subStr.length() + 1).filter(index -> str.substring(index, index + 2).equals(subStr)).count());
    }
}
