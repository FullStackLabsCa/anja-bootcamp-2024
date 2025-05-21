package io.reactivestax.problems.sharedresourcemodification;

public class Withdraw implements Runnable {
    private final BankAccount bankAccount;

    private int times = 10;

    public Withdraw(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Override
    public void run() {
        while (times != 0) {
            times --;
                if (bankAccount.getBalance() - 100 < 0){
                    System.out.println("Not enough funds...Balance is: " + bankAccount.getBalance());
                    times++;
                }else {
                    bankAccount.withdraw(100);
                    System.out.println("Withdrawn....Balance is: "+bankAccount.getBalance());
                }
        }
    }
}
