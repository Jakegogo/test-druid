package org.test.test_druid.log.dtos;

import org.test.test_druid.log.BaseDataSourceLogDto;
import org.test.test_druid.log.DataSourceLogType;

/**
 * 连接异常日志
 * @author Administrator
 */
public class ConnectTimeoutLogDto extends BaseDataSourceLogDto {

	/**
	 * 堆栈信息
	 */
	private String stackTrace;
	
	/**
	 * 是否连接启动后的异常 ==1:true
	 */
	private Integer afterStartup;
	
	
	/**
	 * 创建实例
	 * @param type 日志类
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @return
	 */
	public static ConnectTimeoutLogDto valueOf(DataSourceLogType type, int enterpriseNum, String stackTrace) {
		return valueOf(type, enterpriseNum, stackTrace, 0);
	}
	
	/**
	 * 创建实例
	 * @param type 日志类
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @param afterStartup 是否启动后的连接异常 ==1:true
	 * @return
	 */
	public static ConnectTimeoutLogDto valueOf(DataSourceLogType type, int enterpriseNum, String stackTrace, int afterStartup) {
		ConnectTimeoutLogDto dto = new ConnectTimeoutLogDto();
		dto.type = type.getId();
		dto.connectId = -1;
		dto.enterpriseNum = enterpriseNum;
		dto.name = type.name();
		dto.stackTrace = stackTrace;
		dto.afterStartup = afterStartup;
		return dto;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public Integer getAfterStartup() {
		return afterStartup;
	}

	public void setAfterStartup(Integer afterStartup) {
		this.afterStartup = afterStartup;
	}

	
}
