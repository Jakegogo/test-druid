package org.test.test_druid.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.test.test_druid.Config;

@Component
public class SimpleQueryDao {

	@Autowired
	Config config;
	

	private void timeoutCancel(final Statement statemenet) {
//		new Thread() {
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(2000);
//					statemenet.cancel();
//					System.err.println("called cancel ");
//				} catch (SQLException e) {
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}.start();
	}

	/**
	 * 测试死锁
	 */
	public void testDeadLockCancel() {

		new Thread() {
			@Override
			public void run() {
				testDeadLockCancel2("test", "test1");
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					// 取消的时候 此线程更新成功
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				testDeadLockCancel2("test1", "test");
			}
		}.start();

		// new Thread() {
		// @Override
		// public void run() {
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// testDeadLockCancel2("test2", "test");
		// }
		// }.start();

	}

	private void testDeadLockCancel2(String table1, String table2) {

		long t1 = System.currentTimeMillis();

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = config.getConnection();
			conn.setAutoCommit(false);

			pst = conn.prepareStatement("update " + table1 + " set name='b'");
			pst.executeUpdate();
			// pst.cancel();

			// 睡眠
			Thread.sleep(5000);

			pst = conn.prepareStatement("update " + table2 + " set name='c'");

			// 超时关闭
			// timeoutCancel(pst);

			t1 = System.currentTimeMillis();
			System.err.println("before call cancel1");
			pst.executeUpdate();
			System.err.println("call cancel1");
			// pst.cancel();

			System.err.println("query execute time : "
					+ (System.currentTimeMillis() - t1));

			Thread.sleep(5000);

			conn.commit();

		} catch (Exception e) {
			try {
				conn.commit();
				System.err.println("roll backed");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			handleException(conn, e);
		} finally {
			t1 = System.currentTimeMillis();
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			System.err.println("close rs time : "
					+ (System.currentTimeMillis() - t1));
			t1 = System.currentTimeMillis();
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			System.err.println("close statement time : "
					+ (System.currentTimeMillis() - t1));
			t1 = System.currentTimeMillis();
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					throw new IllegalStateException(e);
				}
			}
			System.err.println("close conn : "
					+ (System.currentTimeMillis() - t1));
		}
		System.err.println("jdbc time : " + (System.currentTimeMillis() - t1));

	}
	
	
	public void fillStatement(PreparedStatement pst, Object... paras)
			throws SQLException {
		if (paras != null) {
			for (int i = 0; i < paras.length; i++) {
				pst.setObject(i + 1, paras[i]);
			}
		}
	}

	// 处理jdbc异常
	private void handleException(Connection conn, Exception e) {
		e.printStackTrace();
		config.checkConnection(conn);
	}

	// 处理关闭连接
	private void handleClose(Connection conn, PreparedStatement pst) {
		config.close(pst, conn);
	}

	// 处理关闭语句和连接
	private void handleClose(Connection conn, PreparedStatement pst,
			ResultSet rs) {
		config.close(rs, pst, conn);
	}

}