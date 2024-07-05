package entpack.utils;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import redis.clients.jedis.Jedis;

/**
 * Created by BIG on 2016/10/22.
 */
public class RedisLock {

    public static boolean lock(String lockKey, int timeoutSeconds) {

        Cache redis = Redis.use();

        long expires = System.currentTimeMillis() + timeoutSeconds * 1000;
        String expiresStr = String.valueOf(expires); //锁到期时间

        Jedis jedis = redis.getJedis();
        try {
            if (jedis.setnx(lockKey, expiresStr) == 1) {
                // lock acquired
                jedis.expire(lockKey, timeoutSeconds);
                return true;
            }
            String currentValueStr = jedis.get(lockKey); //redis里的时间
            if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
                // lock is expired

                String oldValueStr = jedis.getSet(lockKey, expiresStr);
                //获取上一个锁到期时间，并设置现在的锁到期时间，
                //只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
                if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                    //如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                    // lock acquired
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            redis.close(jedis);
        }

        return false;
    }

    /**
     * 获取 锁.
     */
    public static synchronized boolean acquire(String lockKey, int timeoutMsecs, int expireMsecs)  {
        int timeout = timeoutMsecs;
        Cache redis = Redis.use();
        Jedis jedis = redis.getJedis();
        try {

            while (timeout >= 0) {
                long expires = System.currentTimeMillis() + expireMsecs + 1;
                String expiresStr = String.valueOf(expires); //锁到期时间

                if (jedis.setnx(lockKey, expiresStr) == 1) {
                    // lock acquired
                    return true;
                }

                String currentValueStr = jedis.get(lockKey); //redis里的时间
                if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                    //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
                    // lock is expired

                    String oldValueStr = jedis.getSet(lockKey, expiresStr);
                    //获取上一个锁到期时间，并设置现在的锁到期时间，
                    //只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
                    if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                        //如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                        // lock acquired
                        return true;
                    }
                }
                timeout -= 10;
                Thread.sleep(10);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            redis.close(jedis);
        }

        return false;
    }

    /**
     * 释放锁
     *
     * @param key_lock
     */
    public static void release(String key_lock) {
        Cache redis = Redis.use();
        redis.del(key_lock);
    }

}
