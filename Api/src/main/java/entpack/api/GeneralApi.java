package entpack.api;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import entpack.bean.EnterGameInfo;
import entpack.bean.GameInfo;
import entpack.bean.MemberInfo;
import entpack.service.BaseService;
import entpack.service.RedisTicketService;
import entpack.utils.DateUtil;
import entpack.utils.RedisLock;
import entpack.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class GeneralApi {

	private static Logger logger = LoggerFactory.getLogger("general_api");

	//多少天前有进入过游戏的
	private static final int BALANCE_DAY = -1;

	/**
	 * 会员游戏余额
	 *
	 * @param api
	 * @param userName
	 * @param platform
	 * @return
	 */
	public static double getGameBalance(String api, String userName, String platform) {
		double balance = 0;
		GameInfo gameInfo;
		try {
			switch (api) {

				case "apollo": case "apollosgd":
					gameInfo = BaseService.getGameInfo(api, platform);
					balance = ApolloApi.getInstance(gameInfo.getCurrency()).getBalance(userName);
					break;

				default:

					break;
			}


		} catch (Exception ex) {
			String errorMsg = String.format("getGameBalance error userName:%s api:%s", userName, api);
			System.out.println(errorMsg + " msg:" + RedisTicketService.getExceptionToString(ex));
			logger.error(errorMsg, ex);
		}

		return balance;
	}

	/**
	 * 会员游戏余额
	 *
	 * @param memberId
	 * @return
	 */
	public static double getGameBalance(String memberId) {
		double balance = 0;
		try {
			EnterGameInfo lastRecord = getMemberLastEnterGame(memberId);
			if (lastRecord == null) {
				balance = 0;
				return balance;
			}
			String userName = lastRecord.getUserName();
			String api = lastRecord.getApi();

			balance = getGameBalance(api, userName, lastRecord.getPlatform());

		} catch (Exception ex) {
			String errorMsg = String.format("getGameBalance error memberId:%s", memberId);
			System.out.println(errorMsg + "   msg:" + RedisTicketService.getExceptionToString(ex));
			logger.error(errorMsg, ex);
		}

		return balance;
	}


	/**
	 * 从游戏中提出
	 *
	 * @param txnId
	 * @param memberId
	 */
	public static Ret withdraw2Balance(String txnId, String memberId) {

		MemberInfo memberInfo = BaseService.getMemberInfo(memberId);
		if (memberInfo == null) {
			return Ret.fail();
		}

		String userName = memberInfo.getUserName();
		EnterGameInfo lastRecord = getMemberLastEnterGame(memberId);

		//如果没进入游戏记录，直接修改余额
		if (lastRecord == null) {
			return Ret.ok().set("balance", memberInfo.getCreditsBalance());
		}

		Ret ret = withdraw2Balance(txnId, memberId, userName, lastRecord.getApi(), lastRecord.getPlatform(), lastRecord.getGameName());
		if (ret.isFail()) {
			ret.set("balance", memberInfo.getCreditsBalance());
		}
		ret.set("api", lastRecord.getApi());
		return ret;
	}

	/**
	 * 从游戏中提出
	 *
	 * @param txnId
	 * @param memberId
	 */
	public static Ret withdraw2Balance(String txnId, String memberId, String userName, String api, String platform, String gameName) {

		String key = "withdraw2Balance:" + txnId;
		if (!RedisLock.lock(key, 60)) {
			return Ret.fail("txnId is exists");
		}

		//有进入游戏记录，从最后一次游戏记录提现，修改平台余额，再充
		double amount = 0;
		double balance = 0;
		GameInfo gameInfo;
		try {

			Ret result = null;
			switch (api) {

				case "apollo": case "apollosgd":
					gameInfo = BaseService.getGameInfo(api, platform);
					result = ApolloApi.getInstance(gameInfo.getCurrency())
							.withdraw2Balance(txnId, memberId, userName, platform, gameName);
					break;

				default:

					break;
			}
			if (result != null && result.isOk()) {
				amount += result.getDouble("amount");
				balance += result.getDouble("balance");
			}
		} catch (Exception ex) {
			String errorMsg = String.format("withdraw2Balance error memberId:%s", memberId);
			System.out.println(errorMsg + "   msg:" + RedisTicketService.getExceptionToString(ex));
			logger.error(errorMsg, ex);
			return Ret.fail("msg", errorMsg).set("balance", balance);
		} finally {
			logger.info("withdraw2Balance memberId:{} api:{} amount:{}", memberId, api, amount);
		}

		return Ret.ok().set("amount", amount).set("balance", balance);
	}

	/**
	 * 充值到游戏
	 *
	 * @param memberId
	 */
	public static Ret deposit2Game(String txnId, String memberId, String userName, String api, String platform) {

		String key = "deposit2Game:" + txnId;
		if (!RedisLock.lock(key, 60)) {
			return Ret.fail("txnId is exists");
		}

		GameInfo gameInfo;
		//有进入游戏记录，从最后一次游戏记录提现，修改平台余额，再充
		try {
			switch (api) {
				case "apollo": case "apollosgd":
					gameInfo = BaseService.getGameInfo(api, platform);
					ApolloApi.getInstance(gameInfo.getCurrency()).deposit2Game(txnId, memberId, userName, platform);
					break;
				default:
					break;
			}

		} catch (Exception ex) {
			String errorMsg = String.format("deposit2Game error memberId:%s", memberId);
			System.out.println(errorMsg + "   msg:" + RedisTicketService.getExceptionToString(ex));
			logger.error(errorMsg, ex);
		} finally {
			logger.info("deposit2Game memberId:{}", memberId);
		}
		return Ret.ok();

	}

	/**
	 * 生成请求参数
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static String createGetDataUrl(String url, Map<String, String> params) {
		StringBuilder sb = new StringBuilder(url);
		sb.append("?");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue());
			sb.append("&");
		}
		if (params.size() > 0) {
			sb.substring(0, sb.length() - 1);
		}
		return sb.toString();
	}

	public static String createPostDataUrl(String url, Map<String, String> params) {
		StringBuilder sb = new StringBuilder(url);
		sb.append("?");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue());
			sb.append("&");
		}
		if (params.size() > 0) {
			sb.substring(0, sb.length() - 1);
		}
		return sb.toString();
	}

	public static String createPostDataUrlJson(String url, Map<String, Object> params) {
		StringBuilder sb = new StringBuilder(url);
		sb.append("?");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue());
			sb.append("&");
		}
		if (params.size() > 0) {
			sb.substring(0, sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 手动更新余额
	 *
	 * @param memberId
	 * @param amt
	 */
	public static Ret manualRefreshBalance(String txnId, String memberId, String amt) {

		GameInfo gameInfo;
		MemberInfo memberInfo = BaseService.getMemberInfo(memberId);
		if (memberInfo == null) {
			return (Ret.fail());
		}

		String key = "manualRefreshBalance:" + txnId;
		if (!RedisLock.lock(key, 60)) {
			return (Ret.fail("txnId is exists"));
		}

//		double userBalance = memberInfo.getCreditsBalance();
		double amt_double = Double.parseDouble(amt);
//		//如果等于当前余额，跳过处理
//		if (userBalance == amt_double) {
//			renderJson(Ret.ok());
//			return;
//		}

		String userName = memberInfo.getUserName();
		EnterGameInfo lastRecord = getMemberLastEnterGame(memberId);

		//如果没进入游戏记录，直接修改余额
		if (lastRecord == null) {
//			BaseService.resetBalance(memberId, amt_double, StringUtil.shortUUID(), "manualRefreshBalance reset", "");
			return Ret.ok();
		}

		//有进入游戏记录，从最后一次游戏记录提现，修改平台余额，再充
		try {
			String api = lastRecord.getApi();
			String gameType = lastRecord.getGameType();
			String platform = lastRecord.getPlatform();
			switch (api) {

				case "apollo": case "apollosgd":
					gameInfo = BaseService.getGameInfo(api, platform);
					ApolloApi.getInstance(gameInfo.getCurrency()).deposit2Game(txnId, memberId, userName, platform);
					break;
				default:
					break;
			}

		} catch (Exception ex) {
			String errorMsg = String.format("manualRefreshBalance error memberId:%s,amt:%s", memberId, amt_double);
			System.out.println(errorMsg + "   msg:" + RedisTicketService.getExceptionToString(ex));
			logger.error(errorMsg, ex);
			return Ret.fail("msg", errorMsg);
		} finally {
			logger.info("manualRefreshBalance memberId:{},amt:{}", memberId, amt_double);
		}

		return Ret.ok();
	}


	/**
	 * 获取会员最后一次进入游戏
	 *
	 * @param memberId
	 * @return
	 */
	public static EnterGameInfo getMemberLastEnterGame(String memberId) {
		return BaseService.queryLastEnterGame(memberId);
	}


	/**
	 * 回收所有余额
	 */
	public static Ret tranAllBalance(String memberId) {
		MemberInfo memberInfo = BaseService.getMemberInfo(memberId);
		String userName = memberInfo.getUserName();
		List<Record> games = Db.use("member")
				.find("SELECT api,gameType,platform,name_zh " +
						"from tb_game_info " +
						"where api in ('gs','gs2','gs3','fg','evo','mega')");
		String resultTxt = "";
		for (Record game : games) {

			String api = game.getStr("api");
			String platform = game.getStr("platform");
			String gameName = game.getStr("name_zh");
			String txnId = StringUtil.shortUUID();
			Ret result = withdraw2Balance(txnId, memberId, userName, api, platform, gameName);
			if (result.isOk()) {
				resultTxt += api + ":" + game.getStr("platform") + ":"
						+ game.getStr("gameType") + ":" + result.getDouble("amount") + "\n";

			}
		}
		logger.info("tranAllBalance memberId:{} result:{}", memberId, resultTxt);
		return Ret.ok().set("resultTxt", resultTxt);
	}

	public static List<Map<String, Object>> queryAllBalance(String memberId, List<Record> games) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		MemberInfo memberInfo = BaseService.getMemberInfo(memberId);
		String userName = memberInfo.getUserName();

		Set<Callable<Map<String, Object>>> callables = new HashSet<>();

		for (Record game : games) {

			String api = game.getStr("api");
			String platform = game.getStr("platform");

			callables.add(() -> {
				try {
					double result = getGameBalance(api, userName, platform);
					Map<String, Object> kv = new HashMap<>();
					kv.put("platform", platform);
					kv.put("balance", result);
					kv.put("api", api);
					return kv;
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return null;
			});
		}
		List<Map<String, Object>> balanceResults = new ArrayList<>();
		try {
			List<Future<Map<String, Object>>> results = executorService.invokeAll(callables);
			executorService.shutdown();
			try {
				while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
					System.out.println("awaitTermination");
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			for (Future<Map<String, Object>> result : results) {
				Map<String, Object> resultMap = result.get();
				if (resultMap != null) {
					balanceResults.add(resultMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("tranAllBalance memberId:{} result:{}", memberId, JSONObject.toJSONString(balanceResults));
		return balanceResults;
	}

	/**
	 * 记录游戏launch日志
	 *
	 * @param userName
	 * @param url
	 * @param result
	 */
	public static void saveLaunchLog(String userName, String url, JSONObject result) {

		try {

			String resultTxt;
			if (result != null) {
				resultTxt = result.toJSONString();
			} else {
				resultTxt = "";
			}
			Record log = new Record();
			log.set("userName", userName);
			log.set("getUrl", url);
			log.set("getResult", resultTxt);
			log.set("getResult", resultTxt);

			if (result != null) {
				String errCode = result.getString("errCode");
				String gameUrl = result.getString("gameUrl");
				String errMsg = result.getString("errMsg");

				log.set("errCode", errCode);
				log.set("gameUrl", gameUrl);
				log.set("errMsg", errMsg);
			}

			Db.save("api_launch_log", log);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("saveLaunchLog.error", ex);
		}
	}

	public void refreshBalance(String txnId, String memberId) {

		MemberInfo memberInfo = BaseService.getMemberInfo(memberId);
		String userName = memberInfo.getUserName();

		Date startDate = DateUtil.addHour(Calendar.DAY_OF_MONTH, BALANCE_DAY);

		//1天内有进入过游戏的，最多5个
		List<Record> games = Db.find("SELECT * " +
				"FROM(SELECT api,platform,gameName,max(createDate) createDate" +
				"        FROM api_enterGame_log " +
				"     WHERE memberId=? AND createDate >= ? " +
				"     GROUP BY api,platform,gameName )t " +
				"ORDER BY createDate DESC " +
				"LIMIT 5", memberId, startDate);

		List<Map<String, Object>> results = GeneralApi.queryAllBalance(memberId, games);

		for (Map<String, Object> result : results) {
			String api = result.get("api").toString();
			String platform = result.get("platform").toString();
			String gameName = result.get("gameName") == null ? "" : result.get("gameName").toString();
			Double balance = (Double) result.get("balance");

			if (balance != null && balance >= 1) {

				//提现到余额
				Ret ret = GeneralApi.withdraw2Balance(txnId + api, memberId, userName, api, platform, gameName);
				if (ret.getStr("state").equals("ok")) {
					result.put("amount", ret.getFloat("amount"));
				}
				result.put("state", ret.getStr("state"));
			}
		}
	}

}
