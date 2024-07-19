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
	 * 计算签名
	 *
	 * @param m
	 * @return
	 */
	public static String getSign(String m) {
		System.out.println(MD5Util.md5(m + key));
		return MD5Util.md5(m + key);
	}


}
