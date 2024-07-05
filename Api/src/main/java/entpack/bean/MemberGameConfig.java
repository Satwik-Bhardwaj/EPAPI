package entpack.bean;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;

public class MemberGameConfig {

	String id;

	Integer memberType;
	Integer masterDirect;

	String agentId;
	String masterId;

	String userName;

	protected Float comm, ptMy, ptUp;

	String md5;

	int configCount = 0;

	public MemberGameConfig() {

	}

	public MemberGameConfig(Record record) {

		initConfig(record);
	}

	public static void main(String[] args) {
		RedisTicket ticket = new RedisTicket();
		JSONObject recordConfig = JSONObject.parseObject("{\"masterId\":\"BgMS6qJZ\",\"comm\":0.8,\"ptMy\":95.0,\"balance\":84050.24,\"masterDirect\":1,\"memberType\":1,\"ptUp\":0.0,\"userName\":\"testmem916\"}");
		ticket.initConfig(new Record().setColumns(recordConfig));

		System.out.println(JsonKit.toJson(ticket));
	}

	public void initConfig(Record record) {

		id = record.getStr("id");
		userName = record.getStr("userName");

		memberType = record.getInt("memberType");
		masterDirect = record.getInt("masterDirect");
		agentId = record.getStr("agentId");
		masterId = record.getStr("masterId");

		comm = record.getFloat("comm");
		ptMy = record.getFloat("ptMy");

		ptUp = record.getFloat("ptUp");

		md5 = record.getStr("md5");
	}

	public Integer getMemberType() {
		return memberType;
	}

	public Integer getMasterDirect() {
		return masterDirect;
	}



	public String getAgentId() {
		return agentId;
	}

	public String getMasterId() {
		return masterId;
	}

	public Float getComm() {
		return comm;
	}

	public Float getPtMy() {
		return ptMy;
	}

	public Float getPtUp() {
		return ptUp;
	}

	public String getUserName() {
		return userName;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public int getConfigCount() {
		return configCount;
	}

	public void setConfigCount(int configCount) {
		this.configCount = configCount;
	}

	public String getId() {
		return id;
	}
}
