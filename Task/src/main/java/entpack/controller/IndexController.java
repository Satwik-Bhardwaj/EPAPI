package entpack.controller;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import entpack.service.CacheService;
import entpack.tasks.DelDataTask;
import entpack.utils.TaskUtil;

public class IndexController extends BaseController {

    public void index() {
        Cache redis = Redis.use();
        String jsonStr = "test";
        redis.lpush("test", jsonStr);

        set("test", "hello world");
        render("index.html");
    }

    public void delData() {
        DelDataTask.doItem();
    }

    public void clearLocalCache(String key){
        CacheService.clearLocalCache(key);
        renderText("ok");
    }


    public void stop(String name) {
        if (name == null) {
            TaskUtil.setStopTask();
            renderText(TaskUtil.getStopStatus() + "");
        } else {
            TaskUtil.setStopTask(name);
            renderText(TaskUtil.getStopStatus(name) + "");
        }
    }

    public void start(String name) {
        if (name == null) {
            TaskUtil.setStartTask();
            renderText(TaskUtil.getStopStatus() + "");
        } else {
            TaskUtil.getSetStopTask(name).set(false);
            renderText(TaskUtil.getStopStatus(name) + "");
        }
    }

}
