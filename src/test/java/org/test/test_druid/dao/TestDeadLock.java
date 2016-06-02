package org.test.test_druid.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.test.test_druid.Config;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@Component
public class TestDeadLock {
	
	@Autowired
	Config config;
	
	
	/**
	 * 测试死锁
	 */
	@Test
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
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				testDeadLockCancel2("test1", "test");
			}
		}.start();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

			System.out.println("sleeping " + table1);
			// 睡眠
			Thread.sleep(2000);

			pst = conn.prepareStatement("update " + table2 + " set name='c'");

			// 超时关闭
			// timeoutCancel(pst); 

			t1 = System.currentTimeMillis();
			pst.executeUpdate();
			// pst.cancel();

			System.err.println("query execute time : "
					+ (System.currentTimeMillis() - t1));

			conn.commit();

		} catch (Exception e) {
//			try {
//				conn.commit();
////				System.err.println("roll backed");
//			} catch (SQLException e1) {
//				e1.printStackTrace();
				try {
					conn.rollback();
					System.err.println("roll backed");
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
//			}
			handleException(conn, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		
		
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