package task1;

class TransferThread extends Thread {
    private Bank bank;
    private int fromAccount;
    private int maxAmount;

    private int variant;

    private static final int REPS = 1000;

    public TransferThread(Bank b, int from, int max, int var) {
        bank = b;
        fromAccount = from;
        maxAmount = max;
        variant = var;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (int i = 0; i < REPS; i++) {
                int toAccount = (int) (bank.size() * Math.random());
                int amount = (int) (maxAmount * Math.random() / REPS);
                switch(variant){
                    case 1:
                        bank.transferSynch(fromAccount, toAccount, amount);
                        break;
                    case 2:
                        bank.transferWaitNotify(fromAccount, toAccount, amount);
                        break;
                    case 3:
                        bank.transferReentrant(fromAccount, toAccount, amount);
                        break;
                    default:
                        System.out.println("Неправильний варіант керування");
                }
            }
        }
    }
}
