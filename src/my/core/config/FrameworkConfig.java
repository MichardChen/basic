package my.core.config;

import org.huadalink.config.FrameworkConstants;
import org.huadalink.handler.RenderingTimeHandler;
import org.huadalink.handler.SessionIdHandler;
import org.huadalink.log.Slf4jLogFactory;
import org.huadalink.plugin.shiro.ShiroPlugin;
import org.huadalink.plugin.tablebind.AutoTableBindPlugin;
import org.huadalink.route.AutoBindRoutes;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.huadalink.quartz.QuartzPlugin;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.tx.TxByRegex;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;

public class FrameworkConfig extends JFinalConfig {

	private static Prop fprops = PropKit.use(FrameworkConstants.FRAMEWORK_PROPS);
	private static Prop aprops = null;
	Routes routes;

	static {
		try {
			aprops = PropKit.use(FrameworkConstants.APPLICATION_PROPS);
		} catch (Exception e) {
			System.err.println(FrameworkConstants.APPLICATION_PROPS + " was not found!");
		}
	}

	/**
	 * 读取属性文件中的配置，优先获取应用的配置
	 * 
	 * @param key
	 * @return
	 */
	private String seekProp(String key) {
		if (aprops.containsKey(key)) {
			return aprops.get(key);
		} else {
			return fprops.get(key);
		}
	}

	/**
	 * 读取属性文件中的配置，优先获取应用的配置
	 * 
	 * @param key
	 * @return
	 */
	private Boolean seekPropToBoolean(String key) {
		if (aprops.containsKey(key)) {
			return aprops.getBoolean(key);
		} else {
			return fprops.getBoolean(key);
		}
	}

	/**
	 * 常量配置
	 */
	@Override
	public void configConstant(Constants me) {
		//me.setEncoding("UTF-8");
		me.setDevMode(seekPropToBoolean(FrameworkConstants.CFG_DEVMODE));
		Logger.setLoggerFactory(new Slf4jLogFactory());
		me.setViewType(ViewType.JSP);
		me.setEncoding(FrameworkConstants.DEFAULT_ENCODING);
		me.setError404View(seekProp(FrameworkConstants.CFG_ERROR404VIEW));
		me.setError500View(seekProp(FrameworkConstants.CFG_ERROR500VIEW));
		
		
	}

	/**
	 * 路由配置
	 */
	@Override
	public void configRoute(Routes me) {
		this.routes = me;
		AutoBindRoutes routeBind = new AutoBindRoutes().autoScan(false);
		me.add(routeBind);
	}

	/**
	 * 插件配置
	 */
	@Override
	public void configPlugin(Plugins me) {
		String appDsList = seekProp(FrameworkConstants.CFG_DSLIST);
		if (appDsList != null && !appDsList.equals("")) {
			String[] dsList = appDsList.split(",");
			for (int i = 0; i < dsList.length; i++) {
				String ds = dsList[i];
				String jdbcUrl = seekProp(FrameworkConstants.CFG_JDBCURL.replace("###", ds));
				String username = seekProp(FrameworkConstants.CFG_USERNAME.replace("###", ds));
				String password = seekProp(FrameworkConstants.CFG_PASSWORD.replace("###", ds));
				String driverClass = seekProp(FrameworkConstants.CFG_DRIVERCLASS.replace("###", ds));
				String poolInitialSize = seekProp(FrameworkConstants.CFG_POOLINITIALSIZE.replace("###", ds));
				String poolMaxSize = seekProp(FrameworkConstants.CFG_POOLMAXSIZE.replace("###", ds));
				String poolMinSize = seekProp(FrameworkConstants.CFG_POOLMINSIZE.replace("###", ds));
				String connectionTimeoutMillis = seekProp(FrameworkConstants.CFG_CONNECTIONTIMEOUTMILLIS.replace("###", ds));
				//DruidPlugin默认driverClass = "com.mysql.jdbc.Driver",连接池插件
				DruidPlugin dp = new DruidPlugin(jdbcUrl, username, password, driverClass);
				//初始化时获取连接数
				if (poolInitialSize != null) {
					dp.setInitialSize(Integer.valueOf(poolInitialSize));
				}
				//连接池中保留的最大连接数
				if (poolMaxSize != null) {
					dp.setMaxActive(Integer.valueOf(poolMaxSize));
				}
				//连接池中保留的最小连接数
				if (poolMinSize != null) {
					dp.setMinIdle(Integer.valueOf(poolMinSize));
				}
				//设置连接的超时时间
				if (connectionTimeoutMillis != null) {
					dp.setTimeBetweenConnectErrorMillis(Integer.valueOf(connectionTimeoutMillis));
				}
				WallFilter wf = new WallFilter();// 防SQL注入
				StatFilter sf = new StatFilter();// 监控统计
				dp.addFilter(wf);
				dp.addFilter(sf);
				me.add(dp);

				//自动绑定表插件，AutoTableBindPlugin继承自ActiveRecordPlugin,所以不需要单独注册它了
				AutoTableBindPlugin atbp = null;
				if (i == 0) {
					atbp = new AutoTableBindPlugin(dp).autoScan(false);
				} else {
					atbp = new AutoTableBindPlugin(ds, dp).autoScan(false);
				}
				atbp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
				atbp.setShowSql(seekPropToBoolean(FrameworkConstants.CFG_DEVMODE));
				atbp.setDialect(new MysqlDialect());
				me.add(atbp);

				Logger.getLogger(FrameworkConfig.class).debug("Load datasource [" + ds + ":" + jdbcUrl + "] success!");

			}
		}
		//添加shiro的过滤器
		me.add(new ShiroPlugin(this.routes));
		//添加缓存
		me.add(new EhCachePlugin());
		
		
		//定时任务
		me.add(new QuartzPlugin());
		
		
	  
	}

	/**
	 * 拦截器配置
	 */
	@Override
	public void configInterceptor(Interceptors me) {
		// 业务层事务控制  TxByRegex拦截器可通过传入正则表达式对action进行拦截
		me.addGlobalServiceInterceptor(new TxByRegex(".*insert.*|.*add.*|.*save.*|.*update.*|.*delete.*"));
		me.add(new TxByRegex(".*save.*"));
		me.add(new TxByRegex(".*delete.*"));
		me.add(new TxByRegex(".*change.*"));
		me.add(new TxByRegex(".*load.*"));
		me.add(new TxByRegex(".*setUp.*"));
		me.add(new TxByRegex(".*assign.*"));
		me.add(new TxByRegex(".*submit.*"));
		me.add(new TxByRegex(".*alert.*"));
	}

	/**
	 * 处理器配置
	 */
	@Override
	public void configHandler(Handlers handlers) {
		if (seekPropToBoolean(FrameworkConstants.CFG_RENDERtIMESHOW)) {
			handlers.add(new RenderingTimeHandler());
		}

		handlers.add(new ContextPathHandler());
		handlers.add(new SessionIdHandler());
	}

	public void afterJFinalStart() {
	};

	public void beforeJFinalStop() {
	};

}
