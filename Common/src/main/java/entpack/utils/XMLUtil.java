package entpack.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLUtil {

    public static String getScript(String html) {
        String startTag = "<script>";
        String endTag = "</script>";

        return getContent(html, startTag, endTag);
    }

    public static String getContent(String conent, String startTag, String endTag) {

        int start = conent.indexOf(startTag);

        String Str = conent.substring(start);
        int end = Str.indexOf(endTag);

        Str = Str.substring(startTag.length(), end);
        return Str;
    }

    /**
     * 返回 XML
     * @param xml
     * @param tag
     * @return
     */
    public static String getXML(String xml, String tag) {
        String pattern = String.format("<[\\s]*?%s[^>]*?>([\\s\\S]*?)<[\\s]*?\\/[\\s]*?%s[\\s]*?>", tag, tag);
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(xml);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    /**
     * 返回 XML 数组
     * @param xml
     * @param tag
     * @return
     */
    public static List<String> getXMLArray(String xml, String tag) {
        String pattern = String.format("<[\\s]*?%s[^>]*?>([\\s\\S]*?)<[\\s]*?\\/[\\s]*?%s[\\s]*?>", tag, tag);
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(xml);
        List<String> result = new ArrayList<>();
        while (m.find()) {
            result.add(m.group(1));
        }
        return result;
    }
}
