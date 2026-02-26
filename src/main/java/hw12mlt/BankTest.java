package hw12mlt;
import java.util.ArrayList;
import java.util.List;

public class BankTest {

    public static void main(String[] args) throws InterruptedException {
        Bank bank = new Bank();
        BankAccount a1 = new BankAccount(1, 1000);
        BankAccount a2 = new BankAccount(2, 1000);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                try {
                    bank.sendToAccount(a1, a2, 5);
                    bank.sendToAccount(a2, a1, 5);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) t.join();

        System.out.println("Balance A1 after transfers: " + a1.getBalance());
        System.out.println("Balance A2 after transfers: " + a2.getBalance());

        BankAccount b1 = new BankAccount(1, 500);
        BankAccount b2 = new BankAccount(2, 500);

        Thread t1 = new Thread(() -> {
            try {
                bank.sendToAccountDeadlock(b1, b2, 10);
            } catch (Exception e) {
                System.out.println("Deadlock test error: " + e.getMessage());
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                bank.sendToAccountDeadlock(b2, b1, 10);
            } catch (Exception e) {
                System.out.println("Deadlock test error: " + e.getMessage());
            }
        });

        t1.start();
        t2.start();

        t1.join(1000);
        t2.join(1000);

        if (t1.isAlive() || t2.isAlive()) {
            System.out.println("Deadlock detected, threads didn't finish in time.");
            t1.interrupt();
            t2.interrupt();
        } else {
            System.out.println("No deadlock detected.");
        }
        try {
            bank.sendToAccount(a1, a2, 10000);
        } catch (Exception e) {
            System.out.println("Validation test passed: " + e.getMessage());
        }
        try {
            bank.sendToAccount(null, a2, 10);
        } catch (Exception e) {
            System.out.println("Null check test passed: " + e.getMessage());
        }
    }
}
