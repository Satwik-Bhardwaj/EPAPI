package entpack.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import entpack.Constant;
import entpack.bean.ConfigInfo;
import entpack.bean.MemberGameConfig;
import entpack.utils.MD5Util;
import entpack.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CacheService {

	private static final String prefix = "api:";

	private static final Logger logger = LoggerFactory.getLogger("cache_service");
	private static final Logger logger_member = LoggerFactory.getLogger("cache_service_member");

	// 缓存时间  7天
	private static final int cacheTime = 3600 * 24 * 7;

//	private static final Map<String, Object> localCache = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> T getLocalCache(String key) {
//		return (T) localCache.get(key);
		//暂时不用本地缓存
		return null;
	}

	public static void clearLocalCache(String key) {
//        if (key == null) {
//            localCache.clear();
//        } else {
//            localCache.remove(key);
//        }
	}

	public static void setLocalCache(String key, Object val) {
//		localCache.put(key, val);
	}

	/**
	 * 会员默认占成
	 *
	 * @param memberId
	 * @param gameId
	 * @return
	 */
	public static MemberGameConfig getMemberRegisterConfig(String memberId, String gameId) {
		Record record = null;

		try {
			record = Db.use("member").template("memberGameRegisterConfig", Kv.by("memberId", memberId).set("gameId", gameId)).findFirst();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (record == null) {
			logger.error("pt.error record == null memberId:{} gameId:{}", memberId, gameId);
			logger_member.error("pt.error record == null memberId:{} gameId:{}", memberId, gameId);
			return null;
		}

		MemberGameConfig memberGameConfig = new MemberGameConfig(record);
		return memberGameConfig;
	}

	/**
	 * 会员的设置
	 *
	 * @param memberId
	 * @param gameId
	 * @return
	 */
	public static MemberGameConfig getMemberConfig(String memberId, String gameId) {
		Cache redis = Redis.use("cache");
		String key = String.format("MemberConfigList:%s:%s", memberId, gameId);
		String json = getLocalCache(key);
		if (json == null) {
			json = redis.get(key);
		}
		Record record = null;
		if (json == null) {
			try {
				record = Db.use("member").template("memberGameConfig", Kv.by("memberId", memberId).set("gameId", gameId)).findFirst();

				if (record == null) {
					logger.error("config == null,getMemberRegisterConfig memberId:{} gameId:{}", memberId, gameId);

					record = Db.use("member").template("memberGameRegisterConfig", Kv.by("memberId", memberId).set("gameId", gameId)).findFirst();

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (record == null) {
				logger.error("pt.error record == null memberId:{} gameId:{}", memberId, gameId);
				logger_member.error("pt.error record == null memberId:{} gameId:{}", memberId, gameId);
				return null;
			}

		} else {
			JSONObject jsonObject = JSON.parseObject(json);
			record = new Record().setColumns(jsonObject);
		}

		MemberGameConfig memberGameConfig = new MemberGameConfig(record);
		String md5 = memberGameConfig.getMd5();
		if (md5 == null || md5.length() == 0) {

			String masterId = record.getStr("masterId");
			String agentId = record.getStr("agentId");

			List<ConfigInfo> allConfigs = getMasterAgetConfigs(masterId, agentId, gameId);

			if (!checkPt(memberId, gameId, memberGameConfig, allConfigs)) {
				logger.error("pt.error memberId:{} gameId:{}", memberId, gameId);
				logger_member.error("pt.error memberId:{} gameId:{}", memberId, gameId);
				return null;
			}
			String md5json = JSONObject.toJSONString(allConfigs);

//			System.out.println("md5json:" + md5json);
			// 签名
			md5 = MD5Util.md5(md5json);
//			System.out.println("md5:" + md5);
			memberGameConfig.setMd5(md5);
			memberGameConfig.setConfigCount(allConfigs.size());
			redis.setex(Constant.getMasterAgentConfigListKey(md5), cacheTime, md5json);

			String sign = MD5Util.md5(md5 + memberGameConfig.getComm() + memberGameConfig.getPtMy() + memberGameConfig.getPtUp());
//			if (!isExistsPTHistory(sign)) {
			// 历史占成
			List<ConfigInfo> historyConfigList = new ArrayList<>();
			historyConfigList.addAll(allConfigs);
			historyConfigList.add(new ConfigInfo(memberGameConfig));
			save2PTHistory(sign, md5, historyConfigList);
//			}

		}
		if (md5 != null) {
			record.set("md5", md5);
			String jsonData = record.toJson();
			redis.setex(key, cacheTime, jsonData);
			setLocalCache(key, jsonData);
		}
		return memberGameConfig;
	}

	private static boolean isExistsPTHistory(String sign) {
		String sql = "select 1 from tb_pt_history where sign=?";
		Integer cc = Db.queryInt(sql, sign);
		return cc != null && cc.equals(1);
	}

	/**
	 * 保存占成历史
	 *
	 * @param sign
	 * @param md5
	 * @param memberConfigs
	 */
	private static void save2PTHistory(String sign, String md5, List<ConfigInfo> memberConfigs) {

		Date createDate = new Date();
		for (ConfigInfo memberConfig : memberConfigs) {
			Record record = new Record();
			record.set("id", memberConfig.getId());
			record.set("userName", memberConfig.getUserName());
			record.set("parentId", memberConfig.getParentId());
			record.set("agentId", memberConfig.getAgentId());
			record.set("masterId", memberConfig.getMasterId());
			record.set("type", memberConfig.getType());
			record.set("comm", memberConfig.getComm());
			record.set("ptMy", memberConfig.getPtMy());
			record.set("ptSub", memberConfig.getPtSub());
			record.set("ptForce", memberConfig.getPtForce());
			record.set("ptUp", memberConfig.getPtUp());
			record.set("ptRemaining", memberConfig.getPtRemaining());
			record.set("md5", md5);

			record.set("createDate", createDate);

			if (memberConfig.getType() == 0) {
				record.set("sign", sign);
				record.set("memberType", memberConfig.getMemberType());
				record.set("masterDirect", memberConfig.getMasterDirect());
			}

			BaseService.insertOrUpdate("tb_pt_history", record, "member");
		}
	}

	/**
	 * 检查占成
	 *
	 * @param info
	 * @param allConfigs
	 * @return
	 */
	private static boolean checkPt(String memberId, String gameId, MemberGameConfig info, List<ConfigInfo> allConfigs) {

		ConfigInfo memberInfo = new ConfigInfo(info);
		ConfigInfo downInfo = memberInfo;
		ConfigInfo companyCalcInfo = null;
		// 最后的就是总公司的
		for (ConfigInfo current : allConfigs) {
			//下级传过来的值
			float down_comm = downInfo.getComm();
			float down_ptUp = downInfo.getPtUp();
			float down_ptMy = downInfo.getPtMy();

			float down_ptReal = downInfo.getDownPtReal();
			float down_ptRealUp = downInfo.getPtRealUp();
			float down_ptRemaining = downInfo.getPtRemaining();

			//自己的
//			float comm = current.getComm() / 100;
			float ptMy = current.getPtMy();
			float ptUp = current.getPtUp();
			float ptRemaining = current.getPtRemaining();

			//实际往上返占成 = 往上返占成 + 下级实际往上返占成 - MIN(自己多余占成 , 下级实际往上返占成)
			Float ptRealUp = down_ptUp + down_ptRealUp - Math.min(down_ptRemaining, down_ptRealUp);
			//实际占成 = 我的占成 + MIN(自己多余占成 , 下级实际往上返占成)
			Float ptReal = down_ptMy + Math.min(down_ptRealUp, down_ptRemaining);

//			current.setComm(comm);

			// 实际上级占成
			current.setPtRealUp(ptRealUp);
			// 实际占成
			current.setPtReal(ptReal);

			current.setDownPtReal(ptReal + down_ptReal);

			//查找是否有上级代理,如果没有的话那上级就是白牌了
			if (current.getType() == 2 && StringUtil.isBlank(current.getParentId())) {
				//总公司实际占成 = 我的占成 + MIN(自己多余占成 , 下级实际往上返占成)
				Float comReal = ptMy + Math.min(100, ptRealUp);
				companyCalcInfo = new ConfigInfo();
				companyCalcInfo.setPtReal(comReal);
				companyCalcInfo.setPtUp(current.getPtUp());
			}
			downInfo = current;
		}

		float pt = 0;
		for (ConfigInfo current : allConfigs) {
			pt += current.getPtReal();
		}
		pt += memberInfo.getPtReal();
		if (companyCalcInfo == null) {
			logger.error("companyCalcInfo == null member:{} gameId:{}", memberId, gameId);
			companyCalcInfo = new ConfigInfo();
		}
		pt += companyCalcInfo.getPtReal();

		boolean check = pt == 100;

		if (!check) {
			logger.error("checkPt.error userName:{} gameId:{} pt:{}", memberId, gameId, pt);

			for (ConfigInfo current : allConfigs) {
				logger.error("checkPt.error userName:{} ptReal:{} ", current.getUserName(), current.getPtReal());
			}
			logger.error("checkPt.error userName:{} ptReal:{} ", memberInfo.getUserName(), memberInfo.getPtReal());
			logger.error("checkPt.error userName:company ptReal:{} ", companyCalcInfo.getPtReal());
		}

		return check;
	}

	public static List<ConfigInfo> getMasterAgetConfigs(String masterId, String agentId, String gameId) {
		List<ConfigInfo> allList = new ArrayList<>();
		if (agentId != null) {
			allList.addAll(CacheService.getAgentConfig(agentId, gameId));
		}
		allList.addAll(CacheService.getMasterConfig(masterId, gameId));
		return allList;
	}

	/**
	 * 通过md5加载白牌、代理占成
	 *
	 * @param md5
	 * @return
	 */
	public static List<ConfigInfo> getMasterAgentConfigsByMd5(String md5) {
		Cache redis = Redis.use("cache");

		String key = Constant.getMasterAgentConfigListKey(md5);

		String json = getLocalCache(key);
		if (json == null) {
			json = redis.get(key);
		}
		List<ConfigInfo> data = new ArrayList<>();
		JSONArray objects = JSONArray.parseArray(json);
		for (Object o : objects) {
			JSONObject item = (JSONObject) o;
			data.add(new ConfigInfo(item.getInteger("type"), new Record().setColumns(item)));
		}
		return data;
	}


	/**
	 * 代理的设置
	 *
	 * @param agentId
	 * @param gameId
	 * @return
	 */
	public static List<ConfigInfo> getAgentConfig(String agentId, String gameId) {

		Cache redis = Redis.use();
		String key = String.format("AgentConfigList:%s:%s", agentId, gameId);
		String json = getLocalCache(key);
		if (json == null) {
			json = redis.get(key);
		}
		List<ConfigInfo> data;
		if (json == null || json != null) {
			data = getCalcInfo("agentConfig", 1, agentId, gameId, gameId);
			redis.set(key, data);
			setLocalCache(key, JsonKit.toJson(data));
		} else {
			data = new ArrayList<>();
			JSONArray objects = JSONArray.parseArray(json);
			for (Object o : objects) {
				JSONObject item = (JSONObject) o;
				data.add(new ConfigInfo(1, new Record().setColumns(item)));
			}
		}
		return data;
	}

	/**
	 * 白牌的设置
	 *
	 * @param masterId
	 * @param gameId
	 * @return
	 */
	public static List<ConfigInfo> getMasterConfig(String masterId, String gameId) {
		Cache redis = Redis.use();
		String key = String.format("MasterConfigList:%s:%s", masterId, gameId);
		String json = getLocalCache(key);
		if (json == null) {
			json = redis.get(key);
		}
		List<ConfigInfo> data;
		if (json == null || json != null) {
			data = getCalcInfo("masterConfig", 2, masterId, gameId, gameId);
			redis.set(key, data);
			setLocalCache(key, JsonKit.toJson(data));
		} else {
			data = new ArrayList<>();
			JSONArray objects = JSONArray.parseArray(json);
			for (Object o : objects) {
				JSONObject item = (JSONObject) o;
				data.add(new ConfigInfo(2, new Record().setColumns(item)));
			}
		}
		return data;
	}

	private static List<ConfigInfo> getCalcInfo(String key, int type, Object... agrs) {

		List<Record> config = Db.use("member").find(Db.use("member").getSql(key), agrs);

		List<ConfigInfo> list = new ArrayList<>();
		for (Record record : config) {
			list.add(new ConfigInfo(type, record));
		}

		return list;
	}

	public static Record getAndSetCacheObj(String key, String sql, Object... params) {
		return getAndSetCacheObjByConfigName(null, key, sql, params);
	}

	public static Record getAndSetCacheObjByConfigName(String configName, String key, String sql, Object... params) {
		List<Record> data = getAndSetCache(configName, key, sql, params);
		if (data != null && data.size() > 0) {
			return data.get(0);
		}
		return null;
	}

	/**
	 * get and set cache
	 *
	 * @param key
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<Record> getAndSetCache(String configName, String key, String sql, Object... params) {

		key = prefix + key;

		Cache redis = Redis.use();
		//优先从本地内存取
//		String json = getLocalCache(key);
		String json = null;
		if (json == null) {
			//如果本地没有，从redis取
			json = redis.get(key);
		}

		List<Record> data;
		if (json == null) {
			if (configName == null) {
				data = Db.find(sql, params);
			} else {
				data = Db.use(configName).find(sql, params);
			}
			if (data.size() > 0) {
				String jsonData = JsonKit.toJson(data);
				redis.set(key, jsonData);
				//设置一天过期
				redis.expire(key, 24 * 60 * 60);
				setLocalCache(key, jsonData);
			}
		} else {
			data = new ArrayList<>();
			JSONArray objects = JSONArray.parseArray(json);
			for (Object o : objects) {
				JSONObject item = (JSONObject) o;
				data.add(new Record().setColumns(item));
			}
		}
		return data;
	}

}
