/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.test.test_druid.filter;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.test.test_druid.exceptions.FetchRowOverFlowException;
import org.test.test_druid.log.DataSourceLogType;
import org.test.test_druid.log.dtos.ConnectTimeoutLogDto;
import org.test.test_druid.log.dtos.ConnectionLogDto;
import org.test.test_druid.log.dtos.DataSourceLogDto;
import org.test.test_druid.log.dtos.ResultSetLogDto;
import org.test.test_druid.log.dtos.ResultSetOverFlowLogDto;
import org.test.test_druid.log.dtos.StatementLogDto;
import org.test.test_druid.utils.JsonUtils;
import org.test.test_druid.utils.StatementUtils;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

/**
 * 自定义日志监控过滤器
 * @author Administrator
 */
public class MonitorLogFilter extends FilterEventAdapter implements EnterpriseIsolateable {

	private Logger customLogger = LoggerFactory.getLogger("druid.sql.CustomLogger");

	private boolean statementCloseAfterLogEnable = true;
	private boolean statementExecutableSqlLogEnable = true;

	private boolean resultSetOpenAfterLogEnable = true;
	private boolean resultSetCloseAfterLogEnable = true;

	private boolean dataSourceLogEnabled = true;
	private boolean connectionLogEnabled = true;
	private boolean statementLogEnabled = true;
	private boolean resultSetLogEnabled = true;

	protected DataSourceProxy dataSource;
	
	@Autowired
	private transient FetchSizeFilter queryFetchSizeFilter;

	protected MonitorLogFilter() {
		configFromProperties(System.getProperties());
	}
	
	/**
	 * 构造方法
	 * @param queryFetchSizeFilter QueryFetchSizeFilter 依赖QueryFetchSizeFilter
	 */
	public MonitorLogFilter(FetchSizeFilter queryFetchSizeFilter) {
		this();
		this.queryFetchSizeFilter = queryFetchSizeFilter;
	}
	

	public void configFromProperties(Properties properties) {
		{
			String prop = properties.getProperty("druid.log.conn");
			if ("false".equals(prop)) {
				connectionLogEnabled = false;
			} else if ("true".equals(prop)) {
				connectionLogEnabled = true;
			}
		}
		{
			String prop = properties.getProperty("druid.log.stmt");
			if ("false".equals(prop)) {
				statementLogEnabled = false;
			} else if ("true".equals(prop)) {
				statementLogEnabled = true;
			}
		}
		{
			String prop = properties.getProperty("druid.log.rs");
			if ("false".equals(prop)) {
				resultSetLogEnabled = false;
			} else if ("true".equals(prop)) {
				resultSetLogEnabled = true;
			}
		}
		{
			String prop = properties
					.getProperty("druid.log.stmt.executableSql");
			if ("true".equals(prop)) {
				statementExecutableSqlLogEnable = true;
			} else if ("false".equals(prop)) {
				statementExecutableSqlLogEnable = false;
			}
		}
	}

	@Override
	public void init(DataSourceProxy dataSource) {
		this.dataSource = dataSource;
	}

	public boolean isResultSetCloseAfterLogEnabled() {
		return isResultSetLogEnabled() && resultSetCloseAfterLogEnable;
	}

	public void setResultSetCloseAfterLogEnabled(
			boolean resultSetCloseAfterLogEnable) {
		this.resultSetCloseAfterLogEnable = resultSetCloseAfterLogEnable;
	}

	public boolean isResultSetOpenAfterLogEnabled() {
		return isResultSetLogEnabled() && resultSetOpenAfterLogEnable;
	}

	public void setResultSetOpenAfterLogEnabled(
			boolean afterResultSetOpenLogEnable) {
		this.resultSetOpenAfterLogEnable = afterResultSetOpenLogEnable;
	}

	public boolean isStatementCloseAfterLogEnabled() {
		return isStatementLogEnabled() && statementCloseAfterLogEnable;
	}

	public void setStatementCloseAfterLogEnabled(
			boolean afterStatementCloseLogEnable) {
		this.statementCloseAfterLogEnable = afterStatementCloseLogEnable;
	}

	public boolean isStatementExecutableSqlLogEnable() {
		return statementExecutableSqlLogEnable;
	}

	public void setStatementExecutableSqlLogEnable(
			boolean statementExecutableSqlLogEnable) {
		this.statementExecutableSqlLogEnable = statementExecutableSqlLogEnable;
	}

	public boolean isDataSourceLogEnabled() {
		return dataSourceLogEnabled;
	}

	public void setDataSourceLogEnabled(boolean dataSourceLogEnabled) {
		this.dataSourceLogEnabled = dataSourceLogEnabled;
	}

	public boolean isConnectionLogEnabled() {
		return connectionLogEnabled;
	}

	public void setConnectionLogEnabled(boolean connectionLogEnabled) {
		this.connectionLogEnabled = connectionLogEnabled;
	}

