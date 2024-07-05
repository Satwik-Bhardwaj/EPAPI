package entpack.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskUtil {

    private static ConcurrentHashMap<String, AtomicBoolean> runTaskMap = new ConcurrentHashMap();

    private static boolean stopTask = false;

//    public static AtomicInteger doItemCount = new AtomicInteger(0);
//    public static final int len = 1;

    /**
     * 设置任务运行状态
     *
     * @param name
     * @param stop
     */
    public static void setStopTask(String name, AtomicBoolean stop) {
        runTaskMap.put(name, stop);
    }

    /**
     * 取得任务运行状态
     *
     * @param name
     * @return
     */
    public static AtomicBoolean getStopTask(String name) {
        return runTaskMap.get(name);
    }

    /**
     * 取得并设置任务运行状态
     *
     * @param name
     * @return
     */
    public static AtomicBoolean getSetStopTask(String name) {
        AtomicBoolean stop = getStopTask(name);
        if (stop == null) {
            stop = new AtomicBoolean(false);
            setStopTask(name, stop);
        }
        return stop;
    }

    /**
     * 停止所有定时任务
     */
    public static void setStopTask() {
        stopTask = true;
        for (AtomicBoolean stop : runTaskMap.values()) {
            stop.set(true);
        }
    }

    /**
     * 开启所有定时任务
     */
    public static void setStartTask() {
        stopTask = false;
        for (AtomicBoolean stop : runTaskMap.values()) {
            stop.set(false);
        }
    }

    /**
     * 停止指定任务
     *
     * @param name
     */
    public static void setStopTask(String name) {
        getStopTask(name).set(true);
    }

    /**
     * 任务状态
     *
     * @return
     */
    public static boolean getStopStatus() {
        return stopTask;
    }

    public static boolean getStopStatus(String name) {
        if (stopTask) {
            return true;
        }
        return getSetStopTask(name).get();
    }

}
