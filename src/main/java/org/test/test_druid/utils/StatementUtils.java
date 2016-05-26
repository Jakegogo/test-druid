package org.test.test_druid.utils;

import java.util.ArrayList;
import java.util.List;

import org.test.test_druid.constant.DataSourceConstants;

import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

/**
 * 语句工具类
 * @author Administrator
 *
 */
public class StatementUtils {
	
	/**
	 * 获取数据源ID, 如果获取不到就用sql语句替代
	 * @param statement StatementProxy
	 * @param sqlInstead sql语句内容
	 * @return
	 */
	public static String sqlId(StatementProxy statement, String sqlInstead) {
		Object value = statement.getAttribute(DataSourceConstants.DataSourceID_KEY);
		if (value != null) {
			return (String) value;
		}
		return sqlInstead;
	}
	
	/**
	 * 获取statement ID
	 * @param statement
	 * @return
	 */
	public static String stmtId(StatementProxy statement) {
		StringBuffer buf = new StringBuffer();
		if (statement instanceof CallableStatementProxy) {
			buf.append("cstmt-");
		} else if (statement instanceof PreparedStatementProxy) {
			buf.append("pstmt-");
		} else {
			buf.append("stmt-");
		}
		buf.append(statement.getId());

		return buf.toString();
	}
	
	/**
	 * 获取语句参数
	 * @param statement StatementProxy
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List getParameters(StatementProxy statement) {
		int parametersSize = statement.getParametersSize();

		List<Object> parameters = new ArrayList<Object>(parametersSize);
		for (int i = 0; i < parametersSize; ++i) {
			JdbcParameter jdbcParam = statement.getParameter(i);
			parameters.add(jdbcParam.getValue());
		}

		return parameters;
	}
	
	
	/**
	 * 获取statement ID
	 * @param resultSet ResultSetProxy
	 * @return
	 */
	public static String stmtId(ResultSetProxy resultSet) {
		return stmtId(resultSet.getStatementProxy());
	}
	
	
	
}
