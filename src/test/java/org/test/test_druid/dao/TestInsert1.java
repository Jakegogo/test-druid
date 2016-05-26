package org.test.test_druid.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.StringUtils;
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
public class TestInsert1 {

	@Autowired
	Config config;

	@Test
	public void testInsert() {
		int count = 100000;
		
		
		long t1 = 0;
		Connection conn = null;
		Statement statemenet = null;
		try {
			conn = config.getConnection();
//			conn.setAutoCommit(false);
			
			
			statemenet = conn.createStatement();
			
			String[] sqls = new String[count];
			for (int i = 0 ;i < count;i++) {
				sqls[i] = "insert into test values(" + (i + 2) + ", 'abc',1)";
				statemenet.addBatch(sqls[i]);
			}
 
			t1 = System.currentTimeMillis();
			int[] rs = statemenet.executeBatch();
			
//			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
//			try {
//				conn.rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
			handleException(conn, e);
		} finally {
			handleClose(conn, statemenet);
		}
		System.out.println("use time: " + (System.currentTimeMillis() - t1));
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
	private void handleClose(Connection conn, Statement pst) {
		config.close(pst, conn);
	}

	// 处理关闭语句和连接
	private void handleClose(Connection conn, PreparedStatement pst,
			ResultSet rs) {
		config.close(rs, pst, conn);
	}

}
