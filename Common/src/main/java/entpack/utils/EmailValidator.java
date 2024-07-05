package entpack.utils;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * 邮箱有效性验证类
 */
public class EmailValidator {

//	public static void main(String[] args) {
//		System.out.println(isValidEmailAddress("51227887@qq.com"));
//	}

	/**
	 * 邮箱有效性验证
	 *
	 * @param email 待验证的邮箱
	 * @return 返回true为有效，否则为无效
	 */
	public static boolean isValidEmailAddress(String email) {
		int atSymbol = email.lastIndexOf("@");
		if (atSymbol == -1) {
			return false;
		}
		String user = email.substring(0, atSymbol);
		String domain = email.substring(atSymbol + 1, email.length());

		return isValidUser(user) &&
				isValidDomain(domain);
	}

	private static boolean isValidUser(String user) {
		return user.length() > 0;
	}

	private static boolean isValidDomain(String domain) {
		return hasRecord(domain, "MX") || hasRecord(domain, "A") || hasRecord(domain, "AAAA");
	}

	private static boolean hasRecord(String domain, String type) {
		try {
			final NamingEnumeration<?> records = getAllRecords(domain, type);
			if (records == null) {
				return false;
			}

			return records.hasMore();
		} catch (NameNotFoundException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}
	}

	private static NamingEnumeration<?> getAllRecords(String domain, String type) throws NamingException {
		final Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		final DirContext ictx = new InitialDirContext(env);
		final Attributes attrs = ictx.getAttributes(domain, new String[]{type});
		final Attribute attr = attrs.get(type);
		if (attr == null) {
			return null;
		}
		return attr.getAll();
	}
}