package io.reactivestax.problems;

import java.util.ArrayList;
import java.util.List;

public class BuildingsWithOceanView {
    public static void main(String[] args) {
        int[] heights = {4, 2, 3, 1};
        List<Integer> list = new ArrayList<>();
        list.add(heights.length - 1);
        int maxSoFar = heights[heights.length - 1];

        for (int i = heights.length - 2; i >= 0; i--) {
            if (heights[i] > maxSoFar) {
                list.add(i);
                maxSoFar = heights[i];
            }
        }

        int[] array = list.stream().sorted().mapToInt(num -> num).toArray();
    }
}
