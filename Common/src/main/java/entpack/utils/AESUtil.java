package entpack.utils;

import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static entpack.utils.UrlUtil.urlDecode;
import static entpack.utils.UrlUtil.urlEncode;

public class AESUtil {


    public static void main(String[] args) throws Exception {
        String key = "N7W7erK3ZvGuaKCn9AjJ7jfp";
        // String resut= AESUtil.Encrypt192ECB("(s=0&account=colin012&money=500&orderid=321654201811 08090711111colin012)",key);

        //String resut = "s=0&account=colin012&money=500&orderid=32165420181108090711111colin012";
        // System.out.println(resut);
        // System.out.println(AESUtil.EDecrypt192ECB(resut,key));

        //System.out.println(AESUtil.aesEncryptECB(resut, key));
        //System.out.println(AESUtil.aesDecryptECB("QHrGLpBKPTekOiMeU4UXIroEZ4cYmJ7BsHM7VMt+GNu6AHHpsicPKj80Gv4C1cRwtTDVliygo9Y48+jOGs94XyDTOOWgUtyGFXKAFimARdQ=", key));

        // System.out.println(AESUtil.aesEncryptECB(resut,key));
       // String priseString="64eed76ebe79fea78ef84fcded03bb925089012496919f509685dea2c427aff3c8ee397de8a7b9bc6c709b6b1837a86d99edef1db53625a1f332e3192764053874c653ab6f2f199a8112b2e1759a06dd061e3623a1721e32f4d0220167c6185900854b15d9cc1b1df2aa7065b0e396acf80553dc09f8b8f3";
       // System.out.println(AESUtil.aesDecryptECB(priseString,key));

        String testData="DyWCaC9vBG7WBr%2BRyHQHU%2B%2BvIAw2hUhsCMBFjN9b2hQFaywl%2Fwnt%2BpVvHUwYrpMAVjiu0CzmrMGJ%2Fedu4D8BEceI4Sy1jgYw6vIGpnmwO6j1cJ4HcNZae%2FWoR1z%2Bph8%2BFUYFDU3MVdvsZDndxc%2BJ2QjqCyni2TVqU5y3z5MkoCo%3D";
        System.out.println(testData);
        //testData=urlDecode(testData);
        //System.out.println(testData);
        System.out.println(AESDecrypt(testData,key));
        System.out.println(MD5Util.md5("account=test3&storeNo=300001&billno=30000120200716092043253test3&money=18327.0&"+"4mrMCjax"));



      //  System.out.println(AESUtil.aesDecryptECB(testData,key));

    }

    //dk=3DesKey mk=Md5Key
    /*
    private static String game(String agent,String account,String money,String orderId,String dk,String mk,String apiUrl,String gameType) throws Exception {
        String time = new Date().getTime()+"";
        String params = "s=0&account="+account+"&money="+money+"&orderid="+orderId+"&gameType="+gameType;
        String param = AESEncrypt(params,dk);
        String key = MD5(agent+time + mk);
        String postUrl = apiUrl.concat("?agent=").concat(agent).concat("&timestamp=").concat(time).concat("&param=").concat(param).concat("&key=").concat(key);

        String send = postUrl.replace(param, "AESEncrypt(" + params + ")");
        String receive = get(postUrl,null);
        insLog(companyCode, send, receive);
        return receive;
    }
    */

    /**
     * AESEncrypt
     * @param value
     * @param key
     * @return
     * @throws Exception
     */
    public static String AESEncrypt(String value,String key) throws Exception {
        String str = null;
        str = aesEncryptECB(value, key);
        str = urlEncode(str);
        return str;
    }

    /**
     *
     * @param value
     * @param key
     * @return
     * @throws Exception
     */
    public static String AESDecrypt(String value,String key) throws Exception {
        String str = null;
        str = urlDecode(value);
        str = aesDecryptECB(str, key);
        return str;
    }


    /**
     * aesEncrypt
     * @param str
     * @param key
     * @return
     */
    public static String aesEncryptECB(String str, String key) {
        return aesEncrypt(str, key, "", "AES/ECB/PKCS5PADDING");
    }




    /**
     * aesEncrypt
     * @param str
     * @param key
     * @param keyIV
     * @param instance
     * @return
     */
    private static String aesEncrypt(String str, String key, String keyIV, String instance) {
//        return aesEncrypt(str, key, keyIV, instance, "base64","UTF-8");
        return aesEncrypt(str, key, keyIV, instance, "UTF-8");

    }

