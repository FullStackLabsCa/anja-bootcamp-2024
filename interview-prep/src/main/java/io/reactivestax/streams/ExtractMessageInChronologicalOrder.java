package io.reactivestax.streams;

import java.util.List;

public class ExtractMessageInChronologicalOrder {
    public static void main(String[] args) {
        List<String> stringList = List.of(
                "14:30:3:Server started",
                "14:30:1:User logged in",
                "14:30:0:User logged in",
                "14:29:2:Database connected",
                "18:32:4:User logged out"
        );

        System.out.println(stringList.stream().sorted().map(s -> s.split(":", 4)[3]).toList());
    }
}
