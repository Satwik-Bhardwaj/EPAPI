package entpack.controller;

import entpack.api.*;
import entpack.bean.GameInfo;
import entpack.bean.MemberInfo;
import entpack.service.*;
import entpack.utils.MD5Util;
import entpack.utils.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

public class LaunchController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(LaunchController.class);

	private static String key = "gg";

//    String redisKey = "sign:";

//    private Cache redis = Redis.use();

	public static void main(String[] args) {

		System.out.println((int) 1.6);
		System.out.println(getSign("EXVLBhxl"));
	}

	/**
	 * 检查是否禁用状态
	 *
	 * @param memberId
	 * @return
	 */
	private boolean checkMemberStatus(String memberId) {
		MemberInfo memberInfo = BaseService.getMemberInfo(memberId);
		if (memberInfo == null) {
			return false;
		}
		return memberInfo.getStatus() != 0;
	}

	/**
	 * 默认请求地址
	 * /api/launch/{gameType}?m=XXXX
	 */
	public void index() {

		String api = getPara(0);
		String memberId = getPara("m");
		String s = getPara("s");
		String name = getPara("n");
		String gameId = getPara("g");
		String lang = getPara("lang");
		String gameType = getPara("gameType");
		Boolean getUrl = getBoolean("getUrl");

		logger.info("gameType:{},memberId:{},s:{},name:{},gameId:{},lang:{}" +
						" isMoblieBrowser:{}", api, memberId, s, name, gameId, lang,
				RequestUtil.isMoblieBrowser(getRequest()));
		logger.info("checkMemberStatus:________");
		if (s == null || !getSign(memberId).equals(s)) {
			logger.info("s:{}  getSign(memberId):{}", s, getSign(memberId));

			System.out.println("s == null || !getSign(memberId).equals(s):" + getSign(memberId));
			renderNull();
			return;
		}
//        String key = redisKey + s;
//        if (redis.exists(key)) {
//            redis.del(key);
//        } else {
//            renderText("token is expired");
//        }

		// 如果是禁用的不能进入游戏
		logger.info("checkMemberStatus:________");
		boolean checkStatus = checkMemberStatus(memberId);
		logger.info("checkMemberStatus:________" + checkStatus);
		if (!checkStatus) {
			logger.info("checkMemberStatus memberId:{} checkStatus:{}  ", memberId, checkStatus);

			renderNull();
			return;
		}

		if (lang == null) {
			lang = "";
		}

		HttpServletResponse response = getResponse();
		logger.info("getUrl:________1212");
		String url = getUrl(api, memberId, name, gameId, lang, gameType);
		logger.info("url:" + url);

		logger.info("User-Agent:{}", getRequest().getHeader("User-Agent"));

		if (getUrl != null && getUrl) {
			renderText(url);
		} else {
			response.setStatus(302);
			response.setHeader("Location", url);
			renderNull();
		}
	}

	/**
	 * 计算签名
	 *
	 * @param m
	 * @return
	 */
	public static String getSign(String m) {
		System.out.println(MD5Util.md5(m + key));
		return MD5Util.md5(m + key);
	}

	/**
	 * 取得游戏登陆地址
	 *
	 * @param gameType
	 * @param memberId
	 * @return
	 */
	private String getUrl(String api, String memberId, String name, String gameId, String lang, String gameType) {

		String url;
		GameInfo gameInfo;
		String currency;
		switch (api) {

			case "918kiss": case "918kisssgd":
				gameInfo = BaseService.getGameInfo(gameId);
				url = ApolloApi.getInstance(gameInfo.getCurrency()).getLoginUrl(memberId, gameId, lang);
				break;

			default:

				url = "";
		}

		return url;
	}


}
