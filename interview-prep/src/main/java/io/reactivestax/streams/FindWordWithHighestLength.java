package io.reactivestax.streams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class FindWordWithHighestLength {

    public static void main(String[] args) {
        String str = "my name is anant jain and I am a software developer";

        Optional<String> max = Arrays.stream(str.split(" ")).max(Comparator.comparing(String::length));

        System.out.println(max.get());
    }

}
