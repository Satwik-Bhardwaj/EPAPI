package entpack.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LogTask implements Runnable {

    protected static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private static Logger ticketLog = LoggerFactory.getLogger("ticketLog");
    private static AtomicInteger doItemCount = new AtomicInteger();
    private Runnable doItem;
    private String msg;

    public LogTask(Runnable command, String flag, Object... args) {
        doItemCount.incrementAndGet();
        doItem = command;
        if (args != null) {
            msg = String.format(flag, args);
        } else {
            msg = flag;
        }
    }

    @Override
    public void run() {
        int count = doItemCount.decrementAndGet();

        try {
            long startTime = System.currentTimeMillis();
            doItem.run();
            long endTime = System.currentTimeMillis();
            ticketLog.info("{} {}ms,count:{}", msg, (endTime - startTime), count);

        } catch (Exception ex) {
            ticketLog.error(msg + " error", ex);
        }
    }

    public static void pushThreadPool(Runnable command, String flag, Object... args) {

        pushThreadPool(command, true, flag, args);
    }

    public static void pushThreadPool(Runnable command, boolean sync, String flag, Object... args) {

        LogTask task = new LogTask(command, flag, args);
        if (sync) {
            task.run();
        } else {
            threadPool.execute(task);
        }
    }
}
