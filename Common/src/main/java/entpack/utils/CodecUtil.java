package entpack.utils;

/**
 * 编码解码工具类
 *
 * @author zhandl
 * @date 2015年11月21日 上午11:53:49
 */
public final class CodecUtil {

    /**
     * 十六进制定义
     */
    private final static byte[] hex = "0123456789ABCDEF".getBytes();

    public static String encodeBase64(byte[] data) {
        return Base64Util.encode(data);
    }

    public static byte[] decodeBase64(String data) {

        return Base64Util.decode(data);
    }

    /**
     * 将一个IPv4的地址转换为1一个整数
     */
    public static int encodeIP(String ipv4) {
        String[] pieces = ipv4.split("\\.");
        int ipi = 0;
        int i = 3;
        for (String piece : pieces) {
            ipi |= Integer.parseInt(piece) << (8 * (i--));
        }
        return ipi;
    }

    /**
     * 将一个整数转换为一个IPv4地址
     */
    public static String decodeIP(int ipv4) {
        StringBuilder sb = new StringBuilder(15);
        for (int i = 3; i >= 0; i--) {
            sb.append((ipv4 >> (8 * i)) & 0xff);
            if (i > 0) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    /**
     * 从字节数组到十六进制字符串转换
     *
     * @param data - 字节数组
     * @return - 十六进制字符串
     */
    public static String encodeHex(byte[] data) {

        try {
            byte[] buff = new byte[2 * data.length];
            for (int i = 0; i < data.length; i++) {
                buff[2 * i] = hex[(data[i] >> 4) & 0x0f];
                buff[2 * i + 1] = hex[data[i] & 0x0f];
            }
            return new String(buff);
        } catch (Exception ex) {
            throw new RuntimeException("十六进制编码异常：" + new String(data));
        }
    }

    /**
     * 从十六进制字符串到字节数组转换
     *
     * @param hex - 十六进制字符串
     * @return - 字节数组
     */
    public static byte[] decodeHex(String hex) {

        try {
            byte[] b = new byte[hex.length() / 2];
            int j = 0;
            for (int i = 0; i < b.length; i++) {
                char c0 = hex.charAt(j++);
                char c1 = hex.charAt(j++);
                b[i] = (byte) ((parse(c0) << 4) | parse(c1));
            }
            return b;
        } catch (Exception ex) {
            throw new RuntimeException("十六进制解码异常：" + hex);
        }
    }

    /**
     * 整数到字节数组转换
     *
     * @param n - 整数
     * @return - 字节数组
     */
    public static byte[] encodeInt(int n) {

        byte[] ab = new byte[4];
        ab[0] = (byte) (n >> 24);
        ab[1] = (byte) (n >> 16);
        ab[2] = (byte) (n >> 8);
        ab[3] = (byte) (n >> 0);
        return ab;
    }

    /**
     * 字节数组到整数的转换 b
     *
     * @param bb - 字节数组
     * @return - 整数
     */
    public static int decodeInt(byte bb[]) {

        return ((((int) bb[0] & 0xff) << 24)
                | (((int) bb[1] & 0xff) << 16)
                | (((int) bb[2] & 0xff) << 8)
                | (((int) bb[3] & 0xff) << 0));
    }

    /**
     * Long整数到字节数组转换
     *
     * @param x - Long整数
     * @return - 字节数组
     */
    public static byte[] encodeLong(long x) {

        byte[] bb = new byte[8];
        bb[0] = (byte) (x >> 56);
        bb[1] = (byte) (x >> 48);
        bb[2] = (byte) (x >> 40);
        bb[3] = (byte) (x >> 32);
        bb[4] = (byte) (x >> 24);
        bb[5] = (byte) (x >> 16);
        bb[6] = (byte) (x >> 8);
        bb[7] = (byte) (x >> 0);
        return bb;
    }


    /**
     * 字节数组到Long整数的转换
     *
     * @param bb - 字节数组
     * @return - Long整数
     */
    public static long decodeLong(byte[] bb) {
        return ((((long) bb[0] & 0xff) << 56)
                | (((long) bb[1] & 0xff) << 48)
                | (((long) bb[2] & 0xff) << 40)
                | (((long) bb[3] & 0xff) << 32)
                | (((long) bb[4] & 0xff) << 24)
                | (((long) bb[5] & 0xff) << 16)
                | (((long) bb[6] & 0xff) << 8)
                | (((long) bb[7] & 0xff) << 0));
    }


    /**
     * 将字符转成整数
     *
     * @param c - 字符
     * @return - 整数
     */
    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    /**
     * 防止非法实例化
     */
    private CodecUtil() {
    }

    /**
     * Base64 tool class
     *
     * @author zhandl
     * @date 2016年1月14日 上午11:17:24
     */
    public static final class Base64Util {

        private static char[] base64EncodeChars = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
                'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                'w', 'x', 'y', 'z', '0', '1', '2', '3',
                '4', '5', '6', '7', '8', '9', '+', '/'};
        private static byte[] base64DecodeChars = new byte[]{
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
                52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
                -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
                15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
                -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

        public static String encode(String data) {

            try {
                byte[] d = data.getBytes("iso-8859-1");
                return encode(d);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static String encode(byte[] data) {

            StringBuilder sb = new StringBuilder();
            int len = data.length;
            int i = 0;
            int b1, b2, b3;
            while (i < len) {
                b1 = data[i++] & 0xff;
                if (i == len) {
                    sb.append(base64EncodeChars[b1 >>> 2]);
                    sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                    sb.append("==");
                    break;
                }
                b2 = data[i++] & 0xff;
                if (i == len) {
                    sb.append(base64EncodeChars[b1 >>> 2]);
                    sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                    sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                    sb.append("=");
                    break;
                }
                b3 = data[i++] & 0xff;
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
                sb.append(base64EncodeChars[b3 & 0x3f]);
            }
            return sb.toString();
        }

        public static byte[] decode(String str) {

            StringBuilder sb = new StringBuilder();
            byte[] data = null;
            try {
                data = str.getBytes("iso-8859-1");
                int len = data.length;
                int i = 0;
                int b1, b2, b3, b4;
                while (i < len) {

                    do {
                        b1 = base64DecodeChars[data[i++]];
                    } while (i < len && b1 == -1);
                    if (b1 == -1) break;

                    do {
                        b2 = base64DecodeChars
                                [data[i++]];
                    } while (i < len && b2 == -1);
                    if (b2 == -1) break;
                    sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));

                    do {
                        b3 = data[i++];
                        if (b3 == 61) return sb.toString().getBytes("iso8859-1");
                        b3 = base64DecodeChars[b3];
                    } while (i < len && b3 == -1);
                    if (b3 == -1) break;
                    sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));

                    do {
                        b4 = data[i++];
                        if (b4 == 61) return sb.toString().getBytes("iso8859-1");
                        b4 = base64DecodeChars[b4];
                    } while (i < len && b4 == -1);
                    if (b4 == -1) break;
                    sb.append((char) (((b3 & 0x03) << 6) | b4));
                }
                return sb.toString().getBytes("iso8859-1");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
