package org.test.test_druid.log.dtos;

import java.util.List;

import org.test.test_druid.log.BaseDataSourceLogDto;
import org.test.test_druid.log.DataSourceLogType;

/**
 * 语句日志
 * @author Administrator
 */
public class StatementLogDto extends BaseDataSourceLogDto {

	/**
	 * 语句ID
	 */
	private String statementId;
	
	/**
	 * 执行时长
	 */
	private long millis;
	
	/**
	 * 数据源语句Id
	 */
	private String sqlId;
	
	/**
	 * 语句参数
	 */
	@SuppressWarnings("rawtypes")
	private List params;
	
	/**
	 * 堆栈信息
	 */
	private String stackTrace;
	
	
	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param statementId statement ID
	 * @return
	 */
	public static StatementLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum, String statementId) {
		return valueOf(type, connectId, enterpriseNum, statementId, -1);
	}
	
	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param statementId statement ID
	 * @param millis 语句执行时间(毫秒)
	 * @return
	 */
	public static StatementLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum, String statementId, long millis) {
		return valueOf(type, connectId, enterpriseNum, statementId, millis, null, null);
	}
	
	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param statementId statement ID
	 * @param millis 语句执行时间(毫秒)
	 * @param sqlId 数据源语句ID
	 * @param params 语句的参数
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static StatementLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum, String statementId, long millis, String sqlId, List params) {
		return valueOf(type, connectId, enterpriseNum, statementId, millis, sqlId, params, null);
	}
	
	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param statementId statement ID
	 * @param millis 语句执行时间(毫秒)
	 * @param sqlId 数据源语句ID
	 * @param params 语句的参数
	 * @param stackTrace 异常概要
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static StatementLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum, String statementId, long millis, String sqlId, List params, String stackTrace) {
		StatementLogDto dto = new StatementLogDto();
		dto.type = type.getId();
		dto.connectId = connectId;
		dto.enterpriseNum = enterpriseNum;
		dto.statementId = statementId;
		dto.millis = millis;
		dto.name = type.name();
		dto.sqlId = sqlId;
		dto.params = params;
		dto.stackTrace = stackTrace;
		return dto;
	}

	public String getStatementId() {
		return statementId;
	}

	public void setStatementId(String statementId) {
		this.statementId = statementId;
	}

	public long getMillis() {
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

	@SuppressWarnings("rawtypes")
	public List getParams() {
		return params;
	}

	@SuppressWarnings("rawtypes")
	public void setParams(List params) {
		this.params = params;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	
	
}
