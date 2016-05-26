package org.test.test_druid.log.dtos;

import org.test.test_druid.log.BaseDataSourceLogDto;
import org.test.test_druid.log.DataSourceLogType;

/**
 * 结果集行数超出范围日志
 * 
 * @author Administrator
 */
public class ResultSetOverFlowLogDto extends BaseDataSourceLogDto {

	/**
	 * 语句ID
	 */
	private String statementId;
	
	/**
	 * 数据源语句Id
	 */
	private String sqlId;

	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param statementId statement ID
	 * @param mills 执行时间
	 * @return
	 */
	public static ResultSetOverFlowLogDto valueOf(DataSourceLogType type,
			long connectId, int enterpriseNum, String statementId, String sqlId) {
		ResultSetOverFlowLogDto dto = new ResultSetOverFlowLogDto();
		dto.type = type.getId();
		dto.connectId = connectId;
		dto.enterpriseNum = enterpriseNum;
		dto.name = type.name();
		dto.statementId = statementId;
		dto.sqlId = sqlId;
		return dto;
	}

	public String getStatementId() {
		return statementId;
	}

	public void setStatementId(String statementId) {
		this.statementId = statementId;
	}

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

}
