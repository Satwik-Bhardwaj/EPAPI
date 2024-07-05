package entpack.service;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import entpack.Constant;
import entpack.bean.*;
import entpack.utils.DateUtil;
import entpack.utils.MD5Util;
import entpack.utils.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class BaseService {

	private static final Logger logger = LoggerFactory.getLogger("ticket");

	public static final String CURRENCY = Constant.getCurrency();

	public static boolean gameStop = false;
	public static Map<String, Boolean> gameStopMap = new HashMap<>();

	/**
	 * 游戏ID
	 *
	 * @return
	 */
	protected abstract String getGameId();

	/**
	 * 记录注单
	 *
	 * @param memberId  会员信息ID
	 * @param gameType  游戏gameType
	 * @param amount    有效金额
	 * @param amountBet 下注金额
	 * @param amountWL  赢输金额
	 * @param betNum    注数
	 * @param detail    明细，JSON
	 */
	public static void pushTicket(String id,
	                              String memberId,
	                              String gameType,
	                              double amount,
	                              double amountBet,
	                              double amountWL,
	                              int betNum,
	                              Date createDate,
	                              String detail) {
		pushTicket(id, memberId, gameType, null, amount, amountBet,
				amountWL, betNum, createDate, detail);
	}

	/**
	 * 构建redis注单
	 *
	 * @param id
	 * @param memberId
	 * @param gameType   游戏类型
	 * @param platform   游戏平台
	 * @param amount     有效金额
	 * @param amountBet  下注金额
	 * @param amountWL   赢输
	 * @param betNum     注数
	 * @param createDate 下注时间
	 * @param detail     明细，JSON
	 * @return
	 */
	public static RedisTicket buildTicket(String id,
	                                      String memberId,
	                                      String gameType,
	                                      String platform,
	                                      double amount,
	                                      double amountBet,
	                                      double amountWL,
	                                      int betNum,
	                                      Date createDate,
	                                      String detail) {
		MemberInfo memberInfo = getMemberInfo(memberId);
		String gameId;
		if (platform == null) {
			gameId = getGameId(gameType);
		} else {
			gameId = getGameId(gameType, platform);
		}
		if (gameId == null) {
			return null;
		}
		String dt = DateUtil.formatDate(createDate);
		RedisTicket ticket = new RedisTicket(id, gameId, memberId, dt,
				amount, amountBet, amountWL, betNum, memberInfo.getCreditsBalance());
		ticket.setDetail(detail);
		return ticket;
	}


	/**
	 * 构建redis注单
	 *
	 * @param id
	 * @param memberId
	 * @param gameType   游戏类型
	 * @param platform   游戏平台
	 * @param amount     有效金额
	 * @param amountBet  下注金额
	 * @param amountWL   赢输
	 * @param betNum     注数
	 * @param createDate 下注时间
	 * @param detail     明细，JSON
	 * @return
	 */
	public static RedisTicket buildTicket(String api,
	                                      String id,
	                                      String memberId,
	                                      String gameType,
	                                      String platform,
	                                      double amount,
	                                      double amountBet,
	                                      double amountWL,
	                                      int betNum,
	                                      Date createDate,
	                                      String detail) {
		return buildTicket(api, id, memberId, gameType, platform, amount, amountBet, amountWL, betNum, createDate, detail, null);
	}

	/**
	 * 构建redis注单
	 *
	 * @param id
	 * @param memberId
	 * @param gameType   游戏类型
	 * @param platform   游戏平台
	 * @param amount     有效金额
	 * @param amountBet  下注金额
	 * @param amountWL   赢输
	 * @param betNum     注数
	 * @param createDate 下注时间
	 * @param detail     明细，JSON
	 * @return
	 */
	public static RedisTicket buildTicket(String api,
	                                      String id,
	                                      String memberId,
	                                      String gameType,
	                                      String platform,
	                                      double amount,
	                                      double amountBet,
	                                      double amountWL,
	                                      int betNum,
	                                      Date createDate,
	                                      String detail,
	                                      String currency) {
		MemberInfo memberInfo = getMemberInfo(memberId);
		String gameId;
		if (platform == null) {
			gameId = getGameId(gameType);
		} else if (currency == null) {
			gameId = getGameId(api, gameType, platform);
		} else {
			gameId = getGameId(api, gameType, platform);
		}
		if (gameId == null) {
			return null;
		}
		String dt = DateUtil.formatDate(createDate);
		RedisTicket ticket = new RedisTicket(id, gameId, memberId, dt,
				amount, amountBet, amountWL, betNum, memberInfo.getCreditsBalance());
		ticket.setDetail(detail);
		return ticket;
	}

	/**
	 * 记录注单
	 *
	 * @param memberId  会员信息ID
	 * @param gameType  游戏gameType
	 * @param platform  游戏platform
	 * @param amount    有效金额
	 * @param amountBet 下注金额
	 * @param amountWL  赢输金额
	 * @param betNum    注数
	 * @param detail    明细
	 */
	public static boolean pushTicket(String id,
	                                 String memberId,
	                                 String gameType,
	                                 String platform,
	                                 double amount,
	                                 double amountBet,
	                                 double amountWL,
	                                 int betNum,
	                                 Date createDate,
	                                 String detail) {
		MemberInfo memberInfo = getMemberInfo(memberId);
		String gameId;
		if (platform == null) {
			gameId = getGameId(gameType);
		} else {
			gameId = getGameId(gameType, platform);
		}
		if (gameId == null) {
			return false;
		}

		RedisTicketService.pushTicket(id, gameId, memberId,
				amount, amountBet, amountWL, betNum,
				memberInfo.getCreditsBalance(), createDate, detail);
		return true;
	}

	/**
	 * 加入到redis注单
	 *
	 * @param ticket
	 */
	public static void pushTicket(RedisTicket ticket) {
		RedisTicketService.pushTicket(ticket);
	}

	/**
	 * 通过游戏类型查询游戏ID
	 *
	 * @param gameType
	 * @return
	 */
	public static String getGameId(String gameType) {
		Cache redis = Redis.use();
		String key = String.format("tb_game_info:%s", gameType);
		String id = redis.get(key);
		if (id == null) {
			String sql = "select id from tb_game_info " +
					" where gameType=? order by sort limit 1";
			Record record = Db.use("member").findFirst(sql, gameType);

			if (record == null) {
				return null;
			}
			id = record.getStr("id");

			redis.set(key, id);
		}
		return id;
	}

	/**
	 * 通过游戏类型、平台查询游戏ID
	 *
	 * @param gameType
	 * @param platform
	 * @return
	 */
	public static String getGameId(String gameType, String platform) {
		Cache redis = Redis.use();
		String key = String.format("tb_game_info:%s:%s", gameType, platform);

		String id = redis.get(key);
		if (id == null) {
			String sql = "select id from tb_game_info " +
					" where gameType=? and platform=?";
			Record record = Db.use("member").findFirst(sql, gameType, platform);
			if (record == null) {
//				return getGameId(gameType);
				return null;
			}

			id = record.getStr("id");

			redis.set(key, id);
		}
		return id;
	}


	/**
	 * 通过游戏类型、平台查询游戏ID
	 *
	 * @param api
	 * @param gameType
	 * @return
	 */
	public static String getGameIdByGameType(String api, String gameType) {
		Cache redis = Redis.use();
		String key = String.format("tb_game_info:%s:%s", api, gameType);

		String id = redis.get(key);
		if (id == null) {
			String sql = "select id from tb_game_info " +
					" where api=? and gameType=? ";
			Record record = Db.use("member").findFirst(sql, api, gameType);
			if (record == null) {
				return null;
			}

			id = record.getStr("id");

			redis.set(key, id);
		}
		return id;
	}

	public static String getGameId(String api, String gameType, String platform, String currency) {
		Cache redis = Redis.use();
		String key = String.format("tb_game_info:%s:%s:%s:%s", api, gameType, platform, currency);

		String id = redis.get(key);
		if (id == null) {
			String sql = "select id from tb_game_info " +
					" where api=? and gameType=? and platform=? and currency=?";
			Record record = Db.use("member").findFirst(sql, api, gameType, platform, currency);
			if (record == null) {
				return null;
			}
			id = record.getStr("id");
			redis.set(key, id);
		}
		return id;
	}

	/**
	 * 通过游戏类型、平台查询游戏ID
	 *
	 * @param api
	 * @param gameType
	 * @param platform
	 * @return
	 */
	public static String getGameId(String api, String gameType, String platform) {
		Cache redis = Redis.use();
		String key = String.format("tb_game_info:%s:%s:%s", api, gameType, platform);

		String id = redis.get(key);
		if (id == null) {
			String sql = "select id from tb_game_info " +
					" where api=? and gameType=? and platform=?";
			Record record = Db.use("member").findFirst(sql, api, gameType, platform);
			if (record == null) {
//				return getGameId(gameType);
				return null;
			}

			id = record.getStr("id");

			redis.set(key, id);
			redis.expire(key, 24 * 3600);
		}
		return id;
	}

	/**
	 * 通过ID 查询游戏数据
	 *
	 * @param gameId
	 * @return
	 */
	public static GameInfo getGameInfo(String gameId) {
		String sql = "select gameType,platform,name_zh,currency,multiple from tb_game_info " +
				" where id=?";
//		Record record = Db.use("member").findFirst(sql, gameId);
		String key = String.format("getGameInfo:%s", gameId);
		Record record = CacheService.getAndSetCacheObjByConfigName("member", key, sql, gameId);

		GameInfo gameInfo = new GameInfo();
		gameInfo.setGameType(record.getStr("gameType"));
		gameInfo.setPlatform(record.getStr("platform"));
		gameInfo.setName(record.getStr("name_zh"));
		gameInfo.setCurrency(record.getStr("currency"));
		gameInfo.setMultiple(record.getInt("multiple"));
		return gameInfo;
	}

	public static GameInfo getGameInfo(String api, String platform) {
		String sql = "select gameType,platform,name_zh,currency,multiple from tb_game_info " +
				" where api=? and platform=?";
//		Record record = Db.use("member").findFirst(sql, api, platform);
		String key = String.format("getGameInfo:%s:%s", api, platform);
		Record record = CacheService.getAndSetCacheObjByConfigName("member", key, sql, api, platform);

		GameInfo gameInfo = new GameInfo();
		gameInfo.setGameType(record.getStr("gameType"));
		gameInfo.setPlatform(record.getStr("platform"));
		gameInfo.setName(record.getStr("name_zh"));
		gameInfo.setCurrency(record.getStr("currency"));
		gameInfo.setMultiple(record.getInt("multiple"));
		return gameInfo;
	}

	/**
	 * 更新会员信用余额(带锁)
	 *
	 * @param pkId
	 * @param action
	 * @param amt
	 * @param memberId
	 * @param api
	 * @param roundId
	 * @param remark
	 * @return
	 */
	public BalanceResult updateGetCreditsBalanceLock(String pkId, String action, float amt, String memberId,
	                                                 String api, String roundId, String remark) {

		// 如果是下注的，判断是否被禁用
		if (amt < 0 && !allowPlay(memberId, api)) {
			Record result = new Record();
			result.set("result", 0);
			result.set("balance", 0);
			result.set("amt", 0);
			result.set("locked", false);
			return new BalanceResult(result, null);
		}

		// 查询会员占成
		MemberGameConfig config = CacheService.getMemberConfig(memberId, getGameId());

		if (config == null) {
			// 如果会员占成为空，更新余额失败
			logger.error("MemberInfo not found, memberId:{},gameId:{},id:{}", memberId, getGameId(), pkId);
			Record result = new Record();
			result.set("result", 0);
			result.set("balance", 0);
			result.set("amt", 0);
			result.set("locked", false);
			return new BalanceResult(result, null);
		}
		String key = String.format("lock:updateGetCreditsBalance:%s:%s:%s:%s:%s:%s",
				memberId, api, roundId, remark, amt, pkId);
		Record result;
		BalanceRollBack rollBack = null;
		if (RedisLock.lock(key, 3600 * 12)) {

			result = Db.use("member").findFirst("call updateBalance_Api(?,?,?,?,?,?)",
					memberId, amt, api, roundId, pkId, remark);
			if (result.getInt("result") > 0) {

				String setKey = Constant.getGameBalanceKey(memberId, pkId);

				// 转成jsonObj
				JSONObject jsonObject = (JSONObject) JSONObject.toJSON(config);
				jsonObject.put("balance", result.getFloat("balance"));

				// 存储1小时
				Redis.use("ticket").setex(setKey, 3600, jsonObject.toJSONString());

				rollBack = new BalanceRollBack(pkId, action, memberId, amt, api, roundId, remark);
			}
			result.set("locked", false);
		} /*else if (amt == 0) {
            // 如果是0的就更新吧
            result = Db.use("member").findFirst("call updateBalance_Api(?,?,?,?,? )",
                    memberId, amt, api, roundId, remark);
        } */ else {
			logger.info("updateGetCreditsBalance is lock," +
							"memberId:{}, api:{}, roundId:{}, remark:{}, amt:{}",
					memberId, api, roundId, remark, amt);

			MemberInfo memberInfo = getMemberInfo(memberId, api);
			double balance = memberInfo.getCreditsBalance();
			result = new Record();
			result.set("result", 1);
			result.set("balance", balance);
			result.set("amt", amt);
			result.set("locked", true);
		}

		if (gameStop || gameStopMap.containsKey(api)) {
			result.set("balance", 0);
		}

		BalanceResult balanceResult = new BalanceResult(result, rollBack);
		return balanceResult;
	}

	/**
	 * 更新会员信用余额(带所)
	 *
	 * @param amt
	 * @param memberId
	 */
	public static BalanceResult updateGetCreditsBalanceLockForGS(String pkId, String action, double amt, String memberId,
	                                                             String api, String roundId, String remark) {
		try {


			String key = String.format("lock:updateGetCreditsBalance:%s:%s:%s:%s:%s:%s",
					memberId, api, roundId, remark, amt, pkId);
			Record result;
			BalanceRollBack rollBack = null;
			if (RedisLock.lock(key, 3600 * 12)) {
				result = Db.use("member").findFirst("call updateBalance_Api(?,?,?,?,?,? )",
						memberId, amt, api, roundId, pkId, remark);
				if (result.getInt("result") > 0) {
					rollBack = new BalanceRollBack(pkId, action, memberId, amt,
							api, roundId, remark);
				}
				result.set("locked", false);
			} else {
				result = new Record();
				MemberInfo memberInfo = BaseService.getMemberInfo(memberId, api);
				double balance = memberInfo.getCreditsBalance();

				result.set("balance", balance);
				result.set("result", 1);
				result.set("amt", amt);
				result.set("locked", true);
			}

			BalanceResult balanceResult = new BalanceResult(result, rollBack);
			return balanceResult;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 重设会员余额
	 *
	 * @param memberId
	 * @param amt
	 * @param txnId
	 * @param remark
	 * @return
	 */
	public static boolean resetBalance(String memberId, double amt, String txnId, String remark, String api) {
		Record result = Db.use("member").findFirst("call resetBalance(?,?,?,?,?)",
				memberId, amt, txnId, remark, api);
		return result.getInt("result") > 0;
	}

	/**
	 * 更新会员信用余额
	 *
	 * @param amt
	 * @param memberId
	 */
	public static BalanceResult updateGetCreditsBalance(double amt, String memberId,
	                                                    String api, String roundId, String txId, String remark) {

		// 如果是下注的，判断是否被禁用
		if (amt < 0 && !allowPlay(memberId, api)) {
			Record result = new Record();
			result.set("result", 0);
			result.set("balance", 0);
			result.set("amt", 0);
			result.set("locked", false);
			return new BalanceResult(result, null);
		}

		Record result = Db.use("member").findFirst("call updateBalance_Api(?,?,?,?,?,?)",
				memberId, amt, api, roundId, txId, remark);

		if (gameStop || gameStopMap.containsKey(api)) {
			result.set("balance", 0);
		}


		BalanceResult balanceResult = new BalanceResult(result, null);
		return balanceResult;
	}

	/**
	 * 更新会员信用余额
	 *
	 * @param amt
	 * @param memberId
	 */
	public static boolean updateCreditsBalance(float amt, String memberId) {

		Record result = Db.use("member").findFirst("call updateBalance(?,? )",
				memberId, amt);

		int rows = result.getInt("result");
		return rows > 0;
	}

	/**
	 * 获取会员信息
	 *
	 * @param memberId
	 * @return
	 */
	public static MemberInfo getMemberInfo(String memberId) {
		return getMemberInfo(memberId, null);
	}

	/**
	 * 获取会员信息
	 *
	 * @param memberId
	 * @return
	 */
	public static MemberInfo getMemberInfo(String memberId, String api) {
		String sql = "select masterId,account,userName,fullName,memberType,creditsBalance,`status`," +
				"masterDirect,agentId,pwdText " +
				"from tb_member_info " +
				"where id=?";
		Record record = Db.use("member").findFirst(sql, memberId);
		MemberInfo memberInfo = new MemberInfo();
		memberInfo.setMasterId(record.getStr("masterId"));
		memberInfo.setUserName(record.getStr("account"));   //改为使用会员账号
		memberInfo.setUserNameOld(record.getStr("userName"));
		memberInfo.setFullName(record.getStr("fullName"));
		memberInfo.setMemberType(record.getInt("memberType"));
		memberInfo.setCurrency(CURRENCY);
		memberInfo.setStatus(record.getInt("status"));
		memberInfo.setMasterDirect(record.getInt("masterDirect"));
		memberInfo.setAgentId(record.getStr("agentId"));
		memberInfo.setPwdText(record.getStr("pwdText"));

		if (api != null && !allowPlay(memberId, api)) {
			// 判断是否允许玩该游戏
			memberInfo.setCreditsBalance(0);
		} else {
			// 2冻结，信用额度设置为0，不能让其游戏
			if (memberInfo.getStatus() == 2) {
				memberInfo.setCreditsBalance(0);
			} else {
				memberInfo.setCreditsBalance(record.getDouble("creditsBalance"));
			}
		}
		return memberInfo;
	}

	/**
	 * 获取会员对应白牌币种
	 *
	 * @param memberId
	 * @return
	 */
	public static String getMemberCurrency(String memberId) {

		String sql = " select tb_master_info.currency " +
				" from tb_member_info " +
				" inner join " +
				" tb_master_info " +
				" on tb_member_info.masterId=tb_master_info.id " +
				" where tb_member_info.id= ? ";

		Record record = Db.use("member").findFirst(sql, memberId);

		if (record != null) {
			return (record.getStr("currency") == null) ? null : record.getStr("currency").trim();
		}
		return null;
	}

	/**
	 * 判断会员游戏是否允许玩
	 *
	 * @param memberId 会员ID
	 * @param api      游戏ID
	 * @return 返回true为可玩, 否则不可玩
	 */
	public static boolean allowPlay(String memberId, String api) {

		//所有游戏都允许了
		return true;
/*
		// 先屏蔽sexy2
		if ("sexy2".equals(api))
			return true;

		// 先屏蔽sexy
		if ("sexy".equals(api))
			return true;

		// 先屏蔽awc
		if ("fg".equals(api))
			return true;

		// 先屏蔽pp
		if("pp".equals(api))
			return true;

		// 先屏蔽gs、gs2、gs3
		if ("gs".equals(api) || "gs2".equals(api) || "gs3".equals(api)) {
			return true;
		}

		//判断会员ID是否在黑名单
		Cache redis = Redis.use("cache");
		if (gameStop || gameStopMap.containsKey(api)) {
			return false;
		}

		if (redis.sismember("blacklist", memberId)) {
			return false;
		}

		// 加入缓存
		String key = String.format("tb_member_config:%s:%s", memberId, api);
		String strCC = redis.get(key);

		boolean allow;
		if (strCC == null) {
			Integer cc = Db.use("member").queryInt("SELECT 1 FROM tb_game_info p " +
							"JOIN tb_member_config c ON c.gameId = p.id " +
							"WHERE c.status = 0 AND c.memberId = ? AND p.api = ? " +
							"LIMIT 1",
					memberId, api);

			allow = !(cc != null && cc.equals(1));

			redis.set(key, allow ? "1" : "0");
			redis.expire(key, 3600);

		} else {
			allow = strCC.equals("1");
		}
		return allow;*/
	}

	/**
	 * 获取会员的游戏配置
	 *
	 * @param memberId
	 * @param tname
	 * @param gameCode
	 * @return
	 */
	public static Record getUserConfig(String memberId, String tname, String gameCode) {
		String sql = "select * from " + tname + "_config where gameCode=?";
		Record record = Db.use("member").findFirst(sql, gameCode);
		return record;
	}

	/**
	 * 创建账号
	 *
	 * @param model
	 */
	protected static void createUser(Record model, String tname) {

		String tableName = tname + "_create";
		model.set("createDate", new Date());
		insertOrUpdate(tableName, model);
	}

	/**
	 * 是否已经存在
	 *
	 * @param memberId
	 * @return
	 */
	protected static boolean exists(String memberId, String tname) {
		String sql = "select 1 from " + tname + "_create where memberId=?";
		Integer cc = Db.queryInt(sql, memberId);
		return cc != null && cc.equals(1);
	}

	/**
	 * 获取 user
	 *
	 * @param memberId
	 * @return
	 */
	protected static Record getUser(String memberId, String tname) {
		String sql = "select * from " + tname + "_create where memberId=?";
		Record record = Db.findFirst(sql, memberId);
		return record;
	}

	/**
	 * 插入并更新
	 *
	 * @param tableName
	 * @param model
	 */
	public static void insertOrUpdate(String tableName, Record model) {
		insertOrUpdate(tableName, model, null);
	}

	/**
	 * 插入并更新
	 *
	 * @param tableName
	 * @param model
	 */
	public static void insertOrUpdate(String tableName, Record model, String configName) {
		List<Object> params = new ArrayList<>();
		String sql = "insert into " + tableName + " (";
		for (String column : model.getColumnNames()) {
			sql += column + ",";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ") values (";
		for (Object columnValue : model.getColumnValues()) {
			sql += "?,";
			params.add(columnValue);
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ") on duplicate key update ";
		for (String column : model.getColumnNames()) {
			sql += column + "=?,";
		}
		sql = sql.substring(0, sql.length() - 1);

		// 复制多一份，因为更新也需要一样的值
		params.addAll(params);
		if (configName != null) {
			Db.use(configName).update(sql, params.toArray());
		} else {
			Db.update(sql, params.toArray());
		}
	}

	/**
	 * 数据签名
	 *
	 * @param models
	 * @return
	 */
	public static String signRecord(List<Record> models) {
		String json = JSONObject.toJSONString(models);
		return MD5Util.md5(json);
	}

	/**
	 * 数据签名
	 *
	 * @param model
	 * @return
	 */
	public static String signRecord(Record model) {
		String json = JSONObject.toJSONString(model);
		return MD5Util.md5(json);
	}


	/**
	 * 插入并更新 同步执行
	 *
	 * @param tableName
	 * @param models
	 */
	public static void insertOrUpdateSync(String tableName, List<Record> models) {

		// 先分组，以免有些字段长度不一样
		Map<String, List<Record>> groupList = new HashMap<>();

		for (Record model : models) {
			String columnNames = JSONObject.toJSONString(model.getColumnNames());
			List<Record> recordList = groupList.get(columnNames);
			if (recordList == null) {
				recordList = new ArrayList<>();
				groupList.put(columnNames, recordList);
			}

			recordList.add(model);
		}

		for (List<Record> value : groupList.values()) {
			List<Object> params = new ArrayList<>();
			StringBuilder sb = new StringBuilder("insert into " + tableName + " (");
			Record model = value.get(0);
			for (String column : model.getColumnNames()) {
				sb.append(column + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(") values ");

			for (Record record : value) {
				sb.append("(");

				for (Object columnValue : record.getColumnValues()) {
					sb.append("?,");
					params.add(columnValue);
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(") ,");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("on duplicate key update ");

			for (String column : model.getColumnNames()) {
				sb.append(String.format("%s=values(%s),", column, column));
			}
			sb.deleteCharAt(sb.length() - 1);

			Db.update(sb.toString(), params.toArray());
		}
	}

	/**
	 * 插入并更新
	 *
	 * @param tableName
	 * @param models
	 */
	public static void insertOrUpdate(String tableName, List<Record> models) {

		// 先分组，以免有些字段长度不一样
		Map<String, List<Record>> groupList = new HashMap<>();

		for (Record model : models) {
			String columnNames = JSONObject.toJSONString(model.getColumnNames());
			List<Record> recordList = groupList.get(columnNames);
			if (recordList == null) {
				recordList = new ArrayList<>();
				groupList.put(columnNames, recordList);
			}

			recordList.add(model);
		}

		for (List<Record> value : groupList.values()) {
			List<Object> params = new ArrayList<>();
			StringBuilder sb = new StringBuilder("insert into " + tableName + " (");
			Record model = value.get(0);
			for (String column : model.getColumnNames()) {
				sb.append(column + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(") values ");

			for (Record record : value) {
				sb.append("(");

				for (Object columnValue : record.getColumnValues()) {
					sb.append("?,");
					params.add(columnValue);
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(") ,");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("on duplicate key update ");

			for (String column : model.getColumnNames()) {
				sb.append(String.format("%s=values(%s),", column, column));
			}
			sb.deleteCharAt(sb.length() - 1);

			LogTask.pushThreadPool(() -> Db.update(sb.toString(), params.toArray()),
					"insertOrUpdate %s size:%s ", tableName, value.size());
		}
	}

	/**
	 * JSON转Record
	 *
	 * @param obj
	 * @return
	 */
	public static Record jsonObject2Record(JSONObject obj) {
		return jsonObject2Record(obj, null);
	}

	/**
	 * JSON转Record
	 *
	 * @param obj
	 * @param keys
	 * @return
	 */
	public static Record jsonObject2Record(JSONObject obj, String... keys) {
		Record record = new Record();
		if (keys != null) {
			for (String key : keys) {
				record.set(key, obj.get(key));
			}
		} else {
			for (String key : obj.keySet()) {
				record.set(key, obj.get(key));
			}
		}
		return record;
	}


	/**
	 * @param task
	 */
//    public static void pushThreadPool(LogTask task) {
//        threadPool.execute(task);
//    }

	/**
	 * 添加注单日志
	 *
	 * @param ticket
	 * @param status
	 * @param message
	 */
	protected static void addTickLog(RedisTicket ticket, int status, String message) {
		Record record = new Record();
		record.set("id", ticket.getId());
		record.set("gameId", ticket.getGameId());
		record.set("memberId", ticket.getMemberId());
		record.set("amount", ticket.getAmount());
		record.set("amountBet", ticket.getAmountBet());
		record.set("amountWL", ticket.getAmountWL());
		record.set("betNum", ticket.getBetNum());
		record.set("balance", ticket.getBalance());
		record.set("detail", ticket.getDetail());
		record.set("status", status);
		record.set("error", message);
		record.set("createDate", ticket.getCreateDate());

		record.set("memberType", ticket.getMemberType());
		record.set("masterDirect", ticket.getMasterDirect());

		record.set("agentId", ticket.getAgentId());
		record.set("masterId", ticket.getMasterId());
		record.set("userName", ticket.getUserName());
		record.set("comm", ticket.getComm());
		record.set("ptMy", ticket.getPtMy());
		record.set("ptUp", ticket.getPtUp());
		record.set("md5", ticket.getMd5());

		record.set("roundId", ticket.getRoundId());
		record.set("betTime", ticket.getBetTime());
		record.set("gameType", ticket.getGameType());

//        record.set("betType",ticket.getBetType());

//        logger.info(JSONObject.toJSONString(record));

		LogTask.pushThreadPool(() -> Db.save("api_ticket_log", record),
				"写入成功 addTickLog");
	}

	/**
	 * 查询下注限额
	 *
	 * @param memberId 会员ID
	 * @return 返回下注限额
	 */
	public static List<BetLimit> getLimit(String memberId, String gameId) {
		List<Record> result = Db.use("member").find("SELECT DISTINCT b.ruleId,min,max  " +
				"FROM (SELECT 'register' configType, c.gameId,c.limitId, ? memberId,gl.name limitName " +
				"FROM " +
				"tb_member_register_limit c " +
				"JOIN tb_member_register_info a on a.id = c.configId " +
				"JOIN tb_member_info b ON (b.masterId = a.masterId AND b.masterDirect=1) " +
				"                             OR (b.agentId = a.agentId AND b.masterDirect=0) " +
				"LEFT JOIN tb_member_limit mc ON mc.memberId = b.id AND c.gameId = mc.gameId " +
				"LEFT JOIN tb_member_use_limit_template mut ON mut.memberId = b.id AND mut.gameId = c.gameId AND mut.`status` = 1 " +
				"  JOIN tb_game_limit gl ON gl.gameId = c.gameId AND gl.id = c.limitId " +
				"WHERE " +
				"mc.id IS NULL AND mut.memberId is NULL AND b.id =? " +
				"UNION " +
				"SELECT  'template' configType, c.gameId,c.limitId, ? memberId,gl.name limitName " +
				"FROM " +
				"tb_member_limit_template c " +
				"JOIN tb_member_info b ON c.masterDirect = b.masterDirect AND (( c.masterDirect=0 AND c.agentId = b.agentId ) " +
				"                                                                OR (c.masterDirect=1 and b.masterId = c.masterId)) " +
				"JOIN tb_member_use_limit_template mut ON mut.memberId = b.id AND mut.gameId = c.gameId AND mut.`status` = 1 " +
				"LEFT JOIN tb_member_limit a ON a.memberId = b.id AND c.gameId = a.gameId " +
				"   JOIN tb_game_limit gl ON gl.gameId = c.gameId AND gl.id = c.limitId " +
				"WHERE " +
				"a.id IS NULL AND b.id = ? AND c.`status` = 1 " +
				"UNION " +
				"SELECT 'config' configType, c.gameId,c.limitId, ? memberId,gl.name limitName " +
				"FROM " +
				"tb_member_limit c " +
				"   JOIN tb_game_limit gl ON gl.gameId = c.gameId AND gl.id = c.limitId " +
				"WHERE " +
				"memberId = ? AND c.`status` = 1) p join tb_game_limit b on p.limitId=b.id  " +
				"WHERE p.memberId = ? and p.gameId=? limit 5", memberId, memberId, memberId, memberId, memberId, memberId, memberId, gameId);
		if (result == null || result.size() == 0) {
			return null;
		}
		List<BetLimit> list = new ArrayList<>();
		for (Record record : result) {
			String ruleId = record.getStr("ruleId");
			int min = record.getInt("min");
			int max = record.getInt("max");
			list.add(new BetLimit(ruleId, min, max));
		}
		return list;
	}

	/**
	 * 获取限红的ID
	 *
	 * @param memberId
	 * @return
	 */
	public static String getLimitIds(String memberId, String gameId) {
		List<BetLimit> list = getLimit(memberId, gameId);
		if (list == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (BetLimit betLimit : list) {
			sb.append(betLimit.getRuleId()).append(",");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 获取限红的ID
	 *
	 * @param memberId
	 * @return
	 */
	public static String getLimitIds(String memberId, String gameId, int count) {
		List<BetLimit> list = getLimit(memberId, gameId);
		if (list == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (BetLimit betLimit : list) {
			sb.append(betLimit.getRuleId()).append(",");
			count--;
			if (count == 0) {
				break;
			}
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 下注标识到redis
	 *
	 * @param key
	 * @param val
	 */
	protected static void pushBetLogCache(String key, Object val) {
		Cache redis = Redis.use();
		redis.set(key, val);
		redis.expire(key, 3600);
	}

	/**
	 * 是否存在标识在redis
	 *
	 * @param key
	 * @return
	 */
	protected static boolean existsBetLogCache(String key) {
		Cache redis = Redis.use();
		return (redis.exists(key));
	}

	/**
	 * 计算有效金额
	 *
	 * @param betAmt
	 * @param winAmt
	 * @return
	 */
	public static double computeTurnover(double betAmt, double winAmt) {

		if (winAmt == 0)
			return 0;
		betAmt = Math.abs(betAmt);
		winAmt = Math.abs(winAmt);

		return (winAmt > betAmt ? betAmt : winAmt);
	}

	public static void enterGameLog(String lastApi, String txnId, String memberId,
	                                double balance, double amount, boolean depositStatus,
	                                String userName, String api, String gameType,
	                                String platform, String gameName) {
		enterGameLog(lastApi, txnId, memberId, balance, amount, depositStatus, userName, api, gameType, platform, gameName, 1);
	}

	/**
	 * 进入游戏记录
	 *
	 * @param lastApi  进入游戏的上一个接口
	 * @param api      接口
	 * @param gameType 类型
	 * @param platform 平台
	 */
	public static void enterGameLog(String lastApi, String txnId, String memberId,
	                                double balance, double amount, boolean depositStatus,
	                                String userName, String api, String gameType,
	                                String platform, String gameName, int multiple) {
		Record record = new Record()
				.set("txnId", txnId)
				.set("memberId", memberId)
				.set("balance", balance)
				.set("withdrawAmt", amount)
				.set("depositStatus", depositStatus ? 1 : 0)
				.set("userName", userName)
				.set("api", api)
				.set("lastApi", lastApi)
				.set("gameType", gameType)
				.set("platform", platform)
				.set("gameName", gameName)
				.set("multiple", multiple);
		Db.save("api_enterGame_log", record);

		record.remove("id");
		//更新并插入
		insertOrUpdate("tb_member_enter_game", record);
	}

	/**
	 * 更新游戏提出记录
	 *
	 * @param withdrawReferenceId
	 * @param withdrawAmt
	 * @param memberId
	 * @param api
	 * @param platform
	 */
	public static void enterGamePlatformWithdraw(String withdrawReferenceId, double withdrawAmt,
	                                             String memberId, String api, String platform, String gameName) {

		try {
			String sql = "UPDATE api_enterGame_platform " +
					"SET withdrawReferenceId=?,withdrawAmt=?,type=1,updateTime=?,gameName=? " +
					"WHERE memberId=? and api=? and platform=? ";

			Db.update(sql, withdrawReferenceId, withdrawAmt, new Date(), gameName, memberId, api, platform);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 插入进入游戏及充值记录
	 *
	 * @param memberId
	 * @param balance
	 * @param userName
	 * @param api
	 * @param platform
	 * @param gameName
	 * @param depositReferenceId
	 * @param depositAmt
	 */
	public static void enterGamePlatformDeposit(String memberId, double balance, String userName,
	                                            String api, String platform, String gameName,
	                                            String depositReferenceId, double depositAmt) {
		try {
			Record enterGameLog = new Record()
					.set("memberId", memberId)
					.set("balance", balance)
					.set("userName", userName)
					.set("api", api)
					.set("platform", platform)
					.set("type", 0)
					.set("gameName", gameName)
					.set("withdrawReferenceId", "")
					.set("withdrawAmt", 0);
			//充值金额
			enterGameLog.set("depositAmt", depositAmt);
			enterGameLog.set("depositReferenceId", depositReferenceId);
			enterGameLog.set("updateTime", new Date());
			insertOrUpdate("api_enterGame_platform", enterGameLog);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 查询最后一次
	 *
	 * @param memberId
	 */
	public static EnterGameInfo queryLastEnterGame(String memberId) {

		Record record = Db.findFirst("select memberId,userName,api,gameType,platform,gameName " +
				"from tb_member_enter_game where memberId=?", memberId);
		if (record == null) {
			return null;
		}

		EnterGameInfo gameInfo = new EnterGameInfo();
		gameInfo.setGameType(record.getStr("gameType"));
		gameInfo.setPlatform(record.getStr("platform"));
		gameInfo.setUserName(record.getStr("userName"));
		gameInfo.setApi(record.getStr("api"));
		gameInfo.setGameName(record.getStr("gameName"));

		return gameInfo;
	}

}
