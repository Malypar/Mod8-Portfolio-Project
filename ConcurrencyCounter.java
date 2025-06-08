import java.util.concurrent.*;

public class ConcurrencyCounter {
    public static void main(String[] args) {
        // LATCH SIGNAL
        CountDownLatch latch = new CountDownLatch(1);
        // THREAD POOL FOR EXECUTION
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // RELEASE LATCH AFTER COUNTING UPD
        Runnable countUpTask = () -> {
            String name = Thread.currentThread().getName();
            for (int i = 1; i <= 20; i++) {
                System.out.printf("%s - %d%n", name, i);
                sleepMillis(100);
            }
            latch.countDown();
        };

        // WAIT FOR LATCH TO BE RELEASED
        Runnable countDownTask = () -> {
            try {
                latch.await();
                String name = Thread.currentThread().getName();
                for (int i = 20; i >= 0; i--) {
                    System.out.printf("%s - %d%n", name, i);
                    sleepMillis(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Count Down Interrupted Before Start.");
            }
        };

        // SUBMIT TASKS TO EXECUTOR
        executor.submit(countUpTask);
        executor.submit(countDownTask);

        // SHUTDOWN EXECUTOR
        executor.shutdown();
    }

    private static void sleepMillis(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep Interrupted", e);
        }
    }
}