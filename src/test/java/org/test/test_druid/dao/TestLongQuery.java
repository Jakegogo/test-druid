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
public class TestLongQuery {

	@Autowired
	Config config;

	@Test
	public void testQuery() {
		get("select top 2000 * from sfa_t_TCirOrderDetail where ProductName = '赠品2' order by OrderCount desc");
	}

	public void get(String sql) {

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = config.getConnection();

			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();
			
			while(rs.next()) {
				System.out.println(rs.getObject(1));
			}
			
		} catch (Exception e) {
			handleException(conn, e); 
		} finally {
			handleClose(conn, pst, rs);
		}
		  
	}
		
	
	public void fillStatement(PreparedStatement pst, Object... paras) throws SQLException {
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
