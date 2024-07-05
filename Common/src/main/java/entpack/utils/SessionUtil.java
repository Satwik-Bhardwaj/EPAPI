package entpack.utils;

import javax.servlet.http.HttpSession;

/**
 * Session工具类
 */
public class SessionUtil {
    private static ThreadLocal<HttpSession> tl = new ThreadLocal<HttpSession>();

    public static void put(HttpSession s) {
        tl.set(s);
    }

    public static HttpSession get() {
        return tl.get();
    }

    public static void remove() {
        tl.remove();
    }
}
