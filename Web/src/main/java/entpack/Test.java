package entpack;

import com.alibaba.fastjson.JSONObject;
import entpack.bean.LogTask;
import entpack.bean.RedisTicket;
import entpack.utils.DateUtil;
import entpack.utils.MD5Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {


    public static void main(String[] args) {

        System.out.println( MD5Util.md5("SXTvXkCkkhEmdjRKddAF"+"test"+"1701747653387"+"DM28Hhb7eDQn2788S7a6"));

        List<String> matchs = new ArrayList<>();
		String pattern = "([0-9,.]+) จาก[A-Z+]{4}/x([0-9]+)เข้าx([0-9]+)";
//        String pattern = "บชX([0-9]+)X รับโอนจากX([0-9]+)X ([0-9,.]+)บ";

//        String pattern = template.getTemplate();//"บชX([0-9]+)X รับโอนจากX([0-9]+)X ([0-9,.]+)บ";

        String message = "08/02@12:23 100.00 จากBAAC/x838467เข้าx475366 ใช้ได้3,452.35บ";
//        String message = "08/02@13:11 100.00 จากTBNK/x216403เข้าx475366 ใช้ได้3,250.35บ";
        Pattern rr = Pattern.compile(pattern);

        Matcher mm = rr.matcher(message);
        if (mm.find()) {
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(message);
            if (m.find()) {

                String str2 = m.group(0);
                String pattern2 = "([0-9.,]+)";
                Pattern r2 = Pattern.compile(pattern2);
                Matcher m2 = r2.matcher(str2);

                while (m2.find()) {
                    for (int i = 0; i < m2.groupCount(); i++) {
//                    matchs.add(m2.group(0));
                        System.out.println(i + "  " + m2.group(i));
                    }
                }

            }
        }


    }


}
