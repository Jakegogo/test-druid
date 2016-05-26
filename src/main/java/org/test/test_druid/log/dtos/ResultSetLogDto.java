package org.test.test_druid.log.dtos;

import org.test.test_druid.log.BaseDataSourceLogDto;
import org.test.test_druid.log.DataSourceLogType;

/**
 * 结果集日志DTO
 * @author Administrator
 */
public class ResultSetLogDto extends BaseDataSourceLogDto {
	
	/**
	 * 语句ID
	 */
	private String statementId;
	
	/**
	 * 读取的行数量
	 */
	private long rowCount;
	
	/**
	 * 列数
	 */
	private long cloumnCount;
	
	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param statementId statement ID
	 * @return
	 */
	public static ResultSetLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum, String statementId) {
		return valueOf(type, connectId, enterpriseNum, statementId, -1, -1);
	}
	
	/**
	 * 创建实例
	 * @param type 日志类型
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param statementId statement ID
	 * @param rowCount 读取行数
	 * @param cloumnCount 查询列数
	 * @return
	 */
	public static ResultSetLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum, String statementId, long rowCount, long cloumnCount) {
		ResultSetLogDto dto = new ResultSetLogDto();
		dto.type = type.getId();
		dto.connectId = connectId;
		dto.enterpriseNum = enterpriseNum;
		dto.statementId = statementId;
		dto.rowCount = rowCount;
		dto.cloumnCount = cloumnCount;
		dto.name = type.name();
		return dto;
	}

	public String getStatementId() {
		return statementId;
	}

	public void setStatementId(String statementId) {
		this.statementId = statementId;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public long getCloumnCount() {
		return cloumnCount;
	}

	public void setCloumnCount(long cloumnCount) {
		this.cloumnCount = cloumnCount;
	}
	
}
