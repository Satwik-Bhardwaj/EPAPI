package entpack;

import cn.dreampie.quartz.QuartzPlugin;
import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;
import entpack.plugin.RedisPlugin;
import entpack.serializer.MyFstSerializer;
import entpack.utils.ArrayUtil;
import entpack.utils.DateUtil;
import entpack.utils.StringUtil;
import entpack.utils.UrlUtil;

import java.util.Date;

/**
 * 项目配置
 */
public class WebConfig extends JFinalConfig {
    public static void main(String[] args) throws Exception {


        UndertowServer.start(WebConfig.class, 8034, true);
        System.out.println("Task Start......");

        System.out.println("当前GMT+8时间: " + DateUtil.formatDate(new Date()));

        Record record = Db.findFirst("SELECT NOW() NOW");
        System.out.println("DB当前时间: " + DateUtil.formatDate(record.getDate("NOW")));


        Redis.use("ticket").set("test", "redis:test");
        System.out.println(Redis.use("ticket").get("test").toString());


//		new MemberTask().initData();
	}

    /**
     * 配置常量
     *
     * @param me
     */
    public void configConstant(Constants me) {
        // 加载少量必要配置，随后可用PropKit.get(...)获取值
        PropKit.use("config.txt");
        PropKit.use("gameConfig.txt");
        me.setDevMode(PropKit.getBoolean("devMode", false));
        me.setEncoding("UTF-8");

        //开启注解
        me.setInjectDependency(true);
        me.setJsonFactory(new MixedJsonFactory());
    }

    /**
     * 配置路由
     *
     * @param me
     */
    public void configRoute(Routes me) {
        //注册路由
        me.add(new RouteConfig());
    }

    public void configEngine(Engine me) {
        me.addSharedMethod(new DateUtil());
        me.addSharedMethod(new StringUtil());
        me.addSharedMethod(new UrlUtil());
        me.addSharedMethod(new ArrayUtil());

    }

    /**
     * 配置插件
     *
     * @param me
     */
    public void configPlugin(Plugins me) {
        //获取数据库账号、密码
        String dbUser = get("user"), dbPassword = get("password");
        // 配置druid数据库连接池插件
        DruidPlugin druidPlugin = new DruidPlugin(get("jdbcUrl"), dbUser, dbPassword);
        druidPlugin.setInitialSize(PropKit.getInt("druid.InitialSize", 10));
        druidPlugin.setMinIdle(PropKit.getInt("druid.MinIdle", 10));
        druidPlugin.setMaxActive(PropKit.getInt("druid.MaxActive", 100));
        druidPlugin.setDriverClass("com.mysql.cj.jdbc.Driver");

        //监控
        StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(PropKit.getBoolean("druid.logSlowSql", true));
        statFilter.setSlowSqlMillis(PropKit.getInt("druid.slowSqlMillis", 3 * 1000));
        druidPlugin.addFilter(statFilter);
        //防注入
        WallFilter wallFilter = new WallFilter();
        wallFilter.setDbType("mysql");
        druidPlugin.addFilter(wallFilter);

        //日志
        Log4jFilter log4jFilter = new Log4jFilter();
        log4jFilter.setStatementLogEnabled(true);
        log4jFilter.setStatementLogErrorEnabled(true);
        log4jFilter.setStatementExecutableSqlLogEnable(true);   //只开启执行sql
        log4jFilter.setStatementExecuteAfterLogEnabled(false);
        log4jFilter.setStatementExecuteUpdateAfterLogEnabled(false);
        log4jFilter.setStatementExecuteQueryAfterLogEnabled(false);
        log4jFilter.setStatementCloseAfterLogEnabled(false);
        log4jFilter.setStatementCreateAfterLogEnabled(false);
        log4jFilter.setStatementParameterClearLogEnable(false);
        log4jFilter.setStatementParameterSetLogEnabled(false);
        log4jFilter.setStatementPrepareAfterLogEnabled(false);
        log4jFilter.setStatementExecuteBatchAfterLogEnabled(false);
        log4jFilter.setStatementPrepareCallAfterLogEnabled(false);
        druidPlugin.addFilter(log4jFilter);

        me.add(druidPlugin);

        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        arp.setShowSql(false);
        // 配置大小写不敏感
        arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
//        arp.addSqlTemplate("report.sql");
        me.add(arp);

        //配置Api数据库
        DruidPlugin druidPlugin1 = new DruidPlugin(get("jdbcMember"), dbUser, dbPassword);
        druidPlugin1.addFilter(statFilter);
        druidPlugin1.addFilter(wallFilter);
        druidPlugin1.addFilter(log4jFilter);
        me.add(druidPlugin1);

        ActiveRecordPlugin arp1 = new ActiveRecordPlugin("member", druidPlugin1);
        arp1.setContainerFactory(new CaseInsensitiveContainerFactory(true));
        arp1.addSqlTemplate("all.sql");
        me.add(arp1);

        //定时任务
        QuartzPlugin quartz = new QuartzPlugin();
        quartz.setJobs("jobs.properties");
        quartz.setConfig("quartz.properties");
        me.add(quartz);

        //配置Redis数据源
        String redisPwd = PropKit.get("redis_pwd");
        String redis_host = get("redis_host");
        Integer redis_port = getInt("redis_port", 6379);

        RedisPlugin cache = new RedisPlugin("cache", redis_host,
                redis_port, 2000,
                redisPwd, 1);
        cache.setSerializer(MyFstSerializer.me);
        me.add(cache);

        String redisTicket_pwd = PropKit.get("redisTicket_pwd");
        String redisTicket_host = get("redisTicket_host");
        Integer redisTicket_port = getInt("redisTicket_port", 6379);
        Integer redisTicket_database = getInt("redisTicket_database", 1);

        // 正式的注单
        RedisPlugin redisPlugin = new RedisPlugin("ticket", redisTicket_host,
                redisTicket_port, 2000,
                redisTicket_pwd, redisTicket_database
        );
        redisPlugin.setSerializer(MyFstSerializer.me);
        me.add(redisPlugin);

    }

    /**
     * 配置全局拦截器
     *
     * @param me
     */
    public void configInterceptor(Interceptors me) {
        me.add(new SessionInViewInterceptor());
    }

    /**
     * 配置处理器
     *
     * @param me
     */
    public void configHandler(Handlers me) {
        me.add(new DruidStatViewHandler(PropKit.get("druid.visitPath")));
    }

    /**
     * 获取系统变量
     *
     * @param name
     * @return
     */
    private static String get(String name) {

        String val = PropKit.get(name);
        if (val == null) {
            return val;
        }

        if (val.indexOf("${") > -1) {
            // 将${name}替换成name，然后再取环境变量
            return System.getenv(val.replace("${", "")
                    .replace("}", ""));
        }
        return val;
    }

    private static Integer getInt(String name, int def) {
        String val = get(name);
        if (val == null) {
            return def;
        }

        return Integer.parseInt(val);
    }
}
