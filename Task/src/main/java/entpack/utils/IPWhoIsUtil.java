package entpack.utils;

import com.jfinal.kit.PropKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class IPWhoIsUtil {

	private final static String host = PropKit.get("IPWhois.host");
	private final static String key = PropKit.get("IPWhois.key");

	private static Logger logger = LoggerFactory.getLogger(IPWhoIsUtil.class);

	public static String check(String ip) {

		String url = host.replace("{ip}", ip).replace("{key}", key);

		logger.info("check.url:{}" , url);
		return OkHttpUtil.get(url, new HashMap<>());

	}


}
