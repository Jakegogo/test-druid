package org.test.test_druid.firewall;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.test_druid.constant.DataSourceConstants;
import org.test.test_druid.firewall.limitcounter.FlowLimitTable;
import org.test.test_druid.firewall.limitcounter.LimitCounterBuilder;
import org.test.test_druid.log.DataSourceLogType;
import org.test.test_druid.log.dtos.FlowLimitLogDto;
import org.test.test_druid.utils.JsonUtils;

/**
 * 简单限流过滤器
 * @author Administrator
 */
public class SimpleFlowLimitFilter implements FlowLimitFilter {
	
	private Logger logger = LoggerFactory.getLogger("druid.sql.CustomLogger");
	
	/**
	 * 企业限流计数器
	 */
	private FlowLimitTable enterpriseCounter = LimitCounterBuilder.createRateLimiter("企业限流").buildTable();

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
	 * 流量编码生成器
	 */
	private final AtomicLong snGenerator = new AtomicLong();
	

	@Override
	public void acquireToken(Object userId, int enterpriseNum,
			Object dataSourceId) {
		
		Long sn = this.generateSn();
		
		// 1.用户限流统计
		if (!userCounter.acquire(userId)) {
			logger(FlowLimitLogDto.valueOf(DataSourceLogType.Limit_Overflow,
					-1, enterpriseNum, userId, null,
					userCounter.getCurCount(userId), userCounter.getUpperLimit(userId),
					userCounter.getName()).setExtra(sn));
			userCounter.rethrow(userId);
		} else if (userCounter.hasWarn(userId)) {// 输出警告
			logger(FlowLimitLogDto.valueOf(DataSourceLogType.Limit_Warn,
					-1, enterpriseNum, userId, null,
					userCounter.getCurCount(userId), userCounter.getLimit(userId),
					userCounter.getName()).setExtra(sn));
		}
		

		// 2.数据源限流统计
		if (dataSourceId != null) {
			if (!dataSourceCounter.acquire(dataSourceId)) {
				logger(FlowLimitLogDto.valueOf(
						DataSourceLogType.Limit_Overflow, -1, enterpriseNum,
								dataSourceId.toString(), dataSourceCounter
								.getCurCount(dataSourceId), dataSourceCounter
								.getUpperLimit(dataSourceId), dataSourceCounter
								.getName()).setExtra(sn));
				dataSourceCounter.rethrow(dataSourceId);
			} else if (dataSourceCounter.hasWarn(dataSourceId)) {// 输出警告
				logger(FlowLimitLogDto.valueOf(
						DataSourceLogType.Limit_Warn, -1, enterpriseNum,
								dataSourceId.toString(), dataSourceCounter
								.getCurCount(dataSourceId), dataSourceCounter
								.getLimit(dataSourceId), dataSourceCounter
								.getName()).setExtra(sn));
			}
		}

		// 3.企业限流统计
		if (!enterpriseCounter.acquire(enterpriseNum)) {
			logger(FlowLimitLogDto.valueOf(DataSourceLogType.Limit_Overflow,
					-1, enterpriseNum,
					enterpriseCounter.getCurCount(enterpriseNum),
					enterpriseCounter.getUpperLimit(enterpriseNum), enterpriseCounter.getName())
					.setExtra(sn));
			enterpriseCounter.rethrow(enterpriseNum);
		} else if (enterpriseCounter.hasWarn(enterpriseNum)) {// 输出警告
			logger(FlowLimitLogDto.valueOf(DataSourceLogType.Limit_Warn,
					-1, enterpriseNum,
					enterpriseCounter.getCurCount(enterpriseNum),
					enterpriseCounter.getLimit(enterpriseNum), enterpriseCounter.getName())
					.setExtra(sn));
		}
		
		
	}

	// 生成流量编码
	private long generateSn() {
		long sn = snGenerator.incrementAndGet();
		if (sn < 0 || sn >= Long.MAX_VALUE) {
			snGenerator.set(0);
			sn = snGenerator.incrementAndGet();
		}
		return sn;
	}
	
	// 转换成LOG字符串
	private void logger(Object object) {
		logger.info(JsonUtils.object2JsonString(object));
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

	public FlowLimitTable getEnterpriseCounter() {
		return enterpriseCounter;
	}

	public void setEnterpriseCounter(FlowLimitTable enterpriseCounter) {
		this.enterpriseCounter = enterpriseCounter;
	}

	public FlowLimitTable getDataSourceCounter() {
		return dataSourceCounter;
	}

	public void setDataSourceCounter(FlowLimitTable dataSourceCounter) {
		this.dataSourceCounter = dataSourceCounter;
	}

	public FlowLimitTable getUserCounter() {
		return userCounter;
	}

	public void setUserCounter(FlowLimitTable userCounter) {
		this.userCounter = userCounter;
	}

}
