package entpack.bean;

import com.jfinal.plugin.activerecord.Record;

public class ConfigInfo extends CalcInfo {
	protected String id, userName, parentId;

	String agentId;
	String masterId;

	int agentMasterDirect;
	int agentType;

	/**
	 * 0：会员；1:代理；2:白牌
	 */
	protected int type = 0;

	protected Float comm, ptMy, ptSub, ptForce, ptUp;


	protected Float ptRemaining = 0f;

	protected Integer memberType;
	protected Integer masterDirect;

	public ConfigInfo() {

	}

	public ConfigInfo(MemberGameConfig info) {

		type = 0;
		id = info.getId();
		masterId = info.getMasterId();
		agentId = info.getAgentId();

		setComm(info.getComm());
		setPtUp(info.getPtUp());
		setPtMy(info.getPtMy());

		ptForce = 0f;
		ptSub = 0f;
		userName = info.getUserName();

		memberType = info.getMemberType();
		masterDirect = info.getMasterDirect();
	}

	public ConfigInfo(int type, Record record) {
		comm = record.getFloat("comm");
		ptMy = record.getFloat("ptMy");
		ptRemaining = record.getFloat("ptRemaining");
		if (ptRemaining == null) {
			ptRemaining = 0f;
		}
		ptSub = record.getFloat("ptSub");
		ptForce = record.getFloat("ptForce");
		ptUp = record.getFloat("ptUp");

		this.type = type;

		id = record.getStr("id");
		userName = record.getStr("userName");
		parentId = record.getStr("parentId");

		if (type == 1) {
			// 代理
			agentId = record.getStr("id");
			masterId = record.getStr("masterId");

			agentType = record.getInt("agentType");
			agentMasterDirect = record.getInt("agentMasterDirect");
		} else if (type == 2) {
			// 白牌
			masterId = record.getStr("id");
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Float getComm() {
		return comm;
	}

	public void setComm(Float comm) {
		this.comm = comm;
	}

	public Float getPtMy() {
		return ptMy;
	}

	public void setPtMy(Float ptMy) {
		this.ptMy = ptMy;
	}

	public Float getPtSub() {
		return ptSub;
	}

	public void setPtSub(Float ptSub) {
		this.ptSub = ptSub;
	}

	public Float getPtForce() {
		return ptForce;
	}

	public void setPtForce(Float ptForce) {
		this.ptForce = ptForce;
	}

	public Float getPtUp() {
		return ptUp;
	}

	public void setPtUp(Float ptUp) {
		this.ptUp = ptUp;
	}


	public Float getPtRemaining() {
		return ptRemaining;
	}

	public void setPtRemaining(Float ptRemaining) {
		this.ptRemaining = ptRemaining;
	}

	public String getAgentId() {
		return agentId;
	}


	public int getAgentMasterDirect() {
		return agentMasterDirect;
	}

	public void setAgentMasterDirect(int agentMasterDirect) {
		this.agentMasterDirect = agentMasterDirect;
	}

	public int getAgentType() {
		return agentType;
	}

	public void setAgentType(int agentType) {
		this.agentType = agentType;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public Integer getMemberType() {
		return memberType;
	}

	public Integer getMasterDirect() {
		return masterDirect;
	}
}
