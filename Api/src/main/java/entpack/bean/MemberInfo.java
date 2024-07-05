package entpack.bean;

import java.util.Date;

public class MemberInfo {
	String masterId;
	String agentId;
	String userName;
	String pwd;
	String pwdText;
	String userNameOld;
	String fullName;
	int memberType;
	String currency;
	double creditsBalance;
	Date balanceTimestamp;
	int status;
	int masterDirect;

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserNameOld() {
		return userNameOld;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserNameOld(String userName) {
		this.userNameOld = userName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public int getMemberType() {
		return memberType;
	}

	public void setMemberType(int memberType) {
		this.memberType = memberType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getBalanceTimestamp() {
		return balanceTimestamp;
	}

	public void setBalanceTimestamp(Date balanceTimestamp) {
		this.balanceTimestamp = balanceTimestamp;
	}

	public double getCreditsBalance() {
		return creditsBalance;
	}

	public void setCreditsBalance(double creditsBalance) {
		this.creditsBalance = creditsBalance;
	}

	/**
	 * 状态,0禁用,1启用,2冻结
	 *
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public int getMasterDirect() {
		return masterDirect;
	}

	public void setMasterDirect(int masterDirect) {
		this.masterDirect = masterDirect;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getPwdText() {
		return pwdText;
	}

	public void setPwdText(String pwdText) {
		this.pwdText = pwdText;
	}
}
