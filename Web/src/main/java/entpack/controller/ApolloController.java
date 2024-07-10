package entpack.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import entpack.api.ApolloApi;
import entpack.bean.MemberInfo;
import entpack.service.ApolloApiService;
import entpack.utils.DateUtil;
import entpack.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class ApolloController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger("Apollo");

	/**
	 * 返回游戏账号
	 * @param userName
	 * @return
	 */
    public String getAccount(String userName) {

        Record record = ApolloApiService.getUser(userName);
        if (record == null) {
            return null;
        }
        return record.get("account");
    }

    public void loginUrl(String currency, String memberId, String gameId) {
        if (currency == null) {
            currency = "MYR";
        }

        ApolloApi api = ApolloApi.getInstance(currency);

        String result = api.getLoginUrl(memberId, gameId, "en");
        System.out.println(result);
        renderJson(result);
    }

    /**
     * 用户注册接口
     *
     * @param currency
     * @param userName
     */
    public void randomUserName(String currency, String userName) {
        if (currency == null) {
            currency = "MYR";
        }

        renderJson(ApolloApi.getInstance(currency).randomUserName());
    }

    /**
     * 添加用户接口
     */

    // create player
    public void createPlayer(String currency, String uid, String creditAllocated) {
        if (currency == null) {
            currency = "MYR";
        }
        renderJson(ApolloApi.getInstance(currency).createPlayer(uid, creditAllocated));
    }

    //obtain token
    public void obtainToken(String uid,String lang,String gType,String mute,String currency){
        if(currency==null){
            currency="MYR";
        }
        renderJson(ApolloApi.getInstance(currency).obtainToken(uid,lang,gType,mute,currency));
    }

    //search player information
    public void searchPlayer(String currency, String playerId) {
        if (currency == null) {
            currency = "MYR";
        }
        renderJson(ApolloApi.getInstance(currency).searchPlayer(playerId));

    }

    //withdraw or deposit
    public void withdrawOrDeposit(String currency, double amount, String playerId, String remark,
                                  String allCashOutFlag) {
        if (currency == null) {
            currency = "MYR";
        }
        if (allCashOutFlag == null) {
            allCashOutFlag = "0";
        }
        renderJson(ApolloApi.getInstance(currency).withdrawOrDeposit(amount, playerId, remark, allCashOutFlag));
    }
    /**
     * 修改用户信息
     */
    public void editUser(String currency, String userName, String oldPassWd, String passWd) {
        if (currency == null) {
            currency = "MYR";
        }

        renderJson(ApolloApi.getInstance(currency).editUser(userName, oldPassWd, passWd));
    }

    /**
     * 充值上下分接口
     */
    public void setScore(String currency, String userName, String txCode, Integer type, double scoreNum) {
        if (currency == null) {
            currency = "MYR";
        }

        if (type == null) {
            type = 0;
        }

        renderJson(ApolloApi.getInstance(currency).setScore(userName, txCode, type, scoreNum));
    }

    /**
     * 查询游戏余额
     */
    public void getGameBalance(String currency, String userName) {
        if (currency == null) {
            currency = "MYR";
        }
        renderJson(ApolloApi.getInstance(currency).getGameBalance(userName));
    }

    /**
     * 查玩家游戏记录
     *
     * @param pageIndex
     * @param userName
     * @param sDate
     * @param eDate
     * @return
     */
    public void gameLog(String currency, Integer pageIndex, String userName, Date sDate, Date eDate) {
        if (currency == null) {
            currency = "MYR";
        }

        renderJson(ApolloApi.getInstance(currency).gameLog(pageIndex, 100, userName, sDate, eDate));
    }

    /**
     * 提现到余额
     *
     * @param currency
     * @param memberId
     */
    public void withdraw2Balance(String currency, String memberId) {
        if (currency == null) {
            currency = "MYR";
        }

        String txnId = StringUtil.shortUUID();
        MemberInfo memberInfo = ApolloApiService.getMemberInfo(memberId);
        Ret result = ApolloApi.getInstance(currency).withdraw2Balance(txnId, memberId, memberInfo.getUserName());
        renderText("result:" + result.toJson());
    }

    /**
     * 下属玩家或代理报表
     *
     * @param currency
     * @param userName
     * @param sDate
     * @param eDate
     * @param type
     */
    public void agentTotalReport(String currency, String userName, String sDate, String eDate, String type) {
        if (currency == null) {
            currency = "MYR";
        }

        Date startDate = DateUtil.parse(sDate, "yyyy-MM-dd");
        Date endDate = DateUtil.parse(eDate, "yyyy-MM-dd");

        JSONObject result = ApolloApi.getInstance(currency).agentTotalReport(userName, startDate, endDate, type);
        renderText("result:" + result.toJSONString());
    }


    /**
     * 查询代理或玩家列表
     *
     * @param currency
     * @param action
     * @param userName
     * @param pageIndex
     */
    public void accountList(String currency, String action, String userName, Integer pageIndex) {
        if (currency == null) {
            currency = "MYR";
        }

        JSONObject result = ApolloApi.getInstance(currency).accountList(action, userName, pageIndex);
        renderText("result:" + result.toJSONString());
    }

    public void disable(String currency, String userName) {
        if (currency == null) {
            currency = "MYR";
        }

        JSONObject result = ApolloApi.getInstance(currency).disable(userName);
        renderText("result:" + result.toJSONString());
    }

    public void queryTicket(String currency, String date) {
        if (currency == null) {
            currency = "MYR";
        }

        if (date == null) {
            date = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        }

        Date sDate = DateUtil.parse(date, "yyyy-MM-dd");

        ApolloApi.getInstance(currency).queryMemberReport(sDate);
        renderText("result:ok");
    }
    public void apiMap() {
        renderText(JSON.toJSONString(ApolloApi.getApiMap()));
    }

}
