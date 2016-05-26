package org.test.test_druid.firewall;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.test.test_druid.constant.DataSourceConstants;
import org.test.test_druid.exceptions.FlowLimitOverFlowException;

/**
 * 限流计数器
 * @author Administrator
 */
public class SimpleLimitCounter implements Cloneable, LimitCounter {

	/**
	 * 执行语句计数器
	 */
	private AtomicInteger counter = new AtomicInteger();
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
	 * 流量释放时间间隔(毫秒)
	 */
	private int resetInterval = DataSourceConstants.DEFAULT_FLOW_RATE_INTERVAL;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 上一次检测时间
	 */
	private volatile long lastCheck = System.currentTimeMillis();
	
	/**
	 * 构造方法
	 */
	public SimpleLimitCounter() {}
	
	/**
	 * 构造方法
	 * @param name 名称
	 */
	public SimpleLimitCounter(String name) {
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
	public static SimpleLimitCounter valueOf(String name, int limit, int upperLimit) {
		SimpleLimitCounter counter = new SimpleLimitCounter(name);
		counter.setLimit(limit);
		counter.setUpperLimit(upperLimit);
		return counter;
	}
	
	
	@Override
	public boolean canAcquire() {
		return canAcquire(1, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean canAcquire(long timeout, TimeUnit unit) {
		if (this.limit <= 0) {
			return true;
		}
		
		if (this.counter.get() <= this.limit) {
			return true;
		}
		if (unit.toMillis(timeout) > resetInterval) {
			return true;
		}
		return false;
	}
	
	
	@Override
	public boolean acquire() {
		if (this.limit <= 0) {
			return true;
		}
		
		if (System.currentTimeMillis() - lastCheck > resetInterval) {
			lastCheck = System.currentTimeMillis();
			this.counter.set(0);
		}
		
		int curCount = this.counter.incrementAndGet();
		if (curCount <= this.limit) {
			return true;
		}
		
		if (curCount > this.upperLimit) {// 超出最大上限,一定抛出异常
			rethrowTimeout();
		} else {
			return false;
		}
		return false;
	}
	
	/**
	 * 抛出异常
	 */
	public void rethrowTimeout() {
		throw new FlowLimitOverFlowException(this.name, this.upperLimit);
	}
	
	
	@Override
	public int getCurCount() {
		return this.counter.get();
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
	}
	
	@Override
	public boolean hasWarn() {
		return this.counter.get() > this.limit;
	}

	@Override
	public void setTimeout(long timeout) {
		// do nothing
	}

	/**
	 * 设置流量释放时间间隔 (毫秒)
	 * @param resetInterval 时间间隔(毫秒)
	 */
	public void setResetInterval(int resetInterval) {
		if (resetInterval < 50) {
			throw new IllegalArgumentException("参数不在正确范围内: [流量释放时间间隔 (毫秒)]");
		}
		this.resetInterval = resetInterval;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public int getResetInterval() {
		return resetInterval;
	}
	

	@Override
	public LimitCounter clone() throws CloneNotSupportedException {
		SimpleLimitCounter instance = (SimpleLimitCounter) super.clone();
		instance.counter = new AtomicInteger();
		instance.lastCheck = System.currentTimeMillis();
		return instance;
	}


}