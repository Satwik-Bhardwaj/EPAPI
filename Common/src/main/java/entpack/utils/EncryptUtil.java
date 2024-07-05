package entpack.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

import static com.jfinal.kit.HashKit.md5;

/**
 * 加解密
 */
public class EncryptUtil {

	/**
	 * 获取盐
	 *
	 * @return 盐
	 */
	public static String salt() {
		//int random = (int) (10 + (Math.random() * 10));
		return StringUtil.UUID().replace("-", "").substring(0, 6);// 随机长度
	}

	/**
	 * 加密密碼（与Discuz一致，md5(md5加密+6位盐)）
	 *
	 * @param password
	 * @param salt
	 * @return
	 */
	public static String encryptPassword(String password, String salt) {
		return md5(md5(password) + salt);
	}

	/**
	 * 验证密碼（与Discuz一致，md5(md5加密+6位盐)）
	 *
	 * @param userPassword 用户密碼
	 * @param userSalt     盐
	 * @param password     密碼
	 * @return
	 */
	public static boolean verifyPassword(String userPassword, String userSalt, String password) {
		if (userPassword == null)
			return false;
		if (userSalt == null)
			return false;
		return userPassword.equals(encryptPassword(password, userSalt));
	}

	public static String generateUcode(BigInteger id, String salt) {
		return md5(id + salt);
	}

	public static String signForRequest(Map<String, String> params, String secret) {
		String[] keys = params.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		StringBuilder query = new StringBuilder();
		query.append(secret);
		for (String key : keys) {
			String value = params.get(key);
			if (StringUtil.areNotEmpty(key, value)) {
				query.append(key).append(value);
			}
		}
		query.append(secret);
		return md5(query.toString()).toUpperCase();
	}

	public static void main(String[] args) {
		System.out.println(EncryptUtil.encryptPassword("admin", "fe37a1"));
	}

}
