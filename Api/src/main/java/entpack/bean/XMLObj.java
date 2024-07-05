package entpack.bean;

import entpack.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLObj {
    private String xml;

    public XMLObj(String xml) {
        this.xml = xml;
    }

    public XMLObj get(String tag) {
        return getXMLObj(tag);
    }

    /**
     * 返回查找对象
     *
     * @param tag
     * @return
     */
    private XMLObj getXMLObj(String tag) {
        return new XMLObj(XMLUtil.getXML(xml, tag));
    }

    /**
     * 返回数组对象
     *
     * @param tag
     * @return
     */
    private List<XMLObj> getXMLArrayObj(String tag) {
        List<String> list = XMLUtil.getXMLArray(xml, tag);
        List<XMLObj> result = new ArrayList<>();
        for (String xml : list) {
            result.add(new XMLObj(xml));
        }
        return result;
    }

    /**
     * 返回数组对象
     *
     * @param tag
     * @return
     */
    public List<XMLObj> getArray(String tag) {
        return getXMLArrayObj(tag);
    }

    /**
     * 返回内容
     *
     * @return
     */
    public String getContent() {
        return xml;
    }

    /**
     * 返回内容
     *
     * @param startTag
     * @param endTag
     * @return
     */
    public XMLObj getContent(String startTag, String endTag) {
        return new XMLObj(XMLUtil.getContent(xml, startTag, endTag));
    }

    @Override
    public String toString() {
        return xml;
    }

    public String getUrlParams(String tag, String name) {
        return get(tag).getParams(name);
    }

    public String getParams(String name) {
        Pattern r = Pattern.compile("(^|&)" + name + "=([^&]*)(&|$)");
        String url = xml.replace("&amp;", "&");
        Matcher m = r.matcher(url);

        if (m.find()) {
            return m.group(2);
        }
        return "";
    }

    /**
     * 返回所有URL参数
     *
     * @return
     */
    public Map<String, String> getAttrsMap() {
        Pattern r = Pattern.compile("([a-zA-Z]+)([:<>=]+)(\"([^\"]+)\"|\'([^\']+)\'|([0-9\\-]+))");
        String url = xml.replace("&amp;", "&");
        Matcher m = r.matcher(url);
        Map<String, String> paramsMap = new HashMap<>();

        while (m.find()) {
            paramsMap.put(m.group(1), m.group(4) == null ? m.group(5) : m.group(4));
        }
        return paramsMap;
    }

    /**
     * 返回所有URL参数
     *
     * @return
     */
    public Map<String, String> getParamsMap() {
        Pattern r = Pattern.compile("(\\w+)=([^&]*)(&|$)");
        String url = xml.replace("&amp;", "&");
        Matcher m = r.matcher(url);
        Map<String, String> paramsMap = new HashMap<>();

        while (m.find()) {
            paramsMap.put(m.group(1), m.group(2));
        }
        return paramsMap;
    }

    /**
     * 返回filename
     *
     * @return
     */
    public String getFileName() {
        return xml.substring(0, xml.indexOf("?"));
    }
}