	public boolean isStatementLogEnabled() {
		return statementLogEnabled;
	}

	public void setStatementLogEnabled(boolean statementLogEnabled) {
		this.statementLogEnabled = statementLogEnabled;
	}

	public boolean isResultSetLogEnabled() {
		return resultSetLogEnabled;
	}

	public void setResultSetLogEnabled(boolean resultSetLogEnabled) {
		this.resultSetLogEnabled = resultSetLogEnabled;
	}

	@Override
	public void connection_connectAfter(ConnectionProxy connection) {
		if (connection == null) {
			return;
		}

		if (isConnectionLogEnabled()) {
			logger(ConnectionLogDto.valueOf(DataSourceLogType.Connect_Open,
					connection.getId(), enterpriseNum));
		}
	}

	@Override
	public void connection_close(FilterChain chain, ConnectionProxy connection)
			throws SQLException {
		super.connection_close(chain, connection);

		if (isConnectionLogEnabled()) {
			logger(ConnectionLogDto.valueOf(DataSourceLogType.Connect_Close,
					connection.getId(), enterpriseNum));
		}
	}

	@Override
	protected void statementExecuteBefore(StatementProxy statement, String sql) {
		statement.setLastExecuteStartNano();
	}

	@Override
	protected void statementExecuteQueryBefore(StatementProxy statement,
			String sql) {
		statement.setLastExecuteStartNano();
	}

	@Override
	protected void statementExecuteUpdateBefore(StatementProxy statement,
			String sql) {
		statement.setLastExecuteStartNano();
	}

	@Override
	protected void statementExecuteBatchBefore(StatementProxy statement) {
		statement.setLastExecuteStartNano();
	}

	@Override
	public void statement_close(FilterChain chain, StatementProxy statement)
			throws SQLException {
		super.statement_close(chain, statement);

		if (statementCloseAfterLogEnable && isStatementLogEnabled()) {
			logger(StatementLogDto.valueOf(DataSourceLogType.Statement_Close,
					statement.getConnectionProxy().getId(), enterpriseNum,
					StatementUtils.stmtId(statement)));
		}
	}

	@Override
	protected void statementExecuteAfter(StatementProxy statement, String sql,
			boolean firstResult) {
		if (isStatementLogEnabled()) {
			statement.setLastExecuteTimeNano();
			long nanos = statement.getLastExecuteTimeNano();
			long millis = nanos / (1000 * 1000);

			logger(StatementLogDto.valueOf(DataSourceLogType.Statement_Execute,
					statement.getConnectionProxy().getId(), enterpriseNum,
					StatementUtils.stmtId(statement), millis,
					StatementUtils.sqlId(statement, sql),
					StatementUtils.getParameters(statement)));
		}
	}

	@Override
	protected void statementExecuteBatchAfter(StatementProxy statement,
			int[] result) {
		String sql;
		if (statement instanceof PreparedStatementProxy) {
			sql = ((PreparedStatementProxy) statement).getSql();
		} else {
			sql = statement.getBatchSql();
		}

		if (isStatementLogEnabled()) {
			statement.setLastExecuteTimeNano();
			long nanos = statement.getLastExecuteTimeNano();
			long millis = nanos / (1000 * 1000);

			logger(StatementLogDto.valueOf(DataSourceLogType.Statement_Execute,
					statement.getConnectionProxy().getId(), enterpriseNum,
					StatementUtils.stmtId(statement), millis,
					StatementUtils.sqlId(statement, sql),
					StatementUtils.getParameters(statement)));
		}
	}

	@Override
	protected void statementExecuteQueryAfter(StatementProxy statement,
			String sql, ResultSetProxy resultSet) {

		if (isStatementLogEnabled()) {
			statement.setLastExecuteTimeNano();
			long nanos = statement.getLastExecuteTimeNano();
			long millis = nanos / (1000 * 1000);

			logger(StatementLogDto.valueOf(DataSourceLogType.Statement_Execute,
					statement.getConnectionProxy().getId(), enterpriseNum,
					StatementUtils.stmtId(statement), millis,
					StatementUtils.sqlId(statement, sql),
					StatementUtils.getParameters(statement)));
		}
	}

