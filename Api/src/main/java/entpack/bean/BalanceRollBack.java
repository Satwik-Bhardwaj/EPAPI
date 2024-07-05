package entpack.bean;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import entpack.utils.RedisLock;

/**
 * 更新余额结果
 */
public class BalanceRollBack {

    String txId;
    String action;
    String memberId;
    double amount;
    String roundId;
    String remark;
    String api;


    /**
     * 余额回滚类
     *
     * @param txId
     * @param action
     * @param memberId
     * @param amount
     * @param api
     * @param roundId
     * @param remark
     */
    public BalanceRollBack(String txId, String action, String memberId, double amount,
                           String api, String roundId, String remark) {

        // String pkId, String action, float amt, String memberId,
        //     String api, String roundId, String remark

        this.txId = txId;
        this.action = action;
        this.memberId = memberId;
        this.amount = amount;
        this.roundId = roundId;
        this.remark = remark;
        this.api = api;
    }

    public String getTxId() {
        return txId;
    }

    public String getAction() {
        return action;
    }

    public String getMemberId() {
        return memberId;
    }

    public double getAmount() {
        return amount;
    }

    public String getRoundId() {
        return roundId;
    }

    public String getRemark() {
        return remark;
    }

    /**
     * 回滚余额
     */
    public void rollBack() {

        Double amt = -1 * amount;
        String key = String.format("lock:updateGetCreditsBalance:%s:%s:%s:%s:%s:%s",
                memberId, api, roundId, remark, amt, txId);

        if (RedisLock.lock(key, 3600 * 24)) {
            Db.use("member").findFirst("call updateBalance_Api(?,?,?,?,?,? )",
                    memberId, amt, api, roundId, txId, "rollBack:" + remark);
        }

    }
}
