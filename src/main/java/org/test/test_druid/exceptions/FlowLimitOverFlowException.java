package org.test.test_druid.exceptions;

/**
 * 数据源超出限流异常
 * @author Administrator
 */
public class FlowLimitOverFlowException extends RuntimeException {
	private static final long serialVersionUID = -6970087434015993215L;
	
	public FlowLimitOverFlowException(String name, long currentLimit) {
		super(name + "异常! 当前限流:" + currentLimit);
	}

}
