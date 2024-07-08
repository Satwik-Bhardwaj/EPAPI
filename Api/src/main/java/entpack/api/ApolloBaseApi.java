package entpack.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import entpack.Constant;
import entpack.bean.BalanceResult;
import entpack.bean.BalanceRollBack;
import entpack.bean.GameInfo;
import entpack.bean.MemberInfo;
import entpack.service.BaseService;
import entpack.service.Kiss918ApiService;
import entpack.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class ApolloBaseApi implements MultipleInterface {

    protected static String def_Api = "apollo";
    private static final Logger logger = LoggerFactory.getLogger(def_Api);

    private static final Logger logger_json = LoggerFactory.getLogger(def_Api + "_json");
    protected String iv;

    protected String secretKey;

    protected String currency;

    protected String api_agent;

    protected String dc;

    private static final String HOST = Constant.GameConfig.get(def_Api + ".host");

    private static final String HOST2 = Constant.GameConfig.get(def_Api + ".host2");


    public String sign(String data, String time) {
        logger.info("authCode:{} data:{} time:{} secretKey:{}", iv.toLowerCase(), data.toLowerCase(), time.toLowerCase(), secretKey.toLowerCase());
        return MD5Util.md5((iv + data + time + secretKey).toLowerCase()).toUpperCase();
    }


    /**
     * @return
     */
    public abstract String getApi();

    public abstract String getCurrency();

    public abstract String getIv();

    public abstract String getSecretKey();

    public abstract String getApiAgent();


    /**
     * 获取接口数据
     *
     * @param url
     * @param params
     * @return
     */
    public JSONObject postData(String url, Map<String, String> params) {
        try {
            //测试用
//            int i = 1 / 0;
            String result = OkHttpUtil.get(url, params);
            if (result != null) {
                logger_json.info("url:{} result:{}", GeneralApi.createGetDataUrl(url, params), result);
                return JSONObject.parseObject(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("postData.error getUrl:{}", GeneralApi.createGetDataUrl(url, params));
        }
        return null;
    }


    /**
     * 用户注册接口
     *
     * @return
     */
    public String randomUserName() {

        String time = String.valueOf(System.currentTimeMillis());
        Map<String, String> params = new HashMap<>();
        params.put("action", "RandomUserName");

        params.put("userName", getApiAgent());
        params.put("UserAreaId", "1");
        params.put("time", time);
        params.put("authcode", iv);
        params.put("sign", sign(getApiAgent(), time));

        String url = HOST + "/ashx/account/account.ashx";
        try {
            JSONObject obj = postData(url, params);

            if (obj != null) {
                String code = obj.getString("code");
                if (code != null && (code.equals("0") || code.equals("-1"))) {
                    // 记录创建的账号
                    return obj.getString("account");
                } else {
                    logger.error("addUser postUrl:{} params:{} result:{}", url, JSONObject.toJSONString(params), obj.toJSONString());
                }
                return null;
            }
        } catch (Exception e) {
            logger.error("addUser.error", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加用户接口
     * @param uid user id
     * @param creditAllocated player's name
     * @return
     */
    public boolean createPlayer(String uid, String creditAllocated) {

        String time = String.valueOf(System.currentTimeMillis());
        JSONObject params = new JSONObject();
        params.put("action", "2");
        params.put("ts", time);
        params.put("parent", getApiAgent());
        params.put("uid", uid);
        params.put("name", uid);
        params.put("credit_allocated", creditAllocated == null || creditAllocated.isEmpty() ? "0" : creditAllocated);


        //encryption
        // TODO : add encryption operations here
        String data = null;
        try {
            data = AESUtil.encryptForJDB(params.toString(), secretKey, iv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // build request
        Map<String, String> paramList = new HashMap<>();
        paramList.put("dc", dc);
        paramList.put("x", data);

        String url = HOST + "/Tr_CreateUser.aspx";
        try {
            JSONObject obj = postData(url, paramList);

            if (obj != null) {
                String resp_status = obj.getString("status");
                if(resp_status != null && resp_status.equals("0000")) {
                    // 记录创建的账号
                    //create entry in database
                    Record record = new Record()
                            .set("api", getApi())
                            .set("account", obj.get("uid"))
                            .set("memberId", obj.get("uid"))
                            .set("createDate", new Date())
                            .set("currency",getCurrency())
                            .set("username",obj.get("uid"))
                            .set("agentId",getApiAgent());

                    Db.use("member").save("apollo_create",record);

                    return true;
                } else {
                    logger.error("addUser postUrl:{} params:{} result:{}", url, JSONObject.toJSONString(params), obj.toJSONString());
                }
                return false;   
            }
        } catch (Exception e) {
            logger.error("addUser.error", e);
            e.printStackTrace();
        }
        return false;

    }


    /**
     * 修改用户信息
     *
     * @param userName
     * @param oldPassWd
     * @param passWd
     */
    public boolean editUser(String userName, String oldPassWd, String passWd) {

        String time = String.valueOf(System.currentTimeMillis());
        Map<String, String> params = new HashMap<>();
        params.put("action", "editUser");
        params.put("OldPassWd", oldPassWd);
        params.put("PassWd", passWd);
        params.put("pwdtype", "1");
        params.put("time", time);
        params.put("authcode", iv);
        params.put("sign", sign(userName, time));

        String url = HOST + "/ashx/account/account.ashx";
        try {
            JSONObject obj = postData(url, params);

            if (obj != null) {
                String code = obj.getString("code");
                if (code != null && code.equals("0")) {
                    return true;
                } else if (code != null && code.equals("-1")) {
                    logger.error("editUser 账号或密码错误 postUrl:{} params:{} result:{}", url, JSONObject.toJSONString(params), obj.toJSONString());
                    return false;
                } else {
                    logger.error("editUser postUrl:{} params:{} result:{}", url, JSONObject.toJSONString(params), obj.toJSONString());
                }
                return false;
            }
        } catch (Exception e) {
            logger.error("editUser.error", e);
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 查询(简单)用户信息
     *
     * @param userName
     * @return
     */
    public JSONObject getUserInfo(String userName) {

        Record user = Kiss918ApiService.getUser(userName);
        String account = user.get("account");

        String time = String.valueOf(System.currentTimeMillis());

        Map<String, String> map = new HashMap<>();
        map.put("action", "getUserInfo");
        map.put("userName", account);
        map.put("time", time);
        map.put("authcode", iv);
        map.put("sign", sign(account, time));

        String addUserUrl = HOST + "/ashx/account/account.ashx";
        return postData(addUserUrl, map);
    }

    /**
     * 查询代理或玩家列表
     *
     * @param action
     * @param userName
     * @param pageIndex
     * @return
     */
    public JSONObject accountList(String action, String userName, Integer pageIndex) {

        String time = String.valueOf(System.currentTimeMillis());

        Map<String, String> map = new HashMap<>();
        map.put("action", action);
        map.put("userName", userName);
        map.put("pageIndex", String.valueOf(pageIndex));
        map.put("time", time);
        map.put("authcode", iv);
        map.put("sign", sign(userName, time));

        String addUserUrl = HOST2 + "/ashx/getData/AccountList.ashx";
        return postData(addUserUrl, map);
    }

    /**
     * 查玩家每天输赢总账
     *
     * @param userName
     * @param sDate
     * @param eDate
     * @return
     */
    public JSONObject accountReport(String userName, Date sDate, Date eDate) {

        String time = String.valueOf(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Map<String, String> map = new HashMap<>();
        map.put("userName", userName);
        map.put("sDate", sdf.format(sDate));
        map.put("eDate", sdf.format(eDate));
        map.put("time", time);
        map.put("authcode", iv);
        map.put("sign", sign(userName, time));

        String addUserUrl = HOST2 + "/ashx/AccountReport.ashx";
        return postData(addUserUrl, map);
    }

    /**
     * 代理总输赢报表
     *
     * @param userName
     * @param sDate
     * @param eDate
     * @return
     */
    public JSONObject accountMoneyLog(String userName, Date sDate, Date eDate) {

        String time = String.valueOf(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Map<String, String> map = new HashMap<>();
        map.put("userName", userName);
        map.put("sDate", sdf.format(sDate));
        map.put("eDate", sdf.format(eDate));
        map.put("time", time);
        map.put("authcode", iv);
        map.put("sign", sign(userName, time));

        String addUserUrl = HOST2 + "/ashx/AgentMoneyLog.ashx";
        return postData(addUserUrl, map);
    }

    /**
     * 下属玩家或代理报表
     *
     * @param userName
     * @param sDate
     * @param eDate
     * @param type
     * @return
     */
    public JSONObject agentTotalReport(String userName, Date sDate, Date eDate, String type) {

        String time = String.valueOf(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Map<String, String> map = new HashMap<>();
        map.put("userName", userName);
        map.put("sDate", sdf.format(sDate));
        map.put("eDate", sdf.format(eDate));
        map.put("Type", type);
        map.put("time", time);
        map.put("authcode", iv);
        map.put("sign", sign(userName, time));

        String addUserUrl = HOST2 + "/ashx/AgentTotalReport.ashx";
        return postData(addUserUrl, map);
    }

    public void queryMemberReport(Date date) {
        String type = "ServerTotalReport";

        Date startDate = DateUtil.setHour(date, 0,0,0,0);

        Date endDate = DateUtil.addHour(startDate, Calendar.DAY_OF_MONTH, 1);
        endDate = DateUtil.addHour(endDate, Calendar.SECOND, -1);

        JSONObject result = agentTotalReport(getApiAgent(), startDate, endDate, type);

        if (result != null && result.getBooleanValue("success")) {

            String totalwin = result.getString("totalwin");

            String key = "api:Task:kiss918:totalwin";
            String item_key = "api:Task:kiss918:totalwin:";
            String accountGameLog_key = "api:Task:kiss918:accountGameLog:";

            int pageSize = 1000;
            Cache redis = Redis.use();
            String redisTotalWin = redis.get(key);
            if (redisTotalWin == null || !redisTotalWin.equals(totalwin)) {

                JSONArray results = result.getJSONArray("results");

                for (Object o : results) {
                    JSONObject item = (JSONObject) o;

                    String account = item.getString("Account");
                    String win = item.getString("win");

                    String redisAccountWin = redis.get(item_key + account);

                    if (redisAccountWin == null || !redisAccountWin.equals(win)) {

                        //取会员最后一次玩家游戏记录
                        String reportTime = redis.get(accountGameLog_key + account);
                        if (reportTime != null) {
                            Date memberReportTime = DateUtil.parse(reportTime);
                            //要大于当前天的0点，跨天查询不行
                            if (memberReportTime.getTime() > startDate.getTime()) {
                                startDate = memberReportTime;
                            }
                        }

                        boolean doItem = true;
                        int pageIndex = 1;
                        Date lastDate = startDate;
                        while (doItem) {
                            //玩家游戏记录
                            JSONArray list = gameLog(pageIndex, pageSize, account, startDate, endDate);

                            if (list == null || list.size() == 0) {
                                break;
                            }

                            doItem = list.size() >= pageSize;
                            pageIndex++;

                            List<Record> recordList = new ArrayList<>();
                            for (Object item_list : list) {
                                JSONObject account_item = (JSONObject) item_list;

                                Record record = new Record();

                                record.set("account", account);
                                record.set("BeginBlance", account_item.getString("BeginBlance"));
                                record.set("ClassID", account_item.getInteger("ClassID"));
                                record.set("CreateTime", account_item.getString("CreateTime"));
                                record.set("EndBlance", account_item.getString("EndBlance"));
                                record.set("GameID", account_item.getInteger("GameID"));
                                record.set("GameName", account_item.getString("GameName"));
                                record.set("LineNum", account_item.getInteger("LineNum"));
                                record.set("LogDataStr", account_item.getString("LogDataStr"));
                                record.set("LogDataType", account_item.getInteger("LogDataType"));
                                record.set("RoundNO", account_item.getInteger("RoundNO"));
                                record.set("Rownum", account_item.getInteger("Rownum"));
                                record.set("TableID", account_item.getInteger("TableID"));
                                record.set("Win", account_item.getDouble("Win"));
                                record.set("bet", account_item.getDouble("bet"));
                                record.set("cday", account_item.getInteger("cday"));
                                record.set("cno", account_item.getInteger("cno"));
                                record.set("id", account_item.getInteger("id"));
                                record.set("uuid", account_item.getString("uuid"));
                                record.set("api", getApi());

                                recordList.add(record);

                                Date createTime = account_item.getDate("CreateTime");

                                if (createTime.getTime() > lastDate.getTime()) {
                                    lastDate = createTime;
                                }
                            }

                            String record_key = "api:Task:kiss918:signRecord:" + BaseService.signRecord(recordList);
                            if (RedisLock.lock(record_key, 300)) {
                                BaseService.insertOrUpdate("api_918kiss_ticket", recordList);
                            }

                            try {
                                Thread.sleep(220);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                        }
                        redis.setex(accountGameLog_key + account, 3600 * 24, DateUtil.formatDate(lastDate));
                    }

                    try {
                        Thread.sleep(220);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


    /**
     * 查玩家上下分明细
     *
     * @param pageIndex
     * @param userName
     * @param sDate
     * @param eDate
     * @return
     */
    public JSONObject userScoreLog(Integer pageIndex, String userName, Date sDate, Date eDate) {

        String time = String.valueOf(System.currentTimeMillis());

        Map<String, String> map = new HashMap<>();
        map.put("pageIndex", String.valueOf(pageIndex));
        map.put("userName", userName);
        map.put("sDate", DateUtil.formatDate(sDate, "yyyy-MM-dd"));
        map.put("eDate", DateUtil.formatDate(eDate, "yyyy-MM-dd"));
        map.put("time", time);
        map.put("authcode", iv);
        map.put("sign", sign(userName, time));

        String addUserUrl = HOST2 + "/ashx/UserscoreLog.ashx";
        return postData(addUserUrl, map);
    }

    /**
     * 查玩家游戏记录
     *
     * @param pageIndex
     * @param pageSize
     * @param account
     * @param sDate
     * @param eDate
     * @return
     */
    public JSONArray gameLog(int pageIndex, int pageSize, String account, Date sDate, Date eDate) {

        String time = String.valueOf(System.currentTimeMillis());

        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(pageIndex));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("userName", account);
        params.put("sDate", DateUtil.formatDate(sDate));
        params.put("eDate", DateUtil.formatDate(eDate));
        params.put("time", time + "");
        params.put("authcode", iv);
        params.put("sign", sign(account, time));

        try {
            String url = HOST2 + "/ashx/GameLog.ashx";
            JSONObject result = postData(url, params);

            logger_json.info("account:{},startDate:{},endDate:{} result:{}", account, DateUtil.formatDateISO(sDate),
                    DateUtil.formatDate(eDate), result.toJSONString());

            if (result != null && result.getBooleanValue("success")) {
                return result.getJSONArray("results");
            }
            logger.error("gameLog postUrl:{} params:{} result:{}", url, JSONObject.toJSONString(params), result);
        } catch (Exception ex) {
            logger.error("gameLog.error", ex);
            ex.printStackTrace();
        }

        return null;
    }


    /**
     * 禁用开启账号
     *
     * @param userName
     * @return
     */
    public JSONObject disable(String userName) {

        Record user = Kiss918ApiService.getUser(userName);
        String account = user.get("account");

        String time = String.valueOf(System.currentTimeMillis());

        Map<String, String> map = new HashMap<>();
        map.put("action", "disable");
        map.put("userName", account);

        map.put("time", time);
        map.put("authcode", iv);
        map.put("sign", sign(account, time));

        String addUserUrl = HOST + "/ashx/account/account.ashx";
        return postData(addUserUrl, map);
    }

    /**
     * 查订单
     *
     * @param orderId
     * @return
     */
    public JSONObject getOrder(String orderId) {

        String time = String.valueOf(System.currentTimeMillis());

        Map<String, String> map = new HashMap<>();
        map.put("orderid", orderId);
        map.put("time", time);
        map.put("authcode", iv);
        map.put("sign", sign(orderId, time));

        String addUserUrl = HOST2 + "/ashx/getOrder.ashx";
        return postData(addUserUrl, map);
    }

    public double getBalance(String userName) {
        return getGameBalance(userName);
    }

    public String getLoginUrl(String memberId, String gameId, String lang) {
        logger.info("Kiss918.getLoginUrl memberId:{} gameId:{} lang:{}", memberId, gameId, lang);

        //测试用
//        if(1==1)
//        return "/launchApp?api=918kiss";

        MemberInfo memberInfo = Kiss918ApiService.getMemberInfo(memberId);
        if (memberInfo == null) {
            logger.error("memberInfo == null memberId:{}", memberId);
            return "/launchApp";
        }

        if (!Kiss918ApiService.exists(memberId)) {

//            String account = memberInfo.getUserName();
            // TODO : see this
            String uid = " ";
            String pwd = memberInfo.getPwdText();
            String account = randomUserName();
            String name = memberInfo.getUserName();
            boolean result = createPlayer(uid, name);
            logger.info("Kiss918.createMember result:{}", result);

            if (!result) {
                return "/launchApp";
            }
            // 记录创建的账号
            Record model = new Record();
            model.set("api", getApi());
            model.set("agentId", getApiAgent());
            model.set("memberId", memberId);
            model.set("currency", getCurrency());
            model.set("account", account);
            model.set("userName", name);
            model.set("pwd", pwd);
            model.set("createDate", new Date());
            Kiss918ApiService.createUser(model);
        } else {
            //检查密码是否已修改，如果修改需要同步到app
            String curPwd = memberInfo.getPwdText();
            Record bean = Kiss918ApiService.getUser(memberInfo.getUserName());
            String regPwd = bean.getStr("pwd");
            if (!regPwd.equals(curPwd)) {
                String account = bean.getStr("account");
                if (editUser(account, regPwd, curPwd)) {
                    Kiss918ApiService.updateUserPwdByMemberId(memberId, curPwd);
                }
            }
        }

        String userName = memberInfo.getUserName();
        GameInfo gameInfo = BaseService.getGameInfo(gameId);

        String txnId = StringUtil.shortUUID();
        //提出
        Ret ret = GeneralApi.withdraw2Balance(txnId, memberId);
        double userBalance = ret.getDouble("balance");
        Double amount = ret.getDouble("amount");
        if (amount == null) {
            //-1 提现失败
            amount = -1d;
        }

        //将平台余额充入游戏
        boolean depositResult = deposit2Game(txnId, memberId, userName, gameInfo.getPlatform(), gameInfo.getName());

        //记录进行游戏
        Kiss918ApiService.enterGameLog(ret.getStr("api"), txnId, memberId, userBalance, amount, depositResult, userName, getApi(),
                gameInfo.getGameType(), gameInfo.getPlatform(), gameInfo.getName(), gameInfo.getMultiple());

        //跳转到过渡页
        return "/launchApp?api=918kiss";
    }
    /*多钱包接口实现*/

    /**
     * 游戏余额
     *
     * @param userName
     * @return
     */
    public double getGameBalance(String userName) {
        JSONObject info = getUserInfo(userName);
        if (info == null) {
            return 0;
        }
        if (info.getBooleanValue("success")) {
            return info.getDouble("MoneyNum");

        }
        return 0;
    }

    /**
     * 充值上下分接口
     *
     * @param userName
     * @param txCode
     * @param type     1 withdraw 提出
     *                 0 deposit 存入
     * @param amount
     * @return
     */
    public int setScore(String userName, String txCode, int type, double amount) {

        Record user = Kiss918ApiService.getUser(userName);
        String account = user.get("account");
        String memberId = user.get("memberId");

        String time = String.valueOf(System.currentTimeMillis());
        JSONObject result = null;
        String opt;
        if (type == 1) {
            opt = "withdraw";
            //负数为减分
            amount = -1 * Math.abs(amount);
        } else {
            opt = "deposit";
        }

        String url = HOST + "/ashx/account/setScore.ashx";

        Map<String, String> params = new HashMap<>();
        params.put("action", "setServerScore");
        params.put("userName", account);
        params.put("scoreNum", amount + "");
        params.put("orderid", txCode);

        params.put("ActionUser", "api");
        params.put("ActionIp", "0.0.0.0");

        params.put("time", time);
        params.put("authcode", iv);
        params.put("sign", sign(account, time));

        try {
            result = postData(url, params);
            if (result != null) {
                String status = result.getString("code");
                if (status != null && status.equals("0")) {
                    return 1;
                }
                logger.error("Kiss918.{}.error userName:{} amount:{} result:{}", opt, userName, amount, result);
                return 0;
            } else {
                logger.error("Kiss918.{}}.error userName:{} amount:{} result:null", opt, userName, amount);
            }
            return 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Kiss918." + opt + ".error", ex);
            return -1;
        } finally {
            Record record = new Record()
                    .set("api", getApi())
                    .set("account", userName)
                    .set("memberId", memberId)
                    .set("txCode", txCode)
                    .set("createDate", new Date())
                    .set("type", type);

            // 记录日志
            String statusCode = null;
            if (result != null) {
                statusCode = result.getString("code");
                record.set("statusCode", result.getString("code"))
                        .set("amount", amount);
            }

            if (statusCode == null || !statusCode.equals("0")) {
                String postDataUrl = GeneralApi.createPostDataUrl(url, params);
                record.set("url", postDataUrl);
                //转账失败
                record.set("tranStatus", 0);
                if (statusCode != null) {
                    record.set("errCode", statusCode)
                            .set("errMsg", record.get("msg"));
                }
            }

            Db.save("api_918kiss_transfer_log", record);
        }
    }

    /**
     * 提现
     *
     * @param userName
     * @param txCode
     * @param amount
     * @return
     */
    public int withdraw(String userName, String txCode, double amount) {

        return setScore(userName, txCode, 1, amount);
    }

    /**
     * 充值
     *
     * @param userName
     * @param txCode
     * @param amount
     * @return
     */
    public int deposit(String userName, String txCode, long amount) {

        return setScore(userName, txCode, 0, amount);
    }

    /**
     * 提现到平台余额
     *
     * @param memberId
     * @param userName
     * @return
     */
    public Ret withdraw2Balance(String txnId, String memberId, String userName, String platform, String gameName) {

        String api = getApi();
        //取上一箇遊戲游戏余额
        double gameBalance = getGameBalance(userName);
        if (gameBalance > 0) {
            //四舍
            double amount = gameBalance;
            amount = StringUtil.roundDown(amount, 2);
            if (amount > 0) {
                String transferId = txnId + "-" + StringUtil.shortUUID();
                //调接口提出
                int result = withdraw(userName, transferId, amount);
                if (result == 1) {
                    //更新平台余额
                    BalanceResult balanceResult = BaseService.updateGetCreditsBalance(amount, memberId, api, transferId, transferId,
                            String.format("Kiss918.makeTransfer withdraw %s %s", platform, gameName));
                    if (balanceResult.getResult()) {
                        BaseService.enterGamePlatformWithdraw(transferId, amount, memberId, api, platform, gameName);
                        return Ret.ok().set("amount", amount).set("balance", balanceResult.getBalance());
                    }
                } else if (result == -1) {
                    // 检查游戏余额是否已经被减了，如果是扣减了，
                    double after_gameBalance = getGameBalance(userName);
                    if (gameBalance - amount == after_gameBalance) {
                        //更新平台余额
                        BalanceResult balanceResult = BaseService.updateGetCreditsBalance(amount, memberId, api, transferId, transferId,
                                String.format("Kiss918.makeTransfer withdraw %s %s redo before:%s after:%s amount:%s",
                                        platform, gameName, gameBalance, after_gameBalance, amount));
                        if (balanceResult.getResult()) {
                            BaseService.enterGamePlatformWithdraw(transferId, amount, memberId, api, platform, gameName);
                            return Ret.ok().set("amount", amount).set("balance", balanceResult.getBalance());
                        }
                    }
                }
                return Ret.fail();
            }
        }
        return Ret.fail().set("amount", 0).set("balance", 0);
    }

    /**
     * 提现到平台余额
     *
     * @param memberId
     * @param userName
     * @return
     */
    public Ret withdraw2Balance(String txnId, String memberId, String userName) {
        GameInfo lastRecord = BaseService.queryLastEnterGame(memberId);

        return withdraw2Balance(txnId, memberId, userName, lastRecord.getPlatform(), "");
    }

    public boolean deposit2Game(String txnId, String memberId, String userName, String platform) {
        return deposit2Game(txnId, memberId, userName, platform, "");
    }

    /**
     * 充值到游戏
     *
     * @param memberId
     * @param userName
     * @param platform
     * @return
     */
    public boolean deposit2Game(String txnId, String memberId, String userName, String platform, String gameName) {
        String api = getApi();
        //将平台余额充入游戏
        Map<String, Object> data = Kiss918ApiService.getBalance(userName, getApi());
        double userBalance = (double) data.get("balance");
        logger.info("Kiss918.deposit2Game userBalance:{}", userBalance);
        //取遊戲游戏余额
        double gameBalance = getGameBalance(userName);

        if (userBalance > 0f) {
            //四舍
            long amount = (long) (userBalance);
            if (amount > 0) {
                String transferId = txnId + "-" + StringUtil.shortUUID();
                logger.info("Kiss918.deposit2Game transferId:{}", transferId);
                //更新平台余额 减掉所有余额
                BalanceResult balanceResult = Kiss918ApiService.updateGetCreditsBalanceLockForGS(transferId,
                        "deposit", -1 * amount, memberId, api, transferId,
                        String.format("Kiss918.deposit %s %s", platform, gameName));

                logger.info("balanceResult.getResult() :{}", balanceResult.getResult());
                if (balanceResult.getResult()) {
                    logger.info("Kiss918.deposit2Game balanceResult:{}", JSONObject.toJSONString(balanceResult));

                    //调接口存入
                    int result = deposit(userName, txnId, amount);
                    if (result == 0) {
                        //如果充值不成功，回滚
                        BalanceRollBack rollBack = balanceResult.getRollBack();
                        if (rollBack != null) {
                            rollBack.rollBack();
                            return false;
                        }
                    } else if (result == -1) {
                        // 检查游戏余额是否已经被减了，如果是扣减了，
                        double after_gameBalance = getGameBalance(userName);
                        if (gameBalance == after_gameBalance) {
                            //如果充值不成功，回滚
                            BalanceRollBack rollBack = balanceResult.getRollBack();
                            if (rollBack != null) {
                                rollBack.rollBack();
                            }
                        }
                    }

                    BaseService.enterGamePlatformDeposit(memberId, userBalance, userName, api, platform, gameName, transferId, amount);
                    return result == 1;
                }
                return false;
            }
        }
        return true;
    }
}
