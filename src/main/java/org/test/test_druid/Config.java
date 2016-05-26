package org.test.test_druid;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Config {

	@Autowired
	DataSource dataSource;

	private int transactionLevel = Connection.TRANSACTION_READ_COMMITTED;
	
	private final ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

	Config(){}

	public int getTransactionLevel() {
		return transactionLevel;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	

	// --------

	/**
	 * Support transaction with Transaction interceptor
	 */
	public final void setThreadLocalConnection(Connection connection) {
		threadLocal.set(connection);
	}

	public final void removeThreadLocalConnection() {
		threadLocal.remove();
	}

	/**
	 * Get Connection. Support transaction if Connection in ThreadLocal
	 */
	public final Connection getConnection() throws SQLException {
		Connection conn = threadLocal.get();
		if (conn != null) {
			threadLocal.set(conn);
			return conn;
		}
		return dataSource.getConnection();
	}

	/**
	 * Helps to implement nested transaction.
	 * Tx.intercept(...) and Db.tx(...) need this method to detected if it in nested transaction.
	 */
	public final Connection getThreadLocalConnection() {
		return threadLocal.get();
	}

	/**
	 * Close ResultSet、Statement、Connection
	 * ThreadLocal support declare transaction.
	 */
	public final void close(ResultSet rs, Statement st, Connection conn) {
		if (rs != null) {try {rs.close();} catch (SQLException e) {e.printStackTrace();}}
		if (st != null) {try {st.close();} catch (SQLException e) {e.printStackTrace();}}

		if (threadLocal.get() == null) {	// in transaction if conn in threadlocal
			if (conn != null) {try {conn.close();}
			catch (SQLException e) {throw new IllegalStateException(e);}}
		}
	}

	public final void close(Statement st, Connection conn) {
		if (st != null) {try {st.close();} catch (SQLException e) {}}

		if (threadLocal.get() == null) {	// in transaction if conn in threadlocal
			if (conn != null) {try {conn.close();}
			catch (SQLException e) {throw new IllegalStateException(e);}}
		}
	}

	public final void checkConnection(Connection conn) {
		boolean isConnection = false;
		
		try {
			if (conn.isValid(2)) {
				isConnection = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (!isConnection) {
			threadLocal.set(null);
		}
	}

	public final void close(Connection conn) {
		if (threadLocal.get() == null)		// in transaction if conn in threadlocal
			if (conn != null)
				try {conn.close();} catch (SQLException e) {throw new IllegalStateException(e);}
	}
}