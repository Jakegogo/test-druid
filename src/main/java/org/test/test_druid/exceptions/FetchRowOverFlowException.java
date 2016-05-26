package org.test.test_druid.exceptions;

/**
 * 结果集行数超出限值异常
 * @author Administrator
 */
public class FetchRowOverFlowException extends RuntimeException {
	private static final long serialVersionUID = -6970087434015993215L;
	
	public FetchRowOverFlowException(int currentLimit) {
		super("结果集行数超出限值异常! 当前限制:" + currentLimit);
	}

}
