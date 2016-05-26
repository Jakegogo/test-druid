package org.test.test_druid.exceptions;

/**
 * 数据源限流超时异常
 * @author Administrator
 */
public class FlowLimitTimeoutException extends RuntimeException {
	private static final long serialVersionUID = -6970087434015993215L;
	
	public FlowLimitTimeoutException(String name, long currentLimit) {
		super(name + "超时异常! 当前限流:" + currentLimit);
	}

}
