package entpack.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * hash算法工具类
 *
 * @author zhandl
 * @date 2015年11月21日 下午12:02:17
 */
@SuppressWarnings("rawtypes")
public final class HashUtil {

    private static final long FNV_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    /**
     * 对key进行应用DJB算法的hash运算
     * 备注: DJB hash function，俗称'Times33'算法
     * 特点: 算法简单,性能高(cpu不敏感)
     *
     * @param key -- 被hash的key
     * @return -- hash值(hashcode)
     */
    public static long toDJB(String key) {

        long hash = 5381;
        for (int i = 0; i < key.length(); i++) {
            hash = ((hash << 5) + hash) + key.charAt(i);
        }
        return hash;
    }

    /**
     * 对map对象进行应用DJB算法hash运算
     *
     * @param m - map对象
     * @return - hash值(hashcode)
     */
    public static long toDJB(Map m) {

        if (null != m) {
            long hash = 5381;
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Entry e = (Entry) it.next();
                Object k = e.getKey();
                Object v = e.getValue();
                if (null != k) {
                    hash = ((hash << 5) + hash) + k.hashCode();
                }
                if (null != v) {
                    hash = ((hash << 5) + hash) + v.hashCode();
                }
            }
            return hash;
        }
        return 0L;
    }

    /**
     * 对数组对象进行应用DJB算法hash运算
     *
     * @param objs
     * @return
     */
    public static long toDJB(Object... objs) {

        if (null == objs) {
            return 0L;
        }
        long hash = 5381;
        for (Object o : objs) {
            int h = (null != o) ? o.hashCode() : 0;
            hash = ((hash << 5) + hash) + h;
        }
        return hash;
    }

    /**
     * 对list对象进行应用DJB算法hash运算
     *
     * @param list
     * @return
     */
    public static long toDJB(List list) {

        if (null == list) {
            return 0L;
        }

        long hash = 5381;
        for (Object o : list) {
            int h = (null != o) ? o.hashCode() : 0;
            hash = ((hash << 5) + hash) + h;
        }
        return hash;
    }

    /**
     * 对key进行应用Ketama算法的hash运算
     * 备注: Ketama hash算法:key进行md5,然后取最高八个字节作为long类型的hash值
     * 特点: 优先保证hash的分布均匀性
     *
     * @param key - 被hash的key
     * @return - hash值(hashcode)
     */
    public static long kemata64(String key) {

        byte[] rtv = md5(key.getBytes());
        return ((long) rtv[rtv.length - 1] << 56)
                + ((long) (rtv[rtv.length - 2] & 255) << 48)
                + ((long) (rtv[rtv.length - 3] & 255) << 40)
                + ((long) (rtv[rtv.length - 4] & 255) << 32)
                + ((long) (rtv[rtv.length - 5] & 255) << 24)
                + ((rtv[rtv.length - 6] & 255) << 16)
                + ((rtv[rtv.length - 7] & 255) << 8)
                + ((rtv[rtv.length - 8] & 255) << 0);
    }

    /**
     * 对key进行应用Ketama算法的hash运算
     * 备注: Ketama hash算法:key进行md5,然后取最低四个字节作为long类型的hash值
     * 特点: 优先保证hash的分布均匀性
     *
     * @param key - 被hash的key
     * @return - hash值(hashcode)
     */
    public static Long kemata32(String key) {

        byte[] md5 = md("MD5", key.getBytes());
        long hash = 0L;
        for (int i = 0; i < 4; i++) {
            hash |= (md5[i] & 0xFFL) << (8 * i);
        }
        return hash & 0xffffffffL;
    }

    /**
     * 对key进行应用fnv1算法的hash运算
     *
     * @param key - 被hash的key
     * @return - hash值(hashcode)
     */
    public static long fnva(String key) {

        long rv = FNV_64_INIT;
        int len = key.length();
        for (int i = 0; i < len; i++) {
            rv ^= key.charAt(i);
            rv *= FNV_64_PRIME;
        }
        return rv;
    }

    /**
     * 对一个文件进行md5计算
     *
     * @param f - 文件
     * @return
     */
    public static byte[] md5(File f) {

        if (null == f) {
            throw new IllegalArgumentException("file is null");
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f.length());

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] rtv = null;
            synchronized (md) {
                md.reset();
                md.update(byteBuffer);
                rtv = md.digest();
            }
            return rtv;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * md5 hash算法
     *
     * @param msg
     * @return - md5串(十六进制格式)
     */
    public static String md5Hex(String msg) {

        byte[] r = null;
        try {
            r = md5(msg.getBytes("UTF-8"));
        } catch (Exception ex) {
        }
        return CodecUtil.encodeHex(r);
    }

    /**
     * md5 hash算法:对一个文件计算
     *
     * @param f - 文件
     * @return - md5值(十六进制表示)
     */
    public static String md5Hex(File f) {

        byte[] r = null;
        try {
            r = md5(f);
        } catch (Exception ex) {
        }
        return CodecUtil.encodeHex(r);
    }

    /**
     * md5 hash算法
     *
     * @param msg
     * @return - md5串(base64编码)
     */
    public static String md5Base64(String msg) {

        byte[] r = null;
        try {
            r = md5(msg.getBytes("UTF-8"));
        } catch (Exception ex) {
        }
        return CodecUtil.encodeBase64(r);
    }

    /**
     * @param data
     * @return
     * @description md5 hash算法
     */
    public static byte[] md5(byte[] data) {
        return md("MD5", data);
    }

    /**
     * sha-1 hash算法
     *
     * @param msg
     * @return - sha-1串(base64编码)
     */
    public static String sha1Base64(String msg) {

        byte[] r = null;
        try {
            r = sha1(msg.getBytes("UTF-8"));
        } catch (Exception ex) {
        }
        return CodecUtil.encodeBase64(r);
    }

    /**
     * sha-1 hash算法
     *
     * @param msg
     * @return - sha-1串(十六进制格式)
     */
    public static String sha1Hex(String msg) {

        byte[] r = null;
        try {
            r = sha1(msg.getBytes("UTF-8"));
        } catch (Exception ex) {
        }
        return CodecUtil.encodeHex(r);
    }

    /**
     * sha-1 hash算法
     *
     * @param data
     * @return
     */
    public static byte[] sha1(byte[] data) {
        return md("SHA-1", data);
    }

    private static byte[] md(String alg, byte[] codes) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(alg + " is not supported", e);
        }
        byte[] rtv = null;
        synchronized (md) {
            md.reset();
            md.update(codes);
            rtv = md.digest();
        }
        return rtv;
    }

    public static long murmur(byte[] key) {
        return murmur64A(ByteBuffer.wrap(key), 0x1234ABCD);
    }

    public static long murmur(String key) {
        return murmur(key.getBytes());
    }

    private static long murmur64A(ByteBuffer buf, int seed) {

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(
                    ByteOrder.LITTLE_ENDIAN);
            // for big-endian version, do this first:
            // finish.position(8-buf.remaining());
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }

    /**
     * 防止非法实例化
     */
    private HashUtil() {
    }
}
