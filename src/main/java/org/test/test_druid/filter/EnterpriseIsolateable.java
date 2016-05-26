package org.test.test_druid.filter;

/**
 * 支持企业隔离的
 * @author Administrator
 */
public interface EnterpriseIsolateable {
	
	/**
	 * 获取企业号
	 * @return
	 */
	public int getEnterpriseNum();
	
	/**
	 * 设置企业号
	 * @param enterpriseNum
	 */
	public void setEnterpriseNum(int enterpriseNum);
	
}
