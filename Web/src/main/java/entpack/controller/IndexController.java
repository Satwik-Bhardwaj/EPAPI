package entpack.controller;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import entpack.api.GeneralApi;
import entpack.bean.EnterGameInfo;
import entpack.bean.MemberGameConfig;
import entpack.bean.MemberInfo;
import entpack.service.BaseService;
import entpack.service.CacheService;
import entpack.utils.DateUtil;
import entpack.utils.MD5Util;
import entpack.utils.RedisLock;
import entpack.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.text.DecimalFormat;
import java.util.*;

public class IndexController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(IndexController.class);
    private static String key = "gg";

    //多少天前有进入过游戏的
    private static final int BALANCE_DAY = -90;

    public void index() {
//        Cache redis = Redis.use();
//        String jsonStr = "test";
//        redis.lpush("test", jsonStr);
//
//        set("test", "hello world");
//        render("index.html");

        renderText("");
    }

    public void clearLocalCache(String key) {
        CacheService.clearLocalCache(key);
        renderText("ok");
    }

    public void updateMemberGameInfo(String memberId, String gameId) {
        MemberGameConfig config = CacheService.getMemberConfig(memberId, gameId);

        Map<String, Object> map = new HashMap<>();
        if (config == null) {
            renderText("MemberGameConfig not found");
            return;
        }

        map.put("MemberGameConfig", config);
        map.put("MasterAgentConfigs", CacheService.getMasterAgentConfigsByMd5(config.getMd5()));
        renderJson(map);
    }

    public void delMemberConfigKey() {
        String userName = getPara("n");
        if (userName == null) {
            renderText("");
            return;
        }
        Cache redis = Redis.use();

        String sql = "select id from tb_member_info where username=?";
        for (String s : userName.split(",")) {

            Record record = Db.use("member").findFirst(sql, s);
            if (record == null) {
                logger.info(s + " is null");
                continue;
            }
            String memberId = record.getStr("id");
            String keyword = String.format("tb_member_config:%s:%s", memberId, "*");
            List<String> keys = scanKeys(keyword);

            logger.info("keyword :" + keyword);
            for (String key : keys) {
                logger.info("del " + key);
                redis.del(key);
            }
        }
        renderText("ok");
    }

    private List<String> scanKeys(String keyword) {
        List<String> keys = new ArrayList<>();
        // redis游标
        String cur = "0";
        do {
            // 搜索redis
            ScanResult<String> scanResult = searchKey(keyword, cur, 10000);
            // 取当前游标
            cur = scanResult.getCursor();

            for (String key : scanResult.getResult()) {
                keys.add(key);
            }
        } while (Integer.parseInt(cur) > 0);
        return keys;
    }

    public static ScanResult<String> searchKey(String search, String cur, int size) {
        Cache redis = Redis.use();

        Jedis jedis = redis.getJedis();

        try {
            ScanParams params = new ScanParams();
            params.count(size);
            params.match(search);
            ScanResult<String> scanResult = jedis.scan(cur, params);
            return scanResult;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            jedis.close();
        }
    }


    /**
     * 获取游戏余额
     *
     * @param memberId 会员ID
     * @param s        签名
     * @return
     */
    public void getGameBalance(String memberId, String s) {

        double balance = 0;
        if (!s.equals(getSign(memberId))) {
            balance = -1;
        } else {
            balance = getGameBalance(memberId);
        }
        renderText(String.valueOf(balance));
    }

    /**
     * 会员游戏余额
     *
     * @param memberId
     * @return
     */
    private double getGameBalance(String memberId) {
        return GeneralApi.getGameBalance(memberId);
    }

    /**
     * 从游戏中提出
     *
     * @param txnId
     * @param memberId
     * @param s
     */
    public void withdraw2Balance(String txnId, String memberId, String t, String s) {
        if (s == null || !s.equals(getSign(memberId, txnId, t))) {
            renderJson(Ret.fail("sign error"));
            return;
        }

        MemberInfo memberInfo = BaseService.getMemberInfo(memberId);
        if (memberInfo == null) {
            renderJson(Ret.fail());
            return;
        }

        String userName = memberInfo.getUserName();
        EnterGameInfo lastRecord = getMemberLastEnterGame(memberId);

        //如果没进入游戏记录，直接修改余额
        if (lastRecord == null) {
            renderJson(Ret.ok());
            return;
        }

        //如果没进入游戏记录，直接修改余额
        if (lastRecord == null) {
            renderJson(Ret.ok());
            return;
        }
        Ret result = GeneralApi.withdraw2Balance(txnId, memberId, userName, lastRecord.getApi(), lastRecord.getPlatform(), lastRecord.getGameName());
        renderJson(result);
    }

    /**
     * 充值到游戏
     *
     * @param memberId
     * @param s
     */
    public void deposit2Game(String txnId, String memberId, String t, String s) {
        if (s == null || !s.equals(getSign(memberId, txnId, t))) {
            renderJson(Ret.fail());
            return;
        }

        MemberInfo memberInfo = BaseService.getMemberInfo(memberId);
        if (memberInfo == null) {
            renderJson(Ret.fail());
            return;
        }

        String userName = memberInfo.getUserName();
        EnterGameInfo lastRecord = getMemberLastEnterGame(memberId);

        //如果没进入游戏记录，直接修改余额
        if (lastRecord == null) {
//			BaseService.resetBalance(memberId, amt_double, StringUtil.shortUUID(), "deposit2Game reset", "");
            renderJson(Ret.ok());
        }

        Ret result = GeneralApi.deposit2Game(txnId, memberId, userName, lastRecord.getApi(), lastRecord.getPlatform());

        renderJson(result);
    }

    /**
     * 手动更新余额
     *
     * @param memberId
     * @param amt
     */
    public void manualRefreshBalance(String txnId, String memberId, String amt, String t, String s) {

        if (s == null || !s.equals(getSign(memberId, txnId, t)) || amt == null) {
            renderJson(Ret.fail());
            return;
        }

        Ret result = GeneralApi.manualRefreshBalance(txnId, memberId, amt);
        renderJson(result);

    }

    /**
     * 获取会员最后一次进入游戏
     *
     * @param memberId
     * @return
     */
    private EnterGameInfo getMemberLastEnterGame(String memberId) {

        return BaseService.queryLastEnterGame(memberId);
    }


    /**
     * 计算签名
     *
     * @param objects
     * @return
     */
    public static String getSign(String... objects) {

        String str = "";
        for (String object : objects) {
            str += object;
        }
        str += key;
        return MD5Util.md5(str);
    }

    /**
     * 回收所有游戏余额
     *
     * @param memberId
     */
    public void tranAllBalance(String txnId, String memberId, String t, String s) {
        if (s == null || !s.equals(getSign(memberId, txnId, t))) {
            renderJson(Ret.fail());
            return;
        }
        Ret result = GeneralApi.tranAllBalance(memberId);
        renderJson(result);
    }

    public static void main(String[] args) {

        String memberId = "MCAmvfpr";
        String txnId = StringUtil.shortUUID();
        String t = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
        String s = getSign(memberId, txnId, t);
        String sm = getSign(memberId);
        String host = "http://localhost:8090";
        String url = "curl --location --request GET '" + host + "/tranAllBalance?memberId=" + memberId + "&t=" + t + "&txnId=" + txnId + "&s=" + s + "'";
        String urlgetAllGameBalance = "curl --location --request GET '" + host + "/getAllGameBalance?memberId=" + memberId + "&t=" + t + "&txnId=" + txnId + "&s=" + s + "'";
        String urlwithdraw2Balance = "curl --location --request GET '" + host + "/withdraw2Balance?memberId=" + memberId + "&t=" + t + "&txnId=" + txnId + "&s=" + s + "'";
        String urlgetGameBalance = "curl --location --request GET '" + host + "/getGameBalance?memberId=" + memberId + "&t=" + t + "&txnId=" + txnId + "&s=" + sm + "'";
        String urlrefreshBalance = "curl --location --request GET '" + host + "/refreshBalance?memberId=" + memberId + "&t=" + t + "&txnId=" + txnId + "&s=" + s + "'";


        System.out.println(url);
        System.out.println(urlgetAllGameBalance);
        System.out.println(urlwithdraw2Balance);
        System.out.println(urlgetGameBalance);
        System.out.println(urlrefreshBalance);


        double amount = 3195.1875;

        DecimalFormat df = new java.text.DecimalFormat("#0.00");

        System.out.println(StringUtil.roundDown(amount, 2));

    }


    /**
     * 查询所有游戏余额
     *
     * @param memberId
     */
    public void getAllGameBalance(String txnId, String memberId, String t, String s) {

        if (s == null || !s.equals(getSign(memberId, txnId, t))) {
            renderJson(Ret.fail());
            return;
        }

        // 1分钟只能执行一次
        if (!RedisLock.lock(txnId, 60)) {
            return;
        }

        Date startDate = DateUtil.addHour(Calendar.DAY_OF_MONTH, BALANCE_DAY);

        //90天前有进入过游戏的
        List<Record> games = Db.find("SELECT api,platform FROM api_enterGame_log " +
                "WHERE memberId=? AND createDate >= ? " +
                "GROUP BY api,platform", memberId, startDate);

        List<Map<String, Object>> results = GeneralApi.queryAllBalance(memberId, games);

        renderJson(results);
    }

    /**
     * 刷新余额
     *
     * @param memberId
     */
    public void refreshBalance(String txnId, String memberId, String t, String s) {

        if (s == null || !s.equals(getSign(memberId, txnId, t))) {
            renderJson(Ret.fail());
            return;
        }

        String key = "refreshBalance:" + memberId;
        // 1分钟只能执行一次
        if (!RedisLock.lock(key, 10)) {
            renderJson(Ret.fail("msg", "try again in 300 seconds"));
            return;
        }

        MemberInfo memberInfo = BaseService.getMemberInfo(memberId);
        String userName = memberInfo.getUserName();


        Date startDate = DateUtil.addHour(Calendar.DAY_OF_MONTH, BALANCE_DAY);


        //90天前有进入过游戏的
        List<Record> games = Db.find("SELECT * " +
                "FROM(SELECT api,platform,gameName,max(createDate) createDate" +
                "        FROM api_enterGame_log " +
                "     WHERE memberId=? AND createDate >= ? " +
                "     GROUP BY api,platform,gameName )t " +
                "ORDER BY createDate DESC " +
                "LIMIT 10", memberId, startDate);

        List<Map<String, Object>> results = GeneralApi.queryAllBalance(memberId, games);

        for (Map<String, Object> result : results) {
            String api = result.get("api").toString();
            String platform = result.get("platform").toString();
            String gameName = result.get("gameName") == null ? "" : result.get("gameName").toString();
            Double balance = (Double) result.get("balance");

            if (balance != null && balance >= 1) {

                //提现到余额
                Ret ret = GeneralApi.withdraw2Balance(txnId, memberId, userName, api, platform, gameName);
                if (ret.getStr("state").equals("ok")) {
                    result.put("amount", ret.getFloat("amount"));
                }
                result.put("state", ret.getStr("state"));
            }
        }

        renderJson(Ret.ok().set("results", results));
    }

}
