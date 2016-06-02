package org.test.test_druid.constant;

/**
 * 数据源常量
 * @author Administrator
 */
public interface DataSourceConstants {

	/**
	 * 数据源ID的KEY
	 */
	String DataSourceID_KEY = "DataSourceID";
	
	/**
	 * 用户ID的KEY
	 */
	String UserID_KEY = "userId";

	/**
	 * 默认节流请求超时(毫秒), 一般要略大于fast fail预期超时
	 */
	int DEFAULT_FLOW_TIME_OUT = 2000;
	
	/**
	 * 默认节流释放时间间隔
	 */
	int DEFAULT_FLOW_RATE_INTERVAL = 1000;
	
	/**
	 * 默认限流警告阀值(日志输出阀值)
	 */
	int DEFAULT_FLOW_LIMIT = 800;
	
	/**
	 * 默认限流值
	 */
	int DEfAULT_UPPER_LIMIT = 2000;
	
	/**
	 * 限流 梯度缓冲时长(毫秒)
	 */
	int WARMING_UP_PERIOD = 1000;

	/**
	 * 默认超时时间,单位秒 注意不要设置太小
	 */
	int QUERY_TIMEOUT_THRESHOLD_SECOND = 15;
	
	/**
	 * 默认执行超时处理定时器线程池大小
	 */
	int QUERY_TIMEOUT_SCHEDULE_POOL_SIZE = 2;

	/**
	 * 默认单次查询获取最大记录数
	 */
	int DEFAULT_MAX_FETCH_ROW = 1000;

	/**
	 * 默认单次抓取大小
	 */
	int DEFAULT_QUERY_FETCH_SIZE = 500;
	
}
