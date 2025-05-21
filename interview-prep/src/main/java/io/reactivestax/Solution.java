package io.reactivestax;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {

        IntStream stream = Arrays.stream(nums1);
        IntStream stream1 = Arrays.stream(nums2);

        List<Integer> list = IntStream.concat(stream, stream1).sorted().boxed().toList();

        System.out.println(list.size() / 2);

        return 0.0;
    }
}