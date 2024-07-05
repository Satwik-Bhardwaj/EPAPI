package EntPack;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import entpack.bean.MemberGameConfig;
import entpack.service.CacheService;
import entpack.utils.MD5Util;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class TestReport extends TestBase {
	private static Logger logger = LoggerFactory.getLogger("test_member_pt");

	String memberId = "bGcy0VWU";
	String masterId = "fOgsENkV";
	String gameId = "sa2";

	@Test
	public void testMemberGameConfig() {
		Record cc = Db.use("member").template("memberGameConfigApi",
				Kv.by("memberId", "ovetCE6g").set("api", "gs3").set("status", 0)).findFirst();
		boolean allow = !(cc == null);

		System.out.println(allow);
	}

	@Test
	public void md5(){
	}

	public static void main(String[] args) {
		System.out.println(MD5Util.md5("aa123456"));

	}


	/**
	 * 测试所有会员所有游戏PT
	 */
	@Test
	public void testAllGameMemberConfig() {

		ExecutorService threadPool = Executors.newFixedThreadPool(64);

		String sql = "select id from tb_member_info   ";
		List<Record> ids = Db.find(sql);
		for (Record record : ids) {
			String memberId = record.getStr("id");

			threadPool.execute(() -> {
				List<Record> configs = Db.use("member").template("memberGameConfigApi",
						Kv.by("status", 1).set("memberId", memberId)).find();

				for (Record config : configs) {
					try {
						String gameId = config.getStr("gameId");

//			MemberGameConfig memberGameConfig1 = CacheService.getMemberConfig(memberId, "gs3_pr_slots");
						MemberGameConfig memberGameConfig = CacheService.getMemberConfig(memberId, gameId);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		threadPool.shutdown();

		try {
			while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
				System.out.println("awaitTermination");
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 测试所有会员PT
	 */
	@Test
	public void testAllMember() {
		String sql = "select id from tb_member_info ";
		List<Record> ids = Db.find(sql);
		for (Record record : ids) {
			memberId = record.getStr("id");

//			MemberGameConfig memberGameConfig1 = CacheService.getMemberConfig(memberId, "gs3_pr_slots");
			MemberGameConfig memberGameConfig = CacheService.getMemberConfig(memberId, "gs_ac_slots");

//			if (memberGameConfig == null) {
//				logger.error("memberId:{}", memberId);
//			}
		}
	}

	/**
	 * 测试
	 */
	@Test
	public void testMember() {
		assertTrue(!memberId.equals(""));
		assertTrue(!gameId.equals(""));


		MemberGameConfig memberGameConfig = CacheService.getMemberConfig(memberId, gameId);


		System.out.println(JSONObject.toJSONString(memberGameConfig));
	}

	@Test
	public void updateMemberGameInfo() {
		MemberGameConfig config = CacheService.getMemberConfig(memberId, gameId);

		if (config == null) {
			System.out.println("MemberGameConfig not found");
		}

		Map<String, Object> map = new HashMap<>();


		map.put("MemberGameConfig", config);
		map.put("MasterAgetConfigs", CacheService.getMasterAgentConfigsByMd5(config.getMd5()));
		System.out.println(JSONObject.toJSONString(map));


		System.out.println(String.format("%s:%s", config.getUserName(), config.getComm()));
	}
}
