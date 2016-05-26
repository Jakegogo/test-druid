package org.test.test_druid.log.dtos;

import java.util.List;

import org.test.test_druid.log.BaseDataSourceLogDto;
import org.test.test_druid.log.DataSourceLogType;

/**
 * 语句超时日志
 * 
 * @author Administrator
 */
public class StatementTimeoutLogDto extends BaseDataSourceLogDto {

	/**
	 * 语句ID
	 */
	private String statementId;
	
	/**
	 * 数据源语句Id
	 */
	private String sqlId;

	/**
	 * 执行时间
	 */
	private long mills;
	
	/**
	 * 语句参数
	 */
	@SuppressWarnings("rawtypes")
	private List params;

	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param statementId statement ID
	 * @param mills 执行时间
	 * @return
	 */
	public static StatementTimeoutLogDto valueOf(DataSourceLogType type,
			long connectId, int enterpriseNum, String statementId, String sqlId, long mills, @SuppressWarnings("rawtypes") List params) {
		StatementTimeoutLogDto dto = new StatementTimeoutLogDto();
		dto.type = type.getId();
		dto.connectId = connectId;
		dto.enterpriseNum = enterpriseNum;
		dto.name = type.name();
		dto.statementId = statementId;
		dto.mills = mills;
		dto.sqlId = sqlId;
		dto.params = params;
		return dto;
	}

	public String getStatementId() {
		return statementId;
	}

	public void setStatementId(String statementId) {
		this.statementId = statementId;
	}

	public long getMills() {
		return mills;
	}

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

	public void setMills(long mills) {
		this.mills = mills;
	}

	@SuppressWarnings("rawtypes")
	public List getParams() {
		return params;
	}

	@SuppressWarnings("rawtypes")
	public void setParams(List params) {
		this.params = params;
	}

}
