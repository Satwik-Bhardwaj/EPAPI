package entpack.bean;

import com.jfinal.plugin.activerecord.Record;

/**
 * 更新余额结果
 */
public class BalanceResult {

    int result;
    float balance;
    float amt;
    Boolean locked;

    BalanceRollBack rollBack;

    public BalanceResult(Record record, BalanceRollBack rollBack) {
        result = record.getInt("result");
        balance = record.getFloat("balance");
        amt = record.getFloat("amt");
        locked = record.getBoolean("locked");
        this.rollBack = rollBack;
    }

    /**
     * 更新结果
     *
     * @return
     */
    public boolean getResult() {
        return result > 0;
    }

    /**
     * 余额
     *
     * @return
     */
    public Float getBalance() {
        return balance;
    }

    /**
     * 变动前余额
     *
     * @return
     */
    public Float getBeforeBalance() {
        return balance - amt;
    }

    public BalanceRollBack getRollBack() {
        return rollBack;
    }

    public Boolean isLocked() {
        return locked;
    }
}
