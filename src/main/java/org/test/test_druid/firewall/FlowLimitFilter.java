package org.test.test_druid.firewall;


/**
 * 限流过滤器
 * @author Administrator
 */
public interface FlowLimitFilter {
	
	/**
	 * 获取令牌
	 * @param userId 用户Id
	 * @param enterpriseNum 企业Id
	 * @param dataSourceId 数据源Id
	 */
	void acquireToken(Object userId, int enterpriseNum, Object dataSourceId);
	
}
