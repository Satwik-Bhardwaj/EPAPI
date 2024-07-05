package entpack.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import entpack.utils.SessionUtil;

/**
 * 获取session拦截器
 */
public class SessionInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        SessionUtil.put(inv.getController().getSession(true));     //若存在会话则返回该会话，否则新建一个会话
        try {
            inv.invoke();
        } finally {
            SessionUtil.remove();
        }
    }
}