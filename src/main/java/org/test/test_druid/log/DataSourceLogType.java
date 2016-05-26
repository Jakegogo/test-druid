package org.test.test_druid.log;

/**
 * 数据源日志类型
 * @author Administrator
 */
public enum DataSourceLogType {

	/**
	 * 连接打开
	 */
	Connect_Open(1),
	
	/**
	 * 连接关闭
	 */
	Connect_Close(2),
	
	/**
	 * 连接异常日志
	 */
	Connect_Timeout_Exception(3),
	
	/**
	 * 语句执行完成
	 */
	Statement_Execute(4),
	
	/**
	 * 语句关闭
	 */
	Statement_Close(5),
	
	/**
	 * 语句执行异常
	 */
	Statement_Exception(6),
	
	/**
	 * 语句执行超时日志
	 */
	StatementTimeout_Exception(7),
	
	/**
	 * 打开结果集
	 */
	ResultSet_Open(8),
	
	/**
	 * 关闭结果集
	 */
	ResultSet_Close(9),
	
	/**
	 * 语句结果集超出行数异常
	 */
	ResultSet_Overflow_Exception(10),
	
	/**
	 * 获取连接
	 */
	Get_Connection(11),
	
	/**
	 * 释放连接
	 */
	Release_Connection(12),
	
	/**
	 * 限流溢出
	 */
	Limit_Overflow(13),
	
	/**
	 * 流量警告
	 */
	Limit_Warn(14)
	;
	
	// 编号
	private int id;
	
	DataSourceLogType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	
	
}
