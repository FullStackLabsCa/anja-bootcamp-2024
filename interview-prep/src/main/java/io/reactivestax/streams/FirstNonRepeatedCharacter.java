package io.reactivestax.streams;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FirstNonRepeatedCharacter {

    public static void main(String[] args) {
        String str = "reactiverstax";

//        Arrays.stream(str.split("")).collect()

        Arrays.stream(args, 1, args.length);
        System.out.println(
                Arrays.stream(
                                str.split(""))
                        .filter(s -> Arrays.stream(
                                        str.split(""))
                                .collect(Collectors.groupingBy(Function.identity(),
                                        Collectors.counting())).get(s) == 1).findFirst().get());
    }

}
