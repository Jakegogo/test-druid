package org.test.test_druid.log.dtos;

import org.test.test_druid.log.BaseDataSourceLogDto;
import org.test.test_druid.log.DataSourceLogType;

/**
 * 限流日志
 * @author Administrator
 */
public class FlowLimitLogDto extends BaseDataSourceLogDto {
	
	/**
	 * 语句ID
	 */
	private String statementId;
	
	/**
	 * 当前流量
	 */
	private int curCount;
	
	/**
	 * 流量限制
	 */
	private int limit;
	
	/**
	 * 限流名称
	 */
	private String limitName;
	
	/**
	 * 用户Id
	 */
	private Object userId;
	
	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param curCount 当前流量
	 * @param limit 流量限制值(阀值/异常值)
	 * @param limitName 限流器名称
	 * @return
	 */
	public static FlowLimitLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum, int curCount, int limit, String limitName) {
		return valueOf(type, connectId, enterpriseNum, null, curCount, limit, limitName);
	}
	
	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param statementId statement ID
	 * @param curCount 当前流量
	 * @param limit 流量限制值(阀值/异常值)
	 * @param limitName 限流器名称
	 * @return
	 */
	public static FlowLimitLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum, String statementId, int curCount, int limit, String limitName) {
		return valueOf(type, connectId, enterpriseNum, null, statementId, curCount, limit, limitName);
	}
	
	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param userId 用户ID
	 * @param statementId statement ID
	 * @param curCount 当前流量
	 * @param limit 流量限制值(阀值/异常值)
	 * @param limitName 限流器名称
	 * @return
	 */
	public static FlowLimitLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum, Object userId,  String statementId, int curCount, int limit, String limitName) {
		FlowLimitLogDto dto = new FlowLimitLogDto();
		dto.type = type.getId();
		dto.connectId = connectId;
		dto.enterpriseNum = enterpriseNum;
		dto.name = type.name();
		dto.curCount = curCount;
		dto.limit = limit;
		dto.limitName = limitName;
		dto.statementId = statementId;
		return dto;
	}

	public String getStatementId() {
		return statementId;
	}

	public void setStatementId(String statementId) {
		this.statementId = statementId;
	}

	public int getCurCount() {
		return curCount;
	}

	public void setCurCount(int curCount) {
		this.curCount = curCount;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getLimitName() {
		return limitName;
	}

	public void setLimitName(String limitName) {
		this.limitName = limitName;
	}

	public Object getUserId() {
		return userId;
	}

	public void setUserId(Object userId) {
		this.userId = userId;
	}

	
	
}
