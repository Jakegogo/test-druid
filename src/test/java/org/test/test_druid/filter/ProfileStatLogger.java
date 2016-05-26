package org.test.test_druid.filter;

import com.alibaba.druid.pool.DruidDataSourceStatLogger;
import com.alibaba.druid.pool.DruidDataSourceStatLoggerAdapter;
import com.alibaba.druid.pool.DruidDataSourceStatValue;

/**
 * 自定义性能日志输出器
 * @author Administrator
 */
public class ProfileStatLogger extends DruidDataSourceStatLoggerAdapter implements
		DruidDataSourceStatLogger {
	public void log(DruidDataSourceStatValue statValue) {
		// 自定义输出
	}
}