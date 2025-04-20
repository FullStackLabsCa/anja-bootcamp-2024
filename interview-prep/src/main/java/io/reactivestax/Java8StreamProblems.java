package io.reactivestax;

import java.util.*;
import java.util.stream.*;

public class Java8StreamProblems {
    public static void main(String[] args) {
        
        // Problem 1: Creating a Stream
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

        Stream<String> stream = names.stream();

        // Problem 2: Filtering Elements (Keep only even numbers)
        List<Integer> numbers = Arrays.asList(10, 15, 20, 25, 30, 35, 40, 45);

        List<Integer> list = numbers.stream().filter(num -> num % 2 == 0).toList();

        // Problem 3: Mapping Elements (Convert words to their lengths)
        List<String> words = Arrays.asList("Java", "Streams", "Lambda", "Functional", "API");
        List<Integer> list1 = words.stream().map(word -> word.length()).toList();

        // Problem 4: Sorting a Stream (Sort names alphabetically)
        List<String> unsortedNames = Arrays.asList("Charlie", "Alice", "Bob", "David");

        List<String> list2 = unsortedNames.stream().sorted().toList();

        // Problem 5: Distinct Elements (Remove duplicates)
        List<Integer> duplicateNumbers = Arrays.asList(10, 20, 10, 30, 20, 40, 50, 50, 60);

        List<Integer> list3 = duplicateNumbers.stream().distinct().toList();

        // Problem 6: Counting Elements (Count odd numbers)
        List<Integer> countNumbers = Arrays.asList(3, 6, 9, 12, 15, 18, 21, 24);

        long count = countNumbers.stream().filter(num -> num % 2 != 0).count();

        // Problem 7: Reducing (Sum of numbers)
        List<Integer> sumNumbers = Arrays.asList(5, 10, 15, 20, 25);

        Integer i = sumNumbers.stream().reduce((num, sum) -> sum + num).get();

        // Problem 8: Collecting Results (Get words with length > 4 into a List)
        List<String> collectWords = Arrays.asList("Java", "Stream", "Filter", "Reduce", "Map");

        List<String> collect = collectWords.stream().filter(word -> word.length() > 4).collect(Collectors.toList());

        // Problem 9: Checking Conditions (Check if any number > 50 exists)
        List<Integer> checkNumbers = Arrays.asList(10, 25, 35, 40, 60, 75);

        boolean b = checkNumbers.stream().anyMatch(num -> num > 50);

        // Problem 10: Parallel Streams (Process numbers in parallel)
        List<Integer> parallelNumbers = IntStream.rangeClosed(1, 10)
                                                 .boxed()
                                                 .collect(Collectors.toList());

        parallelNumbers.stream().parallel().forEach(System.out::println);

        // Problem 11: FlatMap (Flatten a list of lists)
        List<List<Integer>> nestedLists = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5, 6),
            Arrays.asList(7, 8, 9)
        );

        List<Integer> list4 = nestedLists.stream().flatMap(liste -> liste.stream()).toList();

        // ✅ Now, write your Java 8 Streams logic for each problem here!
    }
}
