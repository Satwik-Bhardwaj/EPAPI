package entpack.controller;

import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import com.jfinal.render.JsonRender;
import entpack.utils.AjaxResult;
import entpack.utils.RequestUtil;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * 控制器基类
 */
public class BaseController extends Controller {

	@NotAction
	public void renderAjaxResultForSuccess(String message) {
		renderAjaxResult(message, 0, null);
	}

	@NotAction
	public void renderAjaxResultForError(String message) {
		renderAjaxResult(message, 1, null);
	}

	@NotAction
	public void renderAjaxResult(String message, int errorCode, Object data) {
		AjaxResult ar = new AjaxResult();
		ar.setMsg(message);
		ar.setCode(errorCode);
		ar.setData(data);

		if (RequestUtil.isIEBrowser(getRequest())) {
			render(new JsonRender(ar).forIE());
		} else {
			renderJson(ar);
		}
	}

	/**
	 * 将参数转为浮点数
	 *
	 * @param name
	 * @return
	 */
	public Float getParaToFloat(String name) {
		String val = getPara(name);
		if (val == null) {
			return null;
		}
		return Float.parseFloat(val);
	}

	/**
	 * 获取 POST 的数据
	 *
	 * @return
	 */
	protected String getBody() {

		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = getRequest().getReader();
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (sb.length() == 0) return null;
		return sb.toString();
	}

	public void retOk(String msg) {
		renderJson(Ret.ok("msg", msg));
	}

	public void retFail(String msg) {
		renderJson(Ret.fail("msg", msg));
	}
}