	@Override
	protected void statementExecuteUpdateAfter(StatementProxy statement,
			String sql, int updateCount) {

		if (isStatementLogEnabled()) {
			statement.setLastExecuteTimeNano();
			long nanos = statement.getLastExecuteTimeNano();
			long millis = nanos / (1000 * 1000);

			logger(StatementLogDto.valueOf(DataSourceLogType.Statement_Execute,
					statement.getConnectionProxy().getId(), enterpriseNum,
					StatementUtils.stmtId(statement), millis,
					StatementUtils.sqlId(statement, sql),
					StatementUtils.getParameters(statement)));
		}
	}
	
	
	@Override
	public void resultSet_close(FilterChain chain, ResultSetProxy resultSet)
			throws SQLException {
		int cloumnCount = 0;
		try {
			ResultSetMetaData meta = resultSet.getMetaData();
			cloumnCount = meta.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		chain.resultSet_close(resultSet);

		
		StatementProxy statement = resultSet.getStatementProxy();
		long connectId = statement.getConnectionProxy().getId();
		String statementId = StatementUtils.stmtId(resultSet);
		
		
		if (isResultSetCloseAfterLogEnabled()) {

			logger(ResultSetLogDto.valueOf(DataSourceLogType.ResultSet_Close,
					connectId, enterpriseNum, statementId,
					resultSet.getFetchRowCount(), cloumnCount));// TODO 测试 resultSet.getFetchRowCount()值
		}
		
		// 结果集超出行数
		if (resultSet.getFetchRowCount() > queryFetchSizeFilter.getMaxFetchRow()) {
			
			logger(ResultSetOverFlowLogDto.valueOf(
					DataSourceLogType.ResultSet_Overflow_Exception, connectId,
					cloumnCount, statementId,
					StatementUtils.sqlId(resultSet.getStatementProxy(), 
							statement.getLastExecuteSql())));
			// 需要抛出异常, 防止造成业务层误判数据(量)
			throw new FetchRowOverFlowException(queryFetchSizeFilter.getMaxFetchRow());
		}
	}

	@Override
	protected void resultSetOpenAfter(ResultSetProxy resultSet) {
		if (resultSetOpenAfterLogEnable && isResultSetLogEnabled()) {

			logger(ResultSetLogDto.valueOf(DataSourceLogType.ResultSet_Open,
					resultSet.getStatementProxy().getConnectionProxy().getId(),
					enterpriseNum, StatementUtils.stmtId(resultSet)));

		}
	}


	@Override
	public void dataSource_releaseConnection(FilterChain chain,
			DruidPooledConnection conn) throws SQLException {
		long connectionId = -1;

		if (conn.getConnectionHolder() != null) {
			ConnectionProxy connection = (ConnectionProxy) conn
					.getConnectionHolder().getConnection();
			connectionId = connection.getId();
		}

		chain.dataSource_recycle(conn);

		if (isConnectionLogEnabled()) {
			logger(DataSourceLogDto.valueOf(
					DataSourceLogType.Release_Connection, connectionId, enterpriseNum,
					conn.getConnectedTimeMillis()));
		}
	}
	
	

	@Override
	public ConnectionProxy connection_connect(FilterChain chain, Properties info)
			throws SQLException {
		try {
			return super.connection_connect(chain, info);
		} catch (SQLException e) {
			// 连接异常日志
			logger(ConnectTimeoutLogDto.valueOf(DataSourceLogType.Connect_Timeout_Exception, enterpriseNum, getExceptionTrace(e)));
			throw e;
		}
		
	}

	
	@Override
	public DruidPooledConnection dataSource_getConnection(FilterChain chain,
			DruidDataSource dataSource, long maxWaitMillis) throws SQLException {
		
		DruidPooledConnection conn = null;
		try {
			 conn = chain.dataSource_connect(dataSource,
				maxWaitMillis);
		} catch (SQLException e) {
			// 连接异常日志
			logger(ConnectTimeoutLogDto.valueOf(DataSourceLogType.Connect_Timeout_Exception, enterpriseNum, getExceptionTrace(e), 1));
			throw e;
		}

		ConnectionProxy connection = (ConnectionProxy) conn
				.getConnectionHolder().getConnection();

		if (isConnectionLogEnabled()) {
			logger(DataSourceLogDto.valueOf(DataSourceLogType.Get_Connection,
					connection.getId(), enterpriseNum, conn.getConnectedTimeMillis()));
		}

		return conn;
	}
	
	
	@Override
	protected void statement_executeErrorAfter(StatementProxy statement,
			String sql, Throwable error) {
		if (isStatementLogEnabled()) {
			statement.setLastExecuteTimeNano();
			long nanos = statement.getLastExecuteTimeNano();
			long millis = nanos / (1000 * 1000);

			logger(StatementLogDto
					.valueOf(DataSourceLogType.Statement_Exception, statement
							.getConnectionProxy().getId(), enterpriseNum,
							StatementUtils.stmtId(statement), millis,
							StatementUtils.sqlId(statement, sql),
							StatementUtils.getParameters(statement),
							getExceptionTrace(error)));
		}
	}
	
	
	private String getExceptionTrace(Throwable error) {
		return error.toString();
	}

	// 转换成LOG字符串
	private void logger(Object object) {
		customLogger.info(JsonUtils.object2JsonString(object));
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) {
		return iface == this.getClass() || iface == MonitorLogFilter.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) {
		if (iface == this.getClass() || iface == MonitorLogFilter.class) {
			return (T) this;
		}
		return null;
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
