package entpack.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import entpack.Constant;
import entpack.bean.RedisTicket;
import entpack.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * redis 注单服务
 */
public class RedisTicketService   {

	private static String ticketKey = "ticket";

	private static Logger ticketLog = LoggerFactory.getLogger("ticketLog");

	public static void pushTicket(String id,
								  String gameId,
								  String memberId,
								  double amount,
								  double amountBet,
								  double amountWL,
								  int betNum,
								  double balance,
								  Date createDate,
								  String detail) {
		String dt = DateUtil.formatDate(createDate);
		RedisTicket ticket = new RedisTicket(id, gameId, memberId, dt,
				amount, amountBet, amountWL, betNum, balance);
		ticket.setDetail(detail);
		pushTicket(ticket);
	}

	public static void pushTicket(RedisTicket ticket) {
		String error = null;
		Cache redis = Redis.use("ticket");
		try {
			long startTime = System.currentTimeMillis();

			String jsonStr = JSON.toJSONString(ticket);
			redis.lpush(ticketKey, jsonStr);

			long endTime = System.currentTimeMillis();

			ticketLog.info("写入成功 pushTicket {}ms", (endTime - startTime));

		} catch (Exception ex) {
			ex.printStackTrace();
			ticketLog.error("pushTicket error", ex);
			error = getExceptionToString(ex);
		}

//		try {
//			String setKey = Constant.getGameBalanceKey( ticket.getMemberId(), ticket.getId());
//			String balance = redis.get(setKey);
//			if (balance != null) {
//				ticket.setBalance(Float.parseFloat(balance));
//			}
//
//		} catch (Exception ex) {
//			ticketLog.error("pushTicket gameBalance error", ex);
//			ex.printStackTrace();
//		}

		int status = error == null ? 1 : 2;
		BaseService.addTickLog(ticket, status, error);

	}

	/**
	 * 将 Exception 转化为 String
	 */
	public static String getExceptionToString(Throwable e) {
		if (e == null) {
			return "";
		}
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	/**
	 * 记录下注
	 *
	 * @param gameCode
	 * @param eventId
	 * @param memberId
	 * @param amount
	 */
	public static void pushBet(String gameCode, String eventId, String memberId,
							   float amount, String roundId) {

		Cache redis = Redis.use();

		RedisTicket ticket = new RedisTicket();
		ticket.setMemberId(memberId);
		ticket.setAmountBet(amount);
		ticket.setCreateDate(DateUtil.formatDate(new Date()));
		ticket.setDetail(roundId);
		redis.hset(gameCode, eventId, JSONObject.toJSONString(ticket));
	}

	/**
	 * 记录下注
	 *
	 * @param gameCode
	 * @param eventId
	 * @param ticket
	 */
	public static void pushBet(String gameCode, String eventId, RedisTicket ticket) {
		Cache redis = Redis.use();
		redis.hset(gameCode, eventId, JSONObject.toJSONString(ticket));
	}

	/**
	 * 获取下注记录
	 *
	 * @param gameCode
	 * @param eventId
	 * @return
	 */
	public static RedisTicket getBet(String gameCode, String eventId) {
		Cache redis = Redis.use();
		String data = redis.hget(gameCode, eventId);
		if (data == null) {
			return null;
		}
		return JSONObject.parseObject(data, RedisTicket.class);
	}

	/**
	 * 删除缓存的注单
	 *
	 * @param gameCode
	 * @param eventId
	 */
	public static void delBet(String gameCode, String eventId) {
		Cache redis = Redis.use();
		redis.hdel(gameCode, eventId);
	}

	/**
	 * 记录下注
	 *
	 * @param gameCode
	 * @param eventId
	 * @param memberId
	 * @param amount
	 */
	public static void pushBetList(String gameCode, String eventId, String memberId,
								   float amount) {

		Cache redis = Redis.use();

		List<RedisTicket> list = getBetList(gameCode, eventId);

		if (list == null) {
			list = new ArrayList<>();
		}


		RedisTicket ticket = new RedisTicket();
		ticket.setMemberId(memberId);
		ticket.setAmountBet(amount);
		ticket.setCreateDate(DateUtil.formatDate(new Date()));

		list.add(ticket);
		redis.hset(gameCode, eventId, JSONObject.toJSONString(list));
	}

	/**
	 * 获取下注记录
	 *
	 * @param gameCode
	 * @param eventId
	 * @return
	 */
	public static List<RedisTicket> getBetList(String gameCode, String eventId) {
		Cache redis = Redis.use();
		String data = redis.hget(gameCode, eventId);
		return JSONObject.parseArray(data, RedisTicket.class);
	}


}
