package entpack.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OrderUtil {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public static synchronized String getOrderNoByUUID() {
        Integer uuidHashCode = UUID.randomUUID().toString().hashCode();
        if (uuidHashCode < 0) {
            uuidHashCode = uuidHashCode * (-1);
        }
        String date = simpleDateFormat.format(new Date());
        return date + String.format("%010d", uuidHashCode);
    }

    public static void main(String[] args) {
        System.out.println(getOrderNoByUUID());
        System.out.println(getOrderNoByUUID());
        System.out.println(getOrderNoByUUID());
    }
}
