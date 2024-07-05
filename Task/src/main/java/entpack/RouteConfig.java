package entpack;

import com.jfinal.config.Routes;
import entpack.controller.IndexController;

/**
 * 路由配置
 */
public class RouteConfig extends Routes {
    public void config() {
        setBaseViewPath("/views/");
        add("/", IndexController.class, "index");

        System.out.println("RouteConfig");
    }
}
