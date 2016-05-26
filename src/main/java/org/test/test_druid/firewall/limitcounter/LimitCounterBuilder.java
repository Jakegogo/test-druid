package org.test.test_druid.firewall.limitcounter;

/**
 * 限流构建器
 * @author Administrator
 */
public class LimitCounterBuilder {
	
	/**
	 * LimitCounter实例
	 */
	private LimitCounter limitCounter;
	
	
	/**
	 * 创建简单限流器
	 * <br>创建一个非阻塞的, 基于计数器的简单限流器
	 * @param name 名称
	 * @return
	 */
	public static LimitCounterBuilder createSimpleLimiter(String name) {
		LimitCounterBuilder builder = new LimitCounterBuilder();
		builder.limitCounter = new SimpleLimitCounter(name);
		return builder;
	}
	
	
	/**
	 * 创建Guava RateLimiter限流器
	 * <br>Guava RateLimiter限流采用漏桶算法,具有平缓的输出速度
	 * <br>可以用于防止瞬间突发高并发流量
	 * @param name 名称
	 * @return
	 */
	public static LimitCounterBuilder createRateLimiter(String name) {
		LimitCounterBuilder builder = new LimitCounterBuilder();
		builder.limitCounter = new RateLimitCounter(name);
		return builder;
	}
	
	
	
	/**
	 * 设置限流警告值
	 * @param limit 警告值
	 * @return
	 */
	public LimitCounterBuilder setLimit(int limit) {
		this.limitCounter.setLimit(limit);
		return this;
	}
	
	
	/**
	 * 设置限流值
	 * @param upperLimit 限流值
	 * @return
	 */
	public LimitCounterBuilder setUpperLimit(int upperLimit) {
		this.limitCounter.setUpperLimit(upperLimit);
		return this;
	}
	
	
	/**
	 * 构建LimitCounter
	 * @return
	 */
	public LimitCounter build() {
		return this.limitCounter;
	}
	
	
	/**
	 * 构建FlowLimitTable
	 * @return
	 */
	public FlowLimitTable buildTable() {
		return new FlowLimitTable(this.limitCounter.getName(), this.limitCounter);
	}
	
}
