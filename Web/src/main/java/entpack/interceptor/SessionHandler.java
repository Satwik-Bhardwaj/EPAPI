package entpack.interceptor;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 修复 url;jsessionid=XXXXXXXXXXX 形式url会话丢失问题
 */
public class SessionHandler extends Handler {

	@SuppressWarnings("deprecation")
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		int index = target.toLowerCase().lastIndexOf(";jsessionid");
		target = index == -1 ? target : target.substring(0, index);
		if (target.indexOf("oauth2.0") > -1) {
			nextHandler.handle(target.replace("oauth2.0", "oauth20"), request, response, isHandled);
			return;
		}
		nextHandler.handle(target, request, response, isHandled);
	}
}
