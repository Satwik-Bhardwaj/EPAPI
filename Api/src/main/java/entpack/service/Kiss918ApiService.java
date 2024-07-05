package entpack.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import entpack.bean.MemberInfo;
import entpack.utils.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kiss918ApiService extends BaseService {

    private static String tname = "api_918kiss";

    @Override
    protected String getGameId() {
        return "918kiss";
    }

    public static boolean exists(String memberId) {
        return exists(memberId, tname);
    }

    public static void createUser(Record model) {
        BaseService.createUser(model, tname);
    }

    public static Record getUser(String userName) {
        String sql = "select agentId,memberId,account,userName,pwd from " + tname + "_create where userName=?";
        Record record = Db.findFirst(sql, userName);
        return record;
    }

    public static Record getUserByAccount(String account) {
        String sql = "select agentId,memberId,account,userName from " + tname + "_create where account=?";
        Record record = Db.findFirst(sql, account);
        return record;
    }

    /**
     * 修改密码
     *
     * @param memberId
     * @param pwd
     */
    public static void updateUserPwdByMemberId(String memberId, String pwd) {
        String sql = "update " + tname + "_create set pwd= ? where memberId= ? ";
        Db.update(sql, pwd, memberId);
    }

    /**
     * 获取余额
     *
     * @param userName
     * @return
     */
    public static Map<String, Object> getBalance(String userName, String api) {
        Map<String, Object> result = new HashMap<>();
        Record bean = getUser(userName);
        /*
         * 0 =>无错误。成功回应。
         * 1000 =>用户帐户不存在
         * 1001 =>货币无效
         * 1004 =>锁定帐户
         * 9999 =>系统错误
         * */
        if (bean == null) {
            // 账号不存在
            result.put("code", 1000);
            return result;
        }

        // 改用会员信用余额
        String memberId = bean.getStr("memberId");
        MemberInfo memberInfo = getMemberInfo(memberId, api);

        Date balanceTimestamp = memberInfo.getBalanceTimestamp();
        if (balanceTimestamp == null) {
            balanceTimestamp = new Date();
        }
        result.put("code", 0);
        result.put("userId", bean.getStr("userId"));
        result.put("currency", bean.getStr("currency"));
        result.put("balance", memberInfo.getCreditsBalance());
        // "2019-06-13T21:18:55+08:00"
        result.put("balanceTimestamp", DateUtil.formatDateISO(balanceTimestamp));
        return result;
    }

    public static List<Record> queryTicket() {
        String sql = "select `account`, `uuid`, `CreateTime`, `GameName`, `bet`, `Win`, `ticketStatus`, `api` " +
                "from api_918kiss_ticket " +
                "where ticketStatus=0 and GameID>-1 limit 1000";
        return Db.find(sql);
    }


    /**
     * 更新注单成功
     *
     * @param betId
     */
    public static void updateTicket(String betId) {
        updateTicket(betId, 1, null);
    }

    /**
     * 更新注单
     *
     * @param betId  注单ID
     * @param status 状态
     * @param msg    信息
     */
    public static void updateTicket(String uuid, int status, String msg) {
        if (msg == null) {
            String sql = "update api_918kiss_ticket set ticketStatus = ? where uuid=?";
            Db.update(sql, status, uuid);
            return;
        }
        String sql = "update api_918kiss_ticket set ticketStatus = ?, ticketMsg=? where uuid=?";
        Db.update(sql, status, msg, uuid);
    }
}
