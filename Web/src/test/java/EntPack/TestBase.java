package EntPack;

import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.json.JsonManager;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import entpack.serializer.MyFstSerializer;
import entpack.utils.StringUtil;
import org.junit.BeforeClass;

public class TestBase {
	@BeforeClass
	public static void init() {
		PropKit.use("config.txt");
		JsonManager.me().setDefaultJsonFactory(new MixedJsonFactory());

		String env = PropKit.get("env", "test");
		String dbUrl = PropKit.get("jdbcUrl");
		//配置主库
		configDB(PropKit.get("jdbcMember"), "member");

		//配置注单和报表明细库
		configDB(dbUrl.replace("entpack?", "entpackTicket?"), "ticket");

		//配置Api数据库
		configDB(env.equals("pro") ? PropKit.get("jdbcApi") : dbUrl.replace("entpack?", "entpackApi?"), "api");

		//配置Redis数据源
		String redisHost = get("redis_host");
		int redisPort = getInt("redis_port", 6379);

		RedisPlugin redisPlugin = new RedisPlugin("cache", redisHost, redisPort, 2000, null, 1);
		redisPlugin.setSerializer(MyFstSerializer.me);

		redisPlugin.start();
	}


	/**
	 * 配置数据库
	 */
	private static void configDB(String dbUrl, String configName) {
		DruidPlugin druidPlugin = new DruidPlugin(dbUrl, PropKit.get("user"), get("password"));
		druidPlugin.setInitialSize(PropKit.getInt("druid.InitialSize", 5));
		druidPlugin.setMinIdle(PropKit.getInt("druid.MinIdle", 5));
		druidPlugin.setMaxActive(PropKit.getInt("druid.MaxActive", 150));
		druidPlugin.setDriverClass("com.mysql.cj.jdbc.Driver");

		//防注入
		WallFilter wallFilter = new WallFilter();            // 加强数据库安全
		wallFilter.setDbType("mysql");
		druidPlugin.addFilter(wallFilter);

		//监控
		StatFilter statFilter = new StatFilter();
		statFilter.setLogSlowSql(PropKit.getBoolean("druid.logSlowSql", true));
		statFilter.setSlowSqlMillis(PropKit.getInt("druid.slowSqlMillis", 3 * 1000));
		statFilter.setMergeSql(true);
		druidPlugin.addFilter(statFilter);

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

		ActiveRecordPlugin arp = StringUtil.isBlank(configName) ?
				new ActiveRecordPlugin(druidPlugin) : new ActiveRecordPlugin(configName, druidPlugin);
		druidPlugin.start();
		arp.addSqlTemplate("all.sql");
		arp.setContainerFactory(new CaseInsensitiveContainerFactory());    // 配置大小写敏感
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.start();
	}

	/**
	 * 获取系统变量
	 *
	 * @param name
	 * @return
	 */
	protected static String get(String name) {
		String val = PropKit.get(name);
		if (val == null) {
			return val;
		}
		String sysPropery = String.format("${%s}", name);
		if (sysPropery.equals(val)) {
//			System.out.println(name + ":   " + System.getenv(name));
			return System.getenv(name);
		}
		return val;
	}

	protected static Integer getInt(String name, int def) {
		String val = get(name);
		if (val == null) {
			return def;
		}

		return Integer.parseInt(val);
	}
}
