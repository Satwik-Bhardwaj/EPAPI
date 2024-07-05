package entpack.service;

public interface IGameBalanceService {


	/**
	 * 查询游戏余额
	 *
	 * @param memberId
	 * @return
	 */
	double queryGameBalance(String memberId);

	/**
	 * 同步余额
	 */
	void syncBalance();
}
