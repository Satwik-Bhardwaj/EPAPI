package entpack.api;

import com.jfinal.kit.Ret;

/**
 * 多钱包接口
 */
public interface MultipleInterface {
    double getGameBalance(String userName);

//    int withdraw(String userName, String transferId);

//    int deposit(String memberId, String txCode, long transferAmount);

    /**
     * 提现到平台余额
     *
     * @param memberId
     * @param userName
     * @return
     */
    Ret withdraw2Balance(String txnId, String memberId, String userName, String platform, String gameName);

    /**
     * 提现到平台余额
     *
     * @param memberId
     * @param userName
     * @return
     */
    Ret withdraw2Balance(String txnId, String memberId, String userName);

    boolean deposit2Game(String txnId, String memberId, String userName, String platform);

    /**
     * 充值到游戏
     *
     * @param memberId
     * @param userName
     * @param platform
     * @return
     */
    boolean deposit2Game(String txnId, String memberId, String userName, String platform, String gameName);
}
