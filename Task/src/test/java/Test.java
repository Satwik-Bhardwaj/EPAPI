import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import entpack.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Test {

	private static String getAccount(String userName) {
		int index = userName.indexOf('0');
		if (index > -1) {
			userName = userName.substring(index + 1);
		}
		return userName;
	}

	public static void main(String[] args) {

		double amount = 196.61;
		System.out.println(StringUtil.roundDown(amount, 2));

		String json = "{\n" +
				"  \"code\": 0,\n" +
				"  \"msg\": \"\",\n" +
				"  \"data\": {\n" +
				"    \"apiVersion\": 20171010,\n" +
				"    \"valid\": true,\n" +
				"    \"result\": [\n" +
				"      {\n" +
				"        \"Id\": 1,\n" +
				"        \"UserId\": \"7093\",\n" +
				"        \"Accounts\": \"20jej\",\n" +
				"        \"KindID\": \"1\",\n" +
				"        \"ServerID\": \"1\",\n" +
				"        \"StartTime\": \"2023-01-19 22:19:00\",\n" +
				"        \"EndTime\": \"2023-01-19 22:19:00\",\n" +
				"        \"MatchTime\": \"2023-01-19 22:19:00\",\n" +
				"        \"BetDetail\": 0,\n" +
				"        \"Turnover\": 1.0,\n" +
				"        \"Bet\": 1.0,\n" +
				"        \"Payout\": 1.0,\n" +
				"        \"Commission\": 1.0,\n" +
				"        \"Status\": 1\n" +
				"      }\n" +
				"    ]\n" +
				"  }\n" +
				"}";
		JSONObject jsonObject = JSONObject.parseObject(json);

		JSONObject data = jsonObject.getJSONObject("data");
		if (data == null) {
			return;
		}
		JSONArray items = data.getJSONArray("result");

		List<Record> recordList = new ArrayList<>();

		for (Object item : items) {
			JSONObject object = (JSONObject) item;

			Record record = new Record();
			record.set("id", object.getString("Id"));
			record.set("userId", object.getString("UserId"));
			record.set("accounts", object.getString("Accounts"));
			record.set("kindId", object.getString("KindId"));
			record.set("serverId", object.getString("ServerId"));
			record.set("startTime", object.getString("StartTime"));
			record.set("endTime", object.getString("EndTime"));
			record.set("matchTime", object.getString("MatchTime"));
			record.set("betDetail", object.getString("BetDetail"));
			record.set("turnover", object.getDouble("Turnover"));
			record.set("bet", object.getDouble("Bet"));
			record.set("payout", object.getDouble("Payout"));
			record.set("commission", object.getDouble("Commission"));
			record.set("status", object.getInteger("Status"));

			recordList.add(record);
		}

		System.out.println(recordList);
	}
}
