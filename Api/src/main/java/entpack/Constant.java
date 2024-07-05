package entpack;


import com.jfinal.kit.PropKit;

import java.util.Arrays;
import java.util.List;

/**
 * 全局常量
 */
public class Constant {

	public static String getCurrency() {
		return PropKit.get("currency");
	}

	public static String getAppKey() {
		return PropKit.get("appKey");
	}

	public static String getAppSecret() {
		return PropKit.get("appSecret");
	}

	/**
	 * 不刷新信用体系的白牌或代理ID
	 */
	public static final List<String> NO_CREDITS_USERID = Arrays.asList("NeRle8UO", "DtckkV2N");

	/**
	 * 充值最大金额，超出需要人工确认
	 *
	 * @return
	 */
	public static int getOrderMaxAmount() {
		String orderMaxAmount = PropKit.get("orderMaxAmount", "20000");
		return Integer.parseInt(orderMaxAmount);
	}

	/**
	 * 是否开发模式
	 *
	 * @return
	 */
	public static boolean getESTicketLog() {
		return PropKit.getBoolean("esTicketLog", false);
	}

	/**
	 * ES注单索引
	 *
	 * @return
	 */
	public static String getESTicket() {
		return PropKit.get("es_ticket", "http://10.10.243.51:9200/ticket_log");
	}

	public static class GameConfig {
//		public static String get(String key) {
//			return PropKit.use("gameConfig.txt").get(key);
//		}

		/**
		 * 获取系统变量
		 *
		 * @param name
		 * @return
		 */
		public static String get(String name) {

			String val = PropKit.use("gameConfig.txt").get(name);
			if (val == null) {
				return val;
			}

			if (val.indexOf("${") > -1) {
				// 将${name}替换成name，然后再取环境变量
				return System.getenv(val.replace("${", "")
						.replace("}", ""));
			}
			return val;
		}

		public static String getEnv(String name){
			return System.getenv(name);
		}
	}

	/**
	 * 构建redisKey
	 *
	 * @param key
	 * @param params
	 * @return
	 */
	public static String buildRedisKey(String key, String... params) {
		StringBuilder stringBuilder = new StringBuilder(key);
		for (String param : params) {
			stringBuilder.append(":").append(param);
		}
		return stringBuilder.toString();
	}

	/**
	 * 当前余额及占成redisKey
	 *
	 * @param memberId
	 * @param pkId
	 * @return
	 */
	public static String getGameBalanceKey(String memberId, String pkId) {
		return buildRedisKey("gameBalance", memberId, pkId);
	}

	/**
	 * 代理占成
	 *
	 * @param agentId
	 * @param gameId
	 * @return
	 */
	public static String getAgentConfigListKey(String agentId, String gameId) {
		return buildRedisKey("AgentConfigList", agentId, gameId);
	}

	/**
	 * 白牌占成
	 *
	 * @param masterId
	 * @param gameId
	 * @return
	 */
	public static String getMasterConfigListKey(String masterId, String gameId) {
		return buildRedisKey("MasterConfigList", masterId, gameId);
	}

	/**
	 * 白牌代理所有下级占成
	 *
	 * @param md5
	 * @return
	 */
	public static String getMasterAgentConfigListKey(String md5) {
		return buildRedisKey("MasterAgentConfigList", md5);
	}

	/**
	 * 获取系统变量
	 *
	 * @param name
	 * @return
	 */
	public static String get(String name) {

		String val = PropKit.get(name);
		if (val == null) {
			return val;
		}

		if (val.indexOf("${") > -1) {
			// 将${name}替换成name，然后再取环境变量
			return System.getenv(val.replace("${", "")
					.replace("}", ""));
		}
		return val;
	}
}