    /**
     * aesEncrypt
     * @param str
     * @param key
     * @param keyIV
     * @param instance
     * @param code
     * @return
     */
    private static String aesEncrypt(String str, String key, String keyIV,
                                     String instance,String code) {
        try {
            if (StringUtil.isEmpty(code)) {
                code = "UTF-8";
            }

            IvParameterSpec iv = null;
            if (!StringUtil.isEmpty(keyIV)) {
                iv = new IvParameterSpec(keyIV.getBytes(code));
            }

            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(code), "AES");
            Cipher cipher = Cipher.getInstance(instance);


            String retVal = "";
            byte[] encrypted = null;

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            encrypted = cipher.doFinal(str.getBytes("UTF-8"));

            //System.out.println("encrypted string:" + Base64.getEncoder().encodeToString(encrypted));

            //BASE64Encoder encoder = new BASE64Encoder();
            //retVal = encoder.encode(encrypted);
            retVal = java.util.Base64.getEncoder().encodeToString(encrypted);
            //retVal = Base64.getEncoder().encodeToString(encrypted);

            return retVal;
        } catch (Exception e) {
            System.out.println(e.toString());
            //ex.printStackTrace();
        }
        return null;
    }

    /*
        private static String aesEncrypt(String str, String key, String keyIV,
                                     String instance, String outFormat,String code) {
        try {
            if (StringUtil.isEmpty(code)) {
                code = "UTF-8";
            }

            IvParameterSpec iv = null;
            if (!StringUtil.isEmpty(keyIV)) {
                iv = new IvParameterSpec(keyIV.getBytes(code));
            }

            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(code), "AES");
            Cipher cipher = Cipher.getInstance(instance);


            String retVal = "";
            byte[] encrypted = null;
            switch (outFormat)
            {
                case "hex":
                    cipher.init(1, skeySpec);
                    encrypted = cipher.doFinal(str.getBytes());

                    //retVal = byte2hex(encrypted);
                    retVal = "";

                    break;

                default:
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
                    encrypted = cipher.doFinal(str.getBytes("UTF-8"));

                    //System.out.println("encrypted string:" + Base64.getEncoder().encodeToString(encrypted));

                    BASE64Encoder encoder = new BASE64Encoder();
                    retVal = encoder.encode(encrypted);

                    //retVal = Base64.getEncoder().encodeToString(encrypted);
                    break;
            }

            return retVal;
        } catch (Exception e) {
            System.out.println(e.toString());
            //ex.printStackTrace();
        }
        return null;
    }
     */


    /**
     * aesDecryptECB
     * @param str
     * @param key
     * @return
     */
    public static String aesDecryptECB(String str, String key) {
        return aesDecrypt(str, key, "", "AES/ECB/PKCS5PADDING");
    }

    /*
    public static String aesDecryptECB(String str, String key, String code) {

        switch (code.toLowerCase())
        {
            case "hexascii":
                return aesDecryptHexASCII(str, key, "", "AES/ECB/PKCS5PADDING");
            case "b64ascii":
                return aesDecryptB64ASCII(str, key, "", "AES/ECB/PKCS5PADDING");
            case "hexutf8":
                return aesDecryptHexUTF8(str, key, "", "AES/ECB/PKCS5PADDING");
            default:
                return aesDecrypt(str, key, "", "AES/ECB/PKCS5PADDING");
        }
    }

    public static String aesDecryptCBC(String str, String key, String keyIV) {
        return aesDecrypt(str, key, keyIV, "AES/CBC/PKCS5PADDING");
    }

*/
    //default
    /**
     * aesDecrypt
     * @param str
     * @param key
     * @param keyIV
     * @param instance
     * @return
     */
    public static String aesDecrypt(String str, String key, String keyIV, String instance) {
//        return aesDecrypt2(str, key, keyIV, instance, "base64","UTF-8");
        return aesDecrypt(str, key, keyIV, instance, "UTF-8");

    }


    /**
     * Decrypt
     * @param str
     * @param key
     * @param keyIV
     * @param instance
     * @param code
     * @return
     */
    public static String aesDecrypt(String str, String key, String keyIV, String instance, String code) {
        try {
            if (code == null) {
                code = "UTF-8";
            }

            //str = eraseNewLine(str);
            IvParameterSpec iv = null;
            if ((keyIV != null) && (keyIV.length() > 0)) {
                iv = new IvParameterSpec(keyIV.getBytes(code));
            }
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(code), "AES");
            Cipher cipher = Cipher.getInstance(instance);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            String retVal = "";
            byte[] original = null;

            original = cipher.doFinal(Base64.getDecoder().decode(str));
            retVal = new String(original);

            return retVal;

        } catch (Exception e) {
            //ex.printStackTrace();
        }
        return "";
    }


    public static String aesEncryptCBC(String str, String key, String keyIV) {
        return aesEncryptFull(str, key, keyIV, "AES/CBC/PKCS5PADDING", "base64","UTF-8");

    }



    private static String aesEncryptFull(String str, String key, String keyIV,
                                     String instance, String outFormat,String code) {
        try {
            if (StringUtil.isEmpty(code)) {
                code = "UTF-8";
            }

            IvParameterSpec iv = null;
            if (!StringUtil.isEmpty(keyIV)) {
                iv = new IvParameterSpec(keyIV.getBytes(code));
            }

            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(code), "AES");
            Cipher cipher = Cipher.getInstance(instance);


            String retVal = "";
            byte[] encrypted = null;
            switch (outFormat)
            {
                case "hex":
                    cipher.init(1, skeySpec);
                    encrypted = cipher.doFinal(str.getBytes());

                    //retVal = byte2hex(encrypted);
                    retVal = "";

                    break;

                default:
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
                    encrypted = cipher.doFinal(str.getBytes("UTF-8"));

                    //System.out.println("encrypted string:" + Base64.getEncoder().encodeToString(encrypted));

                    BASE64Encoder encoder = new BASE64Encoder();
                    retVal = encoder.encode(encrypted);

                    //retVal = Base64.getEncoder().encodeToString(encrypted);
                    break;
            }

            return retVal;
        } catch (Exception e) {
            System.out.println(e.toString());
            //ex.printStackTrace();
        }
        return null;
    }

    /**
     * Encrypt For JDB Game
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static String encryptForJDB(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        int blockSize = cipher.getBlockSize();
        byte[] dataBytes = data.getBytes("UTF-8");
        int plainTextLength = dataBytes.length;
        if (plainTextLength % blockSize != 0) {
            plainTextLength = plainTextLength + (blockSize - plainTextLength % blockSize);
        }
        byte[] plaintext = new byte[plainTextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        byte[] encrypted = cipher.doFinal(plaintext);
        return  org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(encrypted);
    }
}
