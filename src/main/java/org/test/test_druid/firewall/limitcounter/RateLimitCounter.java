package org.test.test_druid.firewall.limitcounter;

import java.util.concurrent.TimeUnit;

import org.test.test_druid.constant.DataSourceConstants;
import org.test.test_druid.exceptions.FlowLimitTimeoutException;
import org.test.test_druid.firewall.ratelimiter.RateLimiter;

/**
 * 限流计数器 (基于guava的RateLimiter)
 * <br> 流量统计+限流
 * <br>RateLimiter可以限制执行的频度(每个acquire存在短时间间隔), 以及每秒执行总量
 * @author Administrator
 */
public class RateLimitCounter implements Cloneable, LimitCounter {

	/**
	 * 限流值 <=0 代表关闭限流
	 * <br/> 设置值必须大于1
	 */
	private int limit = DataSourceConstants.DEFAULT_FLOW_LIMIT;
	/**
	 * 最大上限
	 */
	private int upperLimit = DataSourceConstants.DEfAULT_UPPER_LIMIT;
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 限流器
	 */
	private RateLimiter rateLimiter = RateLimiter.create(upperLimit, 
			DataSourceConstants.WARMING_UP_PERIOD,
			TimeUnit.MILLISECONDS);
	
	/**
	 * 超时时间(毫秒)
	 */
	private long timeout = DataSourceConstants.DEFAULT_FLOW_TIME_OUT;
	
	/**
	 * 构造方法
	 */
	public RateLimitCounter() {}
	
	/**
	 * 构造方法
	 * @param name 名称
	 */
	public RateLimitCounter(String name) {
		this.name = name;
	}
	
	/**
	 * 获取实例
	 * <br/>(默认每秒检测一次)
	 * @param name 名称
	 * @param limit 限制(默认throwIfOverFlow=false, 超出limit记录日志)
	 * @param upperLimit 最大限制(超出upperLimit会抛出异常)
	 * @return
	 */
	public static RateLimitCounter valueOf(String name, int limit, int upperLimit) {
		RateLimitCounter counter = new RateLimitCounter(name);
		counter.setLimit(limit);
		counter.setUpperLimit(upperLimit);
		return counter;
	}
	
	@Override
	public boolean canAcquire() {
		return this.rateLimiter.canAcquire(1, timeout, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public boolean canAcquire(long timeout, TimeUnit unit) {
		return this.rateLimiter.canAcquire(1, timeout, unit);
	}
	
	
	@Override
	public boolean acquire() {
		if (this.limit <= 0) {
			return true;
		}
		
		if (this.rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
			return true;
		}
		
		return false;
	}
	
	
	@Override
	public boolean hasWarn() {
		return this.getCurCount() > this.limit;
	}
	
	@Override
	public int getCurCount() {
		return (int) this.rateLimiter.getUsedPermits();
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public void setLimit(int limit) {
		if (limit == 1) {
			// 创建连接和获取时, 会发出测试连接语句select 1, 避免引起druid不断重试的问题
			throw new IllegalArgumentException("limit值不能小于2");
		}
		this.limit = limit;
	}


	@Override
	public int getUpperLimit() {
		return upperLimit;
	}

	@Override
	public void setUpperLimit(int upperLimit) {
		if (upperLimit == 1) {
			// 创建连接和获取时, 会发出测试连接语句select 1, 避免引起druid不断重试的问题
			throw new IllegalArgumentException("upperLimit值不能小于2");
		}
		this.upperLimit = upperLimit;
		
		this.rateLimiter.setRate(upperLimit);
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public long getTimeout() {
		return timeout;
	}

	/**
	 * 设置超时
	 * @param timeout 毫秒
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * 抛出异常
	 */
	public void rethrow() {
		throw new FlowLimitTimeoutException(this.name, this.upperLimit);
	}

	
	@Override
	public LimitCounter clone() throws CloneNotSupportedException {
		RateLimitCounter instance = (RateLimitCounter) super.clone();
		instance.rateLimiter = RateLimiter.create(upperLimit, 
				DataSourceConstants.WARMING_UP_PERIOD,
				TimeUnit.MILLISECONDS);
		return instance;
	}


}