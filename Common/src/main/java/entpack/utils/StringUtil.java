package entpack.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	/**
	 * 生成长度为8位的UUID(字符数组)
	 */
	private static String[] shortChars = new String[]{"a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z"};

	/**
	 * 转化百分比数值为字符串
	 *
	 * @param finishRate 百分比数值
	 * @return 返回字符串
	 */
	public static String convertFinishRate(BigDecimal finishRate) {
		if (finishRate.compareTo(BigDecimal.valueOf(1)) > -1)
			return "100%";
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(0);
		return nf.format(finishRate);
	}

	/**
	 * 生成长度为8位的UUID
	 *
	 * @return 返回8位UUID
	 */
	public static String shortUUID() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(shortChars[x % 0x3E]);
		}
		return shortBuffer.toString();
	}

	/**
	 * 获取当前时间戳字符串
	 *
	 * @return 返回字符串
	 */
	public static String currentNowStr() {
		return String.valueOf(new Date().getTime());
	}

	/**
	 * 分转金额
	 *
	 * @param integer 分值
	 * @return 返回金额(两位小数)
	 */
	public static String toAmount(Integer integer) {
		if (integer == 0)
			return "0.00";
		return ((float) integer / 100) + "";
	}

	/**
	 * 去掉字符串的空格
	 *
	 * @param str 源字符串
	 * @return 返回新字符串
	 */
	public static String trim(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 获取唯一标识符
	 *
	 * @return 返回字符串
	 */
	public static String UUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 获取唯一标识符(不含-号)
	 *
	 * @return 返回字符串
	 */
	public static String UUID2() {
		return UUID().replace("-", "");
	}

	/**
	 * 获取随机数
	 *
	 * @param n 位数
	 * @return 返回字符串
	 */
	public static String Rand(int n) {
		int ans = 0;
		while (Math.log10(ans) + 1 < n)
			ans = (int) (Math.random() * Math.pow(10, n));
		return ans + "";
	}

	public static boolean areNotEmpty(String... strings) {
		if (strings == null || strings.length == 0)
			return false;

		for (String string : strings) {
			if (string == null || "".equals(string)) {
				return false;
			}
		}
		return true;
	}

	public static boolean areNotBlank(String... strings) {
		if (strings == null || strings.length == 0)
			return false;

		for (String string : strings) {
			if (string == null || "".equals(string.trim())) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmpty(String string) {
		return !isNotEmpty(string);
	}

	public static boolean isNotEmpty(String string) {
		return string != null && !string.equals("");
	}

	public static boolean isNotBlank(String string) {
		return string != null && !string.trim().equals("");
	}

	public static boolean isBlank(String string) {
		return org.jsoup.helper.StringUtil.isBlank(string);
	}

	public static long toLong(String value, Long defaultValue) {
		try {
			if (value == null || "".equals(value.trim()))
				return defaultValue;
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n"))
				return -Long.parseLong(value.substring(1));
			return Long.parseLong(value);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static int toInt(String value) {
		return toInt(value, 0);
	}

	public static int toInt(String value, int defaultValue) {
		try {
			if (value == null || "".equals(value.trim()))
				return defaultValue;
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n"))
				return -Integer.parseInt(value.substring(1));
			return Integer.parseInt(value);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static BigInteger toBigInteger(String value, BigInteger defaultValue) {
		try {
			if (value == null || "".equals(value.trim()))
				return defaultValue;
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n"))
				return new BigInteger(value).negate();
			return new BigInteger(value);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static boolean match(String string, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(string);
		return matcher.matches();
	}

	public static boolean isNumeric(String str) {
		if (str == null)
			return false;
		for (int i = str.length(); --i >= 0; ) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

	public static String escapeHtml(String text) {
		if (isBlank(text))
			return text;

		return text.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;").replace("/", "&#x2F;");
	}

	public static Integer boolToInt(String bool) {
		if (isBlank(bool))
			return 0;
		return bool.toLowerCase().equals("true") ? 1 : 0;
	}

	/**
	 * 刪除字首
	 *
	 * @param source 源字符串
	 * @param prefix 字首
	 * @return 返回字符串
	 */
	public static String trimPrefix(String source, String prefix) {
		if (isNotBlank(prefix)) {
			if (!source.startsWith(prefix)) {
				return source;
			}
			return source.substring(prefix.length(), source.length());
		}
		return source;
	}

	/**
	 * 刪除后缀
	 *
	 * @param source 源字符串
	 * @param suffix 后缀
	 * @return 返回字符串
	 */
	public static String trimSuffix(String source, String suffix) {
		if (isNotBlank(suffix)) {
			if (!source.endsWith(suffix)) {
				return source;
			}
			return source.substring(0, source.length() - suffix.length());
		}
		return source;
	}

	/**
	 * 判断值在字符串中是否存在
	 *
	 * @param str 字符串
	 * @param val 需判断的值
	 * @return 返回布尔值
	 */
	public static boolean contains(String str, String val) {
		if (areNotBlank(str, val)) {
			return str.contains(val);
		}
		return false;
	}

	/**
	 * 刪除双斜杠
	 *
	 * @param url URL地址
	 * @return 返回字符串
	 */
	public static String removeSlash2(String url) {
		if (StringUtil.isNotBlank(url) && url.contains("//")) {
			return url.replaceAll("//", "/");
		}
		return url;
	}

	/**
	 * 从字符串的指定位置截取指定长度的子字符串
	 *
	 * @param str    原字符串
	 * @param length 截取长度
	 * @return 字符串
	 */
	public static String cut(String str, int length) {
		return cut(str, 0, length, "…");
	}

	/**
	 * 从字符串的指定位置截取指定长度的子字符串
	 *
	 * @param str        原字符串
	 * @param startIndex 截取开始
	 * @param length     截取长度
	 * @param symbol     符号，例如省略号，默认为空
	 * @return 字符串
	 */
	public static String cut(String str, int startIndex, int length, String symbol) {
		if (!isNotBlank(str)) {
			return "";
		}
		if (startIndex >= 0) {
			if (length < 0) {
				length = length * -1;
				if (startIndex - length < 0) {
					length = startIndex;
					startIndex = 0;
				} else {
					startIndex = startIndex - length;
				}
			}

			if (startIndex > str.length()) {
				return "";
			}
		} else {
			if (length < 0) {
				return "";
			} else {
				if (length + startIndex > 0) {
					length = length + startIndex;
					startIndex = 0;
				} else {
					return "";
				}
			}
		}

		if (str.length() - startIndex < length) {
			length = str.length() - startIndex;
		}
		return str.substring(startIndex, length) + (str.length() > length ? symbol : "");
	}

	/**
	 * 将List<String>转成带符号分割的字符串
	 *
	 * @param list   数组
	 * @param symbol 分割符号
	 * @return 返回字符串
	 */
	public static String listToString(List<String> list, String symbol) {
		if (list == null) {
			return null;
		}
		if (isBlank(symbol)) {
			symbol = ",";
		}
		StringBuilder result = new StringBuilder();
		boolean flag = false;
		for (String string : list) {
			if (flag) {
				result.append(symbol);
			} else {
				flag = true;
			}
			result.append(string);
		}
		return result.toString();
	}

	/**
	 * 补齐位数并返回字符串
	 *
	 * @param num  待补齐的数字
	 * @param size 一共多少位
	 * @return 补齐之后的字符串或者null
	 */
	public static String paddedString(int num, int size) {
		int numsize = String.valueOf(num).length();
		if (numsize > size)
			return null;

		String res = String.valueOf(num);
		for (int i = 0; i < size - numsize; i++) {
			res = "0" + res;
		}

		return res;
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 *
	 * @param v     需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_CEILING).doubleValue();
	}

	/**
	 * 首字母变小写
	 */
	public static String firstCharToLowerCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'A' && firstChar <= 'Z') {
			char[] arr = str.toCharArray();
			arr[0] += ('a' - 'A');
			return new String(arr);
		}
		return str;
	}

	/**
	 * 首字母变大写
	 */
	public static String firstCharToUpperCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'a' && firstChar <= 'z') {
			char[] arr = str.toCharArray();
			arr[0] -= ('a' - 'A');
			return new String(arr);
		}
		return str;
	}

	public static boolean notNull(Object... paras) {
		if (paras == null) {
			return false;
		}
		for (Object obj : paras) {
			if (obj == null) {
				return false;
			}
		}
		return true;
	}

	public static String toCamelCase(String stringWithUnderline) {
		if (stringWithUnderline.indexOf('_') == -1) {
			return stringWithUnderline;
		}

		stringWithUnderline = stringWithUnderline.toLowerCase();
		char[] fromArray = stringWithUnderline.toCharArray();
		char[] toArray = new char[fromArray.length];
		int j = 0;
		for (int i = 0; i < fromArray.length; i++) {
			if (fromArray[i] == '_') {
				// 当前字符为下划线时，将指针后移一位，将紧随下划线后面一个字符转成大写并存放
				i++;
				if (i < fromArray.length) {
					toArray[j++] = Character.toUpperCase(fromArray[i]);
				}
			} else {
				toArray[j++] = fromArray[i];
			}
		}
		return new String(toArray, 0, j);
	}

	public static String join(String[] stringArray) {
		StringBuilder sb = new StringBuilder();
		for (String s : stringArray) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static String join(String[] stringArray, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < stringArray.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(stringArray[i]);
		}
		return sb.toString();
	}

	public static boolean equals(String a, String b) {
		return a == null ? b == null : a.equals(b);
	}

	public static String getRandomUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 检查字符串中是否有符号
	 *
	 * @param str 待检查的字符串
	 * @return 返回true为有符号
	 */
	public static boolean hasSymbol(String str) {
//		String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
		String regEx = "^([a-zA-Z0-9]+)$";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		boolean b = m.find();
//		System.out.println(b);
		return b;
	}

	/**
	 * 直接删除多余的小数位，如2.35会变成2.3
	 *
	 * @param amount
	 * @return
	 */
	public static double roundDown(double amount,int num) {
		return new BigDecimal(amount).setScale(num, BigDecimal.ROUND_DOWN).doubleValue();//保留两位小数
	}

	/**
	 * 重复字符串 N 次
	 * @param str
	 * @param n
	 * @return
	 */
	public static String repeatN(String str,int n) {
		if (n <= 0) {
			return "";
		}

		String result = "";
		for (int i = 1; i <= n; i++) {
			result += str;
		}
		return result;
	}
}
