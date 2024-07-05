package entpack.utils;

import com.jfinal.core.JFinal;
import com.jfinal.log.Log;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class UrlUtil {

	private static final Log log = Log.getLog(UrlUtil.class);

	/**
	 * 生成URL编码（默认为时间戳）
	 *
	 * @return 返回URL编码
	 */
	public static String generateCode() {
		//获取时间戳
		String timestamp = System.currentTimeMillis() + "";
		//去掉末毫秒3位
		timestamp = timestamp.substring(0, timestamp.length() - 3);
		return timestamp;
	}

	/**
	 * URL解码
	 *
	 * @param string 待解码的URL
	 * @return 返回字符串
	 */
	public static String urlDecode(String string) {
		try {
			return URLDecoder.decode(string, JFinal.me().getConstants().getEncoding());
		} catch (UnsupportedEncodingException e) {
			log.error("urlDecode is error", e);
		}
		return string;
	}

	/**
	 * URL编码
	 *
	 * @param string 待编码的URL
	 * @return 返回字符串
	 */
	public static String urlEncode(String string) {
		try {
			return URLEncoder.encode(string, JFinal.me().getConstants().getEncoding());
		} catch (UnsupportedEncodingException e) {
			log.error("urlEncode is error", e);
		}
		return string;
	}

	/**
	 * 返回编码后的当前URL（含参数和值）
	 *
	 * @param request 请求对象
	 * @return 返回字符串
	 */
	public static String getRetUrl(HttpServletRequest request) {
		String currentUrl = request.getRequestURI();
		String queryStr = request.getQueryString();
		if (StringUtil.isNotEmpty(queryStr)) {
			currentUrl += "?" + queryStr;
		}
		return urlEncode(currentUrl);
	}

}
