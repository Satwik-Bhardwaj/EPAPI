package entpack.bean;

import com.jfinal.plugin.activerecord.Record;

import java.util.Date;

public class RedisTicket extends MemberGameConfig {
    private String id;
    private String gameId;
    private String memberId;
    private String createDate;
    private String detail;
    private String platform;

    /**
     * 有效金额
     */
    private double amount;

    /**
     * 下注金额
     */
    private double amountBet;

    /**
     * 赢输金额，赢为正，输为负
     */
    private double amountWL;
    private int betNum = 1;
    private double balance;

    /**
     * 抽水
     */
    private double tax = 0;

    /**
     * 游戏名
     */
    private String gameName;
    /**
     * 下注时间
     */
    private String betTime;
    /**
     * 下注内容
     */
    private String betType;
    /**
     * 开牌结果
     */
    private String winner;

    /**
     * 玩家IP
     */
    private String ip;

    private String roundId;
    private String gameType;

    public RedisTicket() {

    }

    public RedisTicket(String id,
                       String gameId,
                       String memberId,
                       String createDate,
                       double amount,
                       double amountBet,
                       double amountWL,
                       int betNum,
                       double balance) {
        this.id = id;
        this.gameId = gameId;
        this.memberId = memberId;
        this.createDate = createDate;
        this.amount = amount;
        this.amountBet = amountBet;
        this.amountWL = amountWL;
        this.betNum = betNum;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public double getAmountBet() {
        return amountBet;
    }

    public void setAmountBet(float amountBet) {
        this.amountBet = amountBet;
    }

    public double getAmountWL() {
        return amountWL;
    }

    public void setAmountWL(float amountWL) {
        this.amountWL = amountWL;
    }

    public int getBetNum() {
        return betNum;
    }

    public void setBetNum(int betNum) {
        this.betNum = betNum;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getBetTime() {
        return betTime;
    }

    public void setBetTime(String betTime) {
        this.betTime = betTime;
    }

    public String getBetType() {
        return betType;
    }

    public void setBetType(String betType) {
        this.betType = betType;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getRoundId() {
        return roundId;
    }

    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    /**
     * 抽水
     *
     * @param tax
     */
    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTax() {
        return tax;
    }
}
