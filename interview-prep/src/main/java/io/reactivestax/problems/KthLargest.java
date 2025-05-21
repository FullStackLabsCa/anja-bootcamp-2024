package io.reactivestax.problems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class KthLargest {

    List<Integer> list;
    int k;
    List<Integer> output;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        this.list = Arrays.stream(nums).boxed().collect(Collectors.toList());
        output = new ArrayList<>();
        output.add(null);
    }
    
    public int add(int val) {
        list.add(val);
        List<Integer> list1 = list.stream().sorted(Comparator.reverseOrder()).skip(3).toList();

        return list1.get(0);
    }
}
