package org.test.test_druid.log;

import org.test.test_druid.log.dtos.ConnectionLogDto;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 数据源基础日志DTO
 * 
 * @author Administrator
 */
public class BaseDataSourceLogDto {

	/**
	 * 类型编号
	 */
	@JSONField(ordinal = -5)
	protected int type;
	/**
	 * 连接ID
	 */
	@JSONField(ordinal = -4)
	protected long connectId;
	/**
	 * 企业e号
	 */
	@JSONField(ordinal = -3)
	protected int enterpriseNum;
	/**
	 * 生成的时间戳
	 */
	@JSONField(ordinal = -2)
	protected long timestamp = System.currentTimeMillis();
	/**
	 * 操作名称
	 */
	@JSONField(ordinal = -1)
	protected String name;
	
	/**
	 * 创建实例
	 * @param type 日志类
	 * @param connectId 连接ID
	 * @param enterpriseNum 企业ID
	 * @return
	 */
	public static BaseDataSourceLogDto valueOf(DataSourceLogType type,
			long connectId, int enterpriseNum) {
		BaseDataSourceLogDto dto = new ConnectionLogDto();
		dto.type = type.getId();
		dto.connectId = connectId;
		dto.enterpriseNum = enterpriseNum;
		dto.name = type.name();
		return dto;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getConnectId() {
		return connectId;
	}

	public void setConnectId(long connectId) {
		this.connectId = connectId;
	}

	public int getEnterpriseNum() {
		return enterpriseNum;
	}

	public void setEnterpriseNum(int enterpriseNum) {
		this.enterpriseNum = enterpriseNum;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
