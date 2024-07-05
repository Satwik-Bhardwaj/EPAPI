package entpack.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import entpack.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 信用服务类
 */
public class CreditService {
	private static Logger logger = LoggerFactory.getLogger("balanceLog");

	/**
	 * 更新上级余额
	 *
	 * @param parentType    上级类型,master 或 agent
	 * @param parentId      上级ID
	 * @param amount        变动的金额
	 * @param parentBalance 上级余额
	 * @param parentAmount  上级信用额度
	 * @param createUser    操作人
	 * @param optTime       操作时间
	 * @param targetUser    目标账号
	 * @return 返回true为更新成功
	 */
	public boolean updateParentBalance(String parentType, String parentId, BigDecimal amount, BigDecimal parentBalance,
									   BigDecimal parentAmount, String createUser, Date optTime, String targetUser) {
		String funName = parentType.equals("master") ? "updateBalanceMaster" : "updateBalanceAgent";
//		Record record = new Record().set("id", parentId).set("amount", amount)
//				.set("createUser", createUser)
//				.set("optTime", optTime)
//				.set("targetUser", targetUser);
//		redis.rpush(funName, record.toJson());
		Record _ret = Db.use("member").findFirst("CALL " + funName + "(?, ?);", parentId, amount);
		if (_ret.getInt("result") > 0) {
			BigDecimal _balance = _ret.getBigDecimal("balance");
			addLog(parentType, parentId, "manual", parentBalance, _balance, parentAmount, createUser, optTime, targetUser);
			return true;
		}
		return false;
	}


	/**
	 * 增加余额变动记录
	 *
	 * @param tb            数据标识 master 或 agent 或 member
	 * @param pkId          主键ID
	 * @param option        操作：auto-自动、manual-手动
	 * @param before        变动前余额
	 * @param after         变动后余额
	 * @param creditsAmount 当前信用额度
	 * @param createUser    操作人
	 * @param createDate    变动时间
	 * @param targetUser    被操作人
	 */
	public void addLog(String tb, String pkId, String option, BigDecimal before, BigDecimal after, BigDecimal creditsAmount,
	                   String createUser, Date createDate, String targetUser) {
		Record record = new Record().set("id", StringUtil.shortUUID())
				.set(tb + "Id", pkId)
				.set("type", 1)
				.set("option", option)
				.set("balanceBefore", before)
				.set("balanceAfter", after)
				.set("balance", after.subtract(before))
				.set("creditsAmount", creditsAmount)
				.set("createDate", createDate)
				.set("createUser", createUser)
				.set("targetUser", targetUser);
		Db.use("member").save("tb_" + tb + "_balance", record);
	}
}
