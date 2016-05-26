package org.test.test_druid.filter;

import java.sql.SQLException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.test_druid.constant.DataSourceConstants;
import org.test.test_druid.log.DataSourceLogType;
import org.test.test_druid.log.dtos.StatementTimeoutLogDto;
import org.test.test_druid.utils.JsonUtils;
import org.test.test_druid.utils.StatementUtils;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

/**
 * 设置SQL执行时间 如果超时,查询被cancel,并抛出SQLTimeoutException异常
 * <br/> 建议同时设置 socket timeout或connect timeout
 * <br/> socket timeout > connect timeout > query timeout
 */
public class SqlTimeOutFilter extends FilterEventAdapter implements EnterpriseIsolateable {
	
	private Logger logger = LoggerFactory.getLogger("druid.sql.CustomLogger");
	
	/**
	 * 超时时间,默认为QUERY_TIMEOUT_THRESHOLD_SECOND
	 */
	private int timeoutThreshold = DataSourceConstants.QUERY_TIMEOUT_THRESHOLD_SECOND;

	
	/**
	 * 超时处理定时器线程池
	 */
	private ScheduledThreadPoolExecutor scheduledService;
	
	/**
	 * 定时器线程池大小
	 */
	private int schedulePoolSize = DataSourceConstants.QUERY_TIMEOUT_SCHEDULE_POOL_SIZE;
	
	
	public SqlTimeOutFilter() {
//		this.initExecutor();
	}

	
	/**
	 * 初始化超时处理线程池大小
	 */
	@PostConstruct
	private void initExecutor() {
		if (schedulePoolSize <= 0) {
			throw new IllegalArgumentException("jdbc超时限制处理线程池初始化异常:[schedulePoolSize=" + schedulePoolSize + "]");
		}
		ScheduledThreadPoolExecutor scheduledService = new ScheduledThreadPoolExecutor(schedulePoolSize);
		this.scheduledService = scheduledService;
	}


	@Override
	protected void statementExecuteBefore(StatementProxy statement, String sql) {
		setQueryTimeout(statement);
		super.statementExecuteBefore(statement, sql);
	}

	@Override
	protected void statementExecuteBatchBefore(StatementProxy statement) {
		setQueryTimeout(statement);
		super.statementExecuteBatchBefore(statement);
	}

	@Override
	protected void statementExecuteUpdateBefore(StatementProxy statement,
			String sql) {
		setQueryTimeout(statement);
		super.statementExecuteUpdateBefore(statement, sql);
	}

	@Override
	protected void statementExecuteQueryBefore(StatementProxy statement,
			String sql) {
		setQueryTimeout(statement);
		super.statementExecuteQueryBefore(statement, sql);
	}

	/**
	 * 设置Statement超时时间,
	 * statement.setQueryTimeout单位是秒,0表示没有限制.这个函数可能会抛出SQLException异常,场景:
	 * 1.数据库访问错误 
	 * 2.在一个已经关闭的Statement上调用这个方法 
	 * 3.超时时间不满足seconds >= 0的条件
	 *
	 * @param statement
	 */
	private void setQueryTimeout(final StatementProxy statement) {
//		由于jdbc的超时机制, 采用new Thread()方式, 非常占用线程资源
//		try {
//			statement.setQueryTimeout(timeoutThreshold);
//		} catch (SQLException se) {
////			se.printStackTrace();
//			// ignore
//		}
		
		// 使用定时器取消任务
		scheduledService.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					if (!statement.isClosed()) {// TODO 测试statement是否会重用,造成新的statement无故取消的情况
						statement.cancel();
						
						// 输出日志
						logger(StatementTimeoutLogDto
								.valueOf(
										DataSourceLogType.StatementTimeout_Exception,
										statement.getConnectionProxy().getId(),
										enterpriseNum,
										StatementUtils.stmtId(statement),
										StatementUtils.sqlId(statement,
												statement.getLastExecuteSql()),
										timeoutThreshold * 1000,
										StatementUtils.getParameters(statement)));
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}, timeoutThreshold, TimeUnit.SECONDS);
	}

	public int getTimeoutThreshold() {
		return timeoutThreshold;
	}

	public void setTimeoutThreshold(int timeoutThreshold) {
		this.timeoutThreshold = timeoutThreshold;
	}
	
	public int getSchedulePoolSize() {
		return schedulePoolSize;
	}


	public void setSchedulePoolSize(int schedulePoolSize) {
		this.schedulePoolSize = schedulePoolSize;
	}


	// 转换成LOG字符串
	private void logger(Object object) {
		logger.info(JsonUtils.object2JsonString(object));
	}
	
	/**
	 * 企业号
	 */
	protected int enterpriseNum;
	
	public int getEnterpriseNum() {
		return enterpriseNum;
	}

	public void setEnterpriseNum(int enterpriseNum) {
		this.enterpriseNum = enterpriseNum;
	}
	
}