package org.test.test_druid.firewall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.test_druid.constant.DataSourceConstants;
import org.test.test_druid.filter.EnterpriseIsolateable;
import org.test.test_druid.firewall.limitcounter.FlowLimitTable;
import org.test.test_druid.firewall.limitcounter.LimitCounter;
import org.test.test_druid.firewall.limitcounter.LimitCounterBuilder;
import org.test.test_druid.firewall.limitcounter.RateLimitCounter;
import org.test.test_druid.log.DataSourceLogType;
import org.test.test_druid.log.dtos.FlowLimitLogDto;
import org.test.test_druid.utils.JsonUtils;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

/**
 * 流量限制过滤器
 * @author Administrator
 */
public class FlowLimitDruidFilter extends FilterEventAdapter implements
		EnterpriseIsolateable {

	private Logger logger = LoggerFactory.getLogger("druid.sql.CustomLogger");

	@Override
	protected void statementExecuteUpdateBefore(StatementProxy statement,
			String sql) {
		acquireToken(statement);
	}

	@Override
	protected void statementExecuteQueryBefore(StatementProxy statement,
			String sql) {
		acquireToken(statement);
	}

	@Override
	protected void statementExecuteBefore(StatementProxy statement, String sql) {
		acquireToken(statement);
	}

	@Override
	protected void statementExecuteBatchBefore(StatementProxy statement) {
		acquireToken(statement);
	}

	/**
	 * 增加计数
	 */
	private void acquireToken(StatementProxy statement) {
		
		Object userId = statement.getAttribute(DataSourceConstants.UserID_KEY);
		
		// 1.用户限流统计
		if (!userCounter.acquire(userId)) {
			logger(FlowLimitLogDto.valueOf(DataSourceLogType.Limit_Overflow,
					statement.getConnectionProxy().getId(), enterpriseNum, userId, null,
					userCounter.getCurCount(userId), userCounter.getUpperLimit(userId),
					userCounter.getName()));
			userCounter.rethrow(userId);
		} else if (userCounter.hasWarn(userId)) {// 输出警告
			logger(FlowLimitLogDto.valueOf(DataSourceLogType.Limit_Warn,
					statement.getConnectionProxy().getId(), enterpriseNum, userId, null,
					userCounter.getCurCount(userId), userCounter.getLimit(userId),
					userCounter.getName()));
		}
		

		// 2.数据源限流统计
		Object statementId = statement.getAttribute(DataSourceConstants.DataSourceID_KEY);
		if (statementId != null) {
			if (!dataSourceCounter.acquire(statementId)) {
				logger(FlowLimitLogDto.valueOf(
						DataSourceLogType.Limit_Overflow, statement
								.getConnectionProxy().getId(), enterpriseNum,
						statementId.toString(), dataSourceCounter
								.getCurCount(statementId), dataSourceCounter
								.getUpperLimit(statementId), dataSourceCounter
								.getName()));
				dataSourceCounter.rethrow(statementId);
			} else if (dataSourceCounter.hasWarn(statementId)) {// 输出警告
				logger(FlowLimitLogDto.valueOf(
						DataSourceLogType.Limit_Warn, statement
								.getConnectionProxy().getId(), enterpriseNum,
						statementId.toString(), dataSourceCounter
								.getCurCount(statementId), dataSourceCounter
								.getLimit(statementId), dataSourceCounter
								.getName()));
			}
		}

		// 3.企业限流统计
		if (!enterpriseCounter.acquire()) {
			logger(FlowLimitLogDto.valueOf(DataSourceLogType.Limit_Overflow,
					statement.getConnectionProxy().getId(), enterpriseNum,
					enterpriseCounter.getCurCount(),
					enterpriseCounter.getUpperLimit(), enterpriseCounter.getName()));
			enterpriseCounter.rethrow();
		} else if (enterpriseCounter.hasWarn()) {// 输出警告
			logger(FlowLimitLogDto.valueOf(DataSourceLogType.Limit_Warn,
					statement.getConnectionProxy().getId(), enterpriseNum,
					enterpriseCounter.getCurCount(),
					enterpriseCounter.getLimit(), enterpriseCounter.getName()));
		}

	}
	
	// 转换成LOG字符串
	private void logger(Object object) {
		logger.info(JsonUtils.object2JsonString(object));
	}
	

	/**
	 * 企业限流计数器
	 */
	private LimitCounter enterpriseCounter = LimitCounterBuilder.createRateLimiter("企业限流").build();

	/**
	 * 数据源ID限流
	 */
	private FlowLimitTable dataSourceCounter = LimitCounterBuilder.createRateLimiter("数据源ID限流").buildTable();

	/**
	 * 用户限流
	 */
	private FlowLimitTable userCounter = LimitCounterBuilder.createRateLimiter("用户限流").buildTable();

	/**
	 * 请求超时(毫秒)
	 */
	private int requestTimeout = DataSourceConstants.DEFAULT_FLOW_TIME_OUT;

	/**
	 * 企业号
	 */
	protected int enterpriseNum;
	
	
	public FlowLimitDruidFilter() {
		this.setRequestTimeout(this.requestTimeout);
	}
	

	public int getEnterpriseNum() {
		return enterpriseNum;
	}
	
	public void setEnterpriseNum(int enterpriseNum) {
		this.enterpriseNum = enterpriseNum;
	}

	public int getRequestTimeout() {
		return requestTimeout;
	}

	/**
	 * 设置超时并且设置 FlowLimitCounter的timeout
	 * 
	 * @param requestTimeout
	 */
	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
		this.enterpriseCounter.setTimeout(this.requestTimeout);
		this.userCounter.setTimeout(this.requestTimeout);
		this.dataSourceCounter.setTimeout(this.requestTimeout);
	}


	public LimitCounter getEnterpriseCounter() {
		return enterpriseCounter;
	}

	public void setEnterpriseCounter(RateLimitCounter enterpriseCounter) {
		this.enterpriseCounter = enterpriseCounter;
	}

	protected FlowLimitTable getDataSourceCounter() {
		return dataSourceCounter;
	}

	protected void setDataSourceCounter(FlowLimitTable dataSourceCounter) {
		this.dataSourceCounter = dataSourceCounter;
	}
	
}
