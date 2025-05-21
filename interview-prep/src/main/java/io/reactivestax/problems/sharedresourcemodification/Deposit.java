package io.reactivestax.problems.sharedresourcemodification;

public class Deposit implements Runnable {
    private final BankAccount bankAccount;
    private int count = 5;

    public Deposit(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Override
    public void run() {
        while (count != 0) {
            bankAccount.deposit(200);
            System.out.println("Deposited amount...Balance is: " + bankAccount.getBalance());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            count--;
        }
    }
}
