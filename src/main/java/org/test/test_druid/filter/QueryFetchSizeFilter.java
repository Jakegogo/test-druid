package org.test.test_druid.filter;

import java.sql.SQLException;

import org.test.test_druid.constant.DataSourceConstants;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

/**
 * 查询单次抓取记录数 (具体要看数据库是否支持,如mysql需要其他配置才支持)
 * @author: jake
 */
public class QueryFetchSizeFilter extends FilterEventAdapter implements EnterpriseIsolateable {
	
	/**
	 * 抓取大小,默认为DEFAULT_QUERY_FETCH_SIZE
	 * 仅在模式为 SQLServerResultSet.TYPE_SS_SERVER_CURSOR_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY时生效
	 */
	private int queryFetchSize = DataSourceConstants.DEFAULT_QUERY_FETCH_SIZE;
	
	/**
	 * 最大获取记录数
	 */
	private int maxFetchRow = DataSourceConstants.DEFAULT_MAX_FETCH_ROW;

	@Override
	protected void statementExecuteBefore(StatementProxy statement, String sql) {
		setQueryFetchSize(statement);
		super.statementExecuteBefore(statement, sql);
	}

	@Override
	protected void statementExecuteBatchBefore(StatementProxy statement) {
		setQueryFetchSize(statement);
		super.statementExecuteBatchBefore(statement);
	}

	@Override
	protected void statementExecuteUpdateBefore(StatementProxy statement,
			String sql) {
		setQueryFetchSize(statement);
		super.statementExecuteUpdateBefore(statement, sql);
	}

	@Override
	protected void statementExecuteQueryBefore(StatementProxy statement,
			String sql) {
		setQueryFetchSize(statement);
		super.statementExecuteQueryBefore(statement, sql);
	}

	@Override
	protected void resultSetOpenAfter(ResultSetProxy resultSet) {
		setQueryFetchSize(resultSet);
		super.resultSetOpenAfter(resultSet);
	}

	/**
	 * 设置单次记录抓取行数
	 * @param resultSet
	 */
	private void setQueryFetchSize(ResultSetProxy resultSet) {
		try {
			resultSet.setFetchSize(queryFetchSize);
		} catch (SQLException se) {
			// ignore
		}
	}

	/**
	 * 设置Statement单次查询抓取行数和最大查询行数
	 * @param statement
	 */
	private void setQueryFetchSize(StatementProxy statement) {
		try {
			statement.setFetchSize(queryFetchSize);
			statement.setMaxRows(maxFetchRow + 1);// +1用于统计数据
		} catch (SQLException se) {
			// ignore
		}
	}

	public int getQueryFetchSize() {
		return queryFetchSize;
	}

	public void setQueryFetchSize(int queryFetchSize) {
		this.queryFetchSize = queryFetchSize;
	}
	
	protected int getMaxFetchRow() {
		return maxFetchRow;
	}

	protected void setMaxFetchRow(int maxFetchRow) {
		this.maxFetchRow = maxFetchRow;
	}

	/**
	 * 企业号
	 */
	protected int enterpriseNum;
	
	public int getEnterpriseNum() {
		return enterpriseNum;
	}

	public void setEnterpriseNum(int enterpriseNum) {
		this.enterpriseNum = enterpriseNum;
	}

}