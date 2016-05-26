package org.test.test_druid.firewall.limitcounter;

import java.util.concurrent.TimeUnit;

/**
 * 限流统计器
 * @author Administrator
 */
public interface LimitCounter extends Cloneable {
	
	/**
	 * 判断是否可获取令牌 (不阻塞)
	 * @param timeout 超时时间
	 * @param unit 时间单元
	 * @return
	 */
	public boolean canAcquire();
	
	/**
	 * 判断是否可获取令牌 (不阻塞)
	 * @param timeout 超时时间
	 * @param unit 时间单元
	 * @return
	 */
	public boolean canAcquire(long timeout, TimeUnit unit);
	
	/**
	 * 获取令牌(阻塞)
	 */
	public boolean acquire();
	
	/**
	 * 是否警告
	 * @return
	 */
	public boolean hasWarn();
	
	/**
	 * 获取当前计数
	 * @return
	 */
	public int getCurCount();

	/**
	 * 获取限流阀值
	 * @return
	 */
	public int getLimit();

	/**
	 * 设置限流阀值
	 * @param limit 阀值
	 */
	public void setLimit(int limit);

	/**
	 * 获取限流上限值
	 * @return
	 */
	public int getUpperLimit();

	/**
	 * 设置限流上限值
	 * @param upperLimit 上限值
	 */
	public void setUpperLimit(int upperLimit);

	/**
	 * 获取名称
	 * @return
	 */
	public String getName();

	/**
	 * 设置名称
	 * @param name
	 */
	public void setName(String name);

	/**
	 * 设置超时
	 * @param timeout 毫秒
	 */
	public void setTimeout(long timeout);

	/**
	 * 抛出超时异常
	 */
	public void rethrow();

	/**
	 * 克隆对象
	 * @return
	 */
	public LimitCounter clone() throws CloneNotSupportedException;

}
