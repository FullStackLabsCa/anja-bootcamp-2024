package io.reactivestax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class TransactionSummary {
    int transactionId;
    boolean isSenderEligibleForReward;

    public TransactionSummary(int transactionId, boolean isSenderEligibleForReward) {
        this.transactionId = transactionId;
        this.isSenderEligibleForReward = isSenderEligibleForReward;
    }
}

enum TransactionType {
    P2M, P2P, Self
}

class Payment {
    private static class Transaction {
        int senderId;
        int transactionId;
        int amount;
        TransactionType transactionType;

        public Transaction(int senderId, int transactionId, int amount, TransactionType transactionType) {
            this.senderId = senderId;
            this.transactionId = transactionId;
            this.amount = amount;
            this.transactionType = transactionType;
        }

        public int getSenderId() {
            return senderId;
        }

        public int getTransactionId() {
            return transactionId;
        }

        public int getAmount() {
            return amount;
        }

        public TransactionType getTransactionType() {
            return transactionType;
        }
    }

    Map<Integer, List<Transaction>> transactions = new HashMap<>();

    TransactionSummary makePayment(int transactionId, int senderId, int amount, TransactionType transactionType) {
        Transaction transaction = new Transaction(senderId, transactionId, amount, transactionType);
        if (transactions.containsKey(senderId)) {
            transactions.get(senderId).add(transaction);
        } else {
            ArrayList<Transaction> list = new ArrayList<>();
            list.add(transaction);
            transactions.put(senderId, list);
        }

        if (transactionType.equals(TransactionType.P2M)) {
            Map<Integer, Integer> collect = transactions.entrySet().stream()
                    .flatMap(transactionEntry -> transactionEntry.getValue().stream())
                    .filter(transaction1 -> transaction1.getTransactionType().equals(TransactionType.P2M))
                    .collect(Collectors.groupingBy(Transaction::getSenderId, Collectors.summingInt(Transaction::getAmount)))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .limit(100)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (collect.containsKey(senderId)) return new TransactionSummary(transactionId, true);
        }

        return new TransactionSummary(transactionId, false);
    }

    int getNumberOfTransactions(int senderId, TransactionType transactionType) {
        List<Transaction> transactionsList = transactions.get(senderId);
        Map<TransactionType, Long> transactionTypeMap = transactionsList.stream()
                .collect(Collectors.groupingBy(Transaction::getTransactionType, Collectors.counting()));

        if (transactionTypeMap.containsKey(transactionType)) return transactionTypeMap.get(transactionType).intValue();

        return 0;
    }
}


public class UserTransactionHandler {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        int totalNumberOfRequests = Integer.parseInt(br.readLine().trim());
        Payment payment = new Payment();

        String[] arr;

        while (totalNumberOfRequests-- > 0) {
            arr = br.readLine().split(" ");
            int transactionId;
            int senderId;
            int amount;
            int res;
            TransactionType transactionType;
            switch (arr[0]) {
                case "makePayment":
                    transactionId = Integer.parseInt(arr[1]);
                    senderId = Integer.parseInt(arr[2]);
                    amount = Integer.parseInt(arr[3]);
                    transactionType = TransactionType.valueOf(arr[4]);
                    TransactionSummary transactionSummary = payment.makePayment(transactionId, senderId, amount, transactionType);
                    out.print(transactionSummary.transactionId + " " + transactionSummary.isSenderEligibleForReward + "\n");
                    break;
                case "getNumberOfTransactions":
                    senderId = Integer.parseInt(arr[1]);
                    transactionType = TransactionType.valueOf(arr[2]);
                    res = payment.getNumberOfTransactions(senderId, transactionType);
                    out.print(res + "\n");
                    break;
                default:
            }
        }
        out.flush();
        out.close();
        System.out.println();
    }
}