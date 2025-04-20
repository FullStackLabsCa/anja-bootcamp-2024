package io.reactivestax.problems;

import java.util.*;
import java.util.stream.Stream;

public class CoinDenomination {

    static Integer amount = 0;

    static Integer coins = 0;

    static List<Integer> minDenominationsList = new ArrayList<>();

    public static void initialiseVars() {
        amount = 0;
        coins = 0;
        minDenominationsList = new ArrayList<>();
    }

    public static Integer findMinDenominationInList(List<Integer> denomination) {
        return denomination.stream().filter(num -> num <= amount).max(Comparator.naturalOrder()).orElse(0);
    }

    public static Integer findMinimumCoins(List<Integer> denominations, Integer target) {
        initialiseVars();
        Optional<Integer> minDenominationInList = denominations.stream().min(Comparator.naturalOrder());
        if (target <= 0 || (minDenominationInList.isPresent() && minDenominationInList.get() > target))
            return -1;

        List<Integer> denominationsList = new ArrayList<>(denominations.stream().filter(num -> num <= target).toList());

        checkMinimumDenomination(denominations, target, denominationsList);

        return minDenominationsList.stream().min(Comparator.naturalOrder()).orElse(-1);
    }

    private static void checkMinimumDenomination(List<Integer> denominations, Integer target, List<Integer> denominationsList) {
        for (Integer num : denominations) {
            amount = num;
            coins = 1;

            List<Integer> copiedDenominationList = new ArrayList<>(denominationsList.stream().toList());

            while (!Objects.equals(amount, target) && !copiedDenominationList.isEmpty()) {
                Integer min = findMinDenominationInList(copiedDenominationList);
                if (min == 0 || amount + min > target) {
                    copiedDenominationList.remove(min);
                } else {
                    amount = amount + min;
                    coins++;
                }
            }

            if (amount.equals(target)) {
                minDenominationsList.add(coins);
            }

            denominationsList.remove(num);
        }
    }

    public static void main(String[] args) {
        List<Integer> denomination = new ArrayList<>(Stream.of(2, 3, 5).sorted(Comparator.reverseOrder()).toList());

        System.out.println(findMinimumCoins(denomination, 19));
//        System.out.println(findMinimumCoins(denomination, 2));
//        System.out.println(findMinimumCoins(denomination, 13));
//        System.out.println(findMinimumCoins(denomination, 41));
//        System.out.println(findMinimumCoins(denomination, 67));
//        System.out.println(findMinimumCoins(denomination, 69));
//        System.out.println(findMinimumCoins(denomination, 113));
//        System.out.println(findMinimumCoins(denomination, 0));

    }
}