package org.test.test_druid.firewall;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.test.test_druid.exceptions.FlowLimitOverFlowException;

/**
 * 限流计数表
 * @author Administrator
 */
public class FlowLimitTable {

	/**
	 * 限流计数Map
	 */
	private ConcurrentMap<Object, LimitCounter> counterMap = new ConcurrentHashMap<Object, LimitCounter>();

	/**
	 * 限流实例
	 */
	private LimitCounter counterInstance = new SimpleLimitCounter("默认");

	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 构造方法
	 * @param name 
	 */
	public FlowLimitTable(String name) {
		this.name = name;
	}

	/**
	 * 构造方法
	 * 
	 * @param counterInstance  LimitCounter 限流实例
	 */
	public FlowLimitTable(String name, LimitCounter counterInstance) {
		this.name = name;
		this.counterInstance = counterInstance;
	}
	
	
	/**
	 * 判断是否可获取令牌 (不阻塞)
	 * @param timeout 超时时间
	 * @param unit 时间单元
	 * @return
	 */
	public boolean canAcquire(Object key) {
		return getLimitCounter(key).canAcquire();
	}
	
	/**
	 * 判断是否可获取令牌 (不阻塞)
	 * @param timeout 超时时间
	 * @param unit 时间单元
	 * @return
	 */
	public boolean canAcquire(Object key, long timeout, TimeUnit unit) {
		return getLimitCounter(key).canAcquire(timeout, unit);
	}

	
	/**
	 * 获取令牌(阻塞)
	 * @param key 键
	 * @return
	 */
	public boolean acquire(Object key) {
		return getLimitCounter(key).acquire();
	}
	
	
	/**
	 * 是否警告
	 * @param key 键
	 * @return
	 */
	public boolean hasWarn(Object key) {
		LimitCounter counter = counterMap.get(key);
		if (counter == null) {
			return false;
		}
		return counter.getCurCount() > counter.getLimit();
	}
	
	/**
	 * 获取当前计数
	 * @param key 键
	 * @return
	 */
	public int getCurCount(Object key) {
		LimitCounter counter = counterMap.get(key);
		if (counter == null) {
			return 0;
		}
		return counter.getCurCount();
	}

	/**
	 * 获取当前限流阀值
	 * @param key 键
	 * @return
	 */
	public int getLimit(Object key) {
		LimitCounter counter = counterMap.get(key);
		if (counter == null) {
			return this.counterInstance.getLimit();
		}
		return counter.getLimit();
	}
	
	/**
	 * 获取当前限流上限值
	 * @param key 键
	 * @return
	 */
	public int getUpperLimit(Object key) {
		LimitCounter counter = counterMap.get(key);
		if (counter == null) {
			return this.counterInstance.getUpperLimit();
		}
		return counter.getUpperLimit();
	}
	

	/**
	 * 设置限流值
	 * @param key 键
	 * @param limit 警告值
	 * @param upperLimit 限流值
	 */
	public FlowLimitTable setLimitVal(Object key, int limit, int upperLimit) {
		LimitCounter counter = getLimitCounter(key);
		
		counter.setLimit(limit);
		counter.setUpperLimit(upperLimit);
		return this;
	}

	// 根据Key获取LimitCounter
	private LimitCounter getLimitCounter(Object key) {
		LimitCounter counter = counterMap.get(key);

		if (counter == null) {
			try {
				counter = (LimitCounter) counterInstance.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException("初始化节流器异常", e);
			}
			counter.setName(name + ":[" + key + "]");
			
			LimitCounter pre = counterMap.putIfAbsent(key, counter);
			counter = pre != null ? pre : counter;
		}
		return counter;
	}
	

	public String getName() {
		return name;
	}

	/**
	 * 抛出异常
	 * @param key 键
	 */
	public void rethrow(Object key) {
		throw new FlowLimitOverFlowException(this.name, getUpperLimit(key));
	}

	/**
	 * 设置超时
	 * @param timeout 毫秒
	 */
	public void setTimeout(int timeout) {
		for (LimitCounter counter : counterMap.values()) {
			counter.setTimeout(timeout);
		}
	}
	

}
