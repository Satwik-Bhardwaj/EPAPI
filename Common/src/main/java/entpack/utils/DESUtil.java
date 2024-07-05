package entpack.utils;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * des加密、解密
 */
public class DESUtil {

    // 对字符串进行DES加密，返回BASE64编码的加密字符串
    public static String encryptString(String key, String str) {
        byte[] bytes = str.getBytes();
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            IvParameterSpec iv = new IvParameterSpec(key.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            byte[] encryptStrBytes = cipher.doFinal(bytes);

            return Base64.encodeBase64String(encryptStrBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 对BASE64编码的加密字符串进行解密，返回解密后的字符串
    public final static String decryptString(String key, String str) {
        try {
            byte[] bytes = Base64.decodeBase64(str);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            IvParameterSpec iv = new IvParameterSpec(key.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            bytes = cipher.doFinal(bytes);
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws Exception {

        /*
        String test="{\"SystemCode\":\"testC001\",\"WebId\":\"testWID001\",\"Language\":\"test001\"}";
        String key="12345678";
        String iv="76543210";

        System.out.println(encryptString(test,key,iv));

        String testmd5="922867E3-B151-44C6-B090-A5935D565FAp@ssw0rd1579241473QiapgdW4A+teWEQkQWG6KDRPNeH/CN5Jg/L4tHPA6rff9ucxr7ucsI+XF0QO71dXoWzELGE1zDmKKSAR7fIrfvT4U+YLqwPQ";
        System.out.println(MD5Util.md5(testmd5));

        System.out.println( MD5Util.md5Base64(testmd5));
        */




//        String testmd5="WENSEWSMkHaXhz1595573254EwurtDbZGktZBqBa0+47CClujlGwFpUMke2H7eiTniaONllLkEg8/5RclT8ujCc6MaXhW7/L4LtxTk5xET5lwQvNQGUhAyRkb6QesQPbRQi8b9jgs1yteQvbMkBe7KO6wS3Z1Q728lA=";
//        System.out.println(MD5Util.md5(testmd5));




      //  {"SystemCode"="WENSEW", "UserId"="H1mem541", "WebId"="hoh", "Language"="thai", "UserName"="H1mem541"}

      //  RydDecoder rydDecoder = new RydDecoder("12345678","76543210");


      // System.out.println(MD5Util.md5("WENSEWSMkHaXhz1595573254EwurtDbZGktZBqBa0+47CClujlGwFpUMke2H7eiTniaONllLkEg8/5RclT8ujCc6MaXhW7/L4Ltx\r\n"  +
       //        "Tk5xET5lwQvNQGUhAyRkb6QesQPbRQi8b9jgs1yteQvbMkBe7KO6wS3Z1Q728lA="));




        LinkedHashMap<String,String> request = new LinkedHashMap<String,String>();
        request.put("SystemCode", "WENSEW");
        request.put("WebId", "hoh");
        request.put("UserId", "testa112");
        request.put("Language", "thai");
        request.put("IsMobile", "0");

        String msg =encoder(request,"ONzUb5sL","85405000");
        //"SystemCode":"WENSEW","WebId":"hoh","UserId":"testa112","Language":"thai","IsMobile":"0"}

        //String msg2 = encryptString(tranToJson(request),"ONzUb5sL","85405000");
        System.out.println("request:"+request+"\n");

        System.out.println("encoder:"+msg);

        System.out.println(decode(msg,"ONzUb5sL","85405000"));

        //  Map<String,String> decode = rydDecoder.decode(msg);
      //  System.out.println("decode:"+decode);
    }


    public static String encoder(Map<String,String> datas,String deskv,String desiv){

        SecretKeySpec desKey = new SecretKeySpec(deskv.getBytes(), "DES");
        IvParameterSpec desIv = new IvParameterSpec(desiv.getBytes());

        String msg=null;

        String jsonData = tranToJson(datas);
        System.out.println(jsonData);

       //final Base64.Encoder encoder = Base64.getEncoder();
        final byte[] textByte;
        try
        {

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, desKey, desIv);
            textByte = cipher.doFinal(jsonData.getBytes("UTF-8"));

        }catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        msg= Base64.encodeBase64String(textByte);
//        msg = encoder.encodeToString(textByte);

        return msg;
    }

    public static Map<String,String> decode(String data,String deskv,String desiv)
    {
        SecretKeySpec desKey = new SecretKeySpec(deskv.getBytes(), "DES");
        IvParameterSpec desIv = new IvParameterSpec(desiv.getBytes());

       // final Base64.Decoder decoder = Base64.getDecoder();
        try
        {
            byte[] textByte = new BASE64Decoder().decodeBuffer(data);
            //byte[] textByte = decoder.decode(data);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, desKey, desIv);

            byte[] original = cipher.doFinal(textByte);
            String originalString = new String(original);
            return dataToMap(originalString);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
}

    }


    private static Map<String,String> dataToMap(String data)
    {
        Map<String,String> maps = new HashMap<>();
        String tmp = data.replace("{", "").replace("}", "");
        String[] fs= tmp.split(",");
        String[] ts;
        for(String f:fs)
        {
            ts=f.split(":");
            maps.put(ts[0].replace("'", ""), ts[1].replace("'", ""));
        }

        return maps;
    }



    private static String tranToJson(Map<String,String> datas) {


        StringBuilder sb =new StringBuilder();
        sb.append("{");
        datas.forEach((k, v) ->{
            if (sb.length() != 1)
                sb.append(",");
            sb.append("\""+k+"\":\""+v+"\"");

        });
        sb.append("}");

        return sb.toString();

    }
}

