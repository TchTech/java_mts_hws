package hw12mlt;

public class Bank {
    public void sendToAccountDeadlock(BankAccount from, BankAccount to, int amount) throws Exception {
        synchronized (from) {
            Thread.sleep(50);
            synchronized (to) {
                if (from.getBalance() < amount) {
                    throw new Exception("Insufficient funds");
                }
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    public void sendToAccount(BankAccount from, BankAccount to, int amount) throws Exception {
        BankAccount first, second;
        if (from.getId() < to.getId()) {
            first = from;
            second = to;
        } else {
            first = to;
            second = from;
        }

        synchronized (first) {
            synchronized (second) {
                if (from.getBalance() < amount) {
                    throw new Exception("Insufficient funds");
                }
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }
}
