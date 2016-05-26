package org.test.test_druid.log.dtos;

import org.test.test_druid.log.BaseDataSourceLogDto;
import org.test.test_druid.log.DataSourceLogType;

/**
 * 数据源日志DTO
 * @author Administrator
 */
public class DataSourceLogDto extends BaseDataSourceLogDto {
	
	/**
	 * 创建时间
	 */
	private long last;
	
	
	/**
	 * 创建实例
	 * @param type 日志类
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param last 连接创建时间
	 * @return
	 */
	public static DataSourceLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum, long last) {
		DataSourceLogDto dto = new DataSourceLogDto();
		dto.type = type.getId();
		dto.connectId = connectId;
		dto.enterpriseNum = enterpriseNum;
		dto.last = last;
		dto.name = type.name();
		return dto;
	}


	public long getLast() {
		return last;
	}


	public void setLast(long last) {
		this.last = last;
	}


	
}
