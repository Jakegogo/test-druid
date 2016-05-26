package org.test.test_druid.log.dtos;

import org.test.test_druid.log.BaseDataSourceLogDto;
import org.test.test_druid.log.DataSourceLogType;

/**
 * 连接日志
 * @author Administrator
 */
public class ConnectionLogDto extends BaseDataSourceLogDto {


	/**
	 * 创建实例
	 * @param type 日志类
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @return
	 */
	public static ConnectionLogDto valueOf(DataSourceLogType type, long connectId, int enterpriseNum) {
		ConnectionLogDto dto = new ConnectionLogDto();
		dto.type = type.getId();
		dto.connectId = connectId;
		dto.enterpriseNum = enterpriseNum;
		dto.name = type.name();
		return dto;
	}

	
}
