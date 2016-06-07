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
public class TestLongQuery2 {

	@Autowired
	Config config;

	@Test
	public void testQuery() {
		long t1 = System.currentTimeMillis();
		for (int i = 0;i < 2000;i++) {
			System.out.println(i);
			get("SELECT s.storeid,s.guid,s.storecode,s.storename,s.address,s.saleareaid,d.departmentname,d.codepath,s.storetype,sd.DicKey,us.usernumber,us.username,us.mobilephone,COUNT(DISTINCT exd.DisplayID)DisplayCun,SUM(CAST(exd.amountmoney AS FLOAT))amountmoney FROM sfa_t_store s " +
			" INNER JOIN  sfa_t_ExDisplay exd ON exd.Guid=s.Guid" +
			" LEFT JOIN dbo.com_t_userinfo us ON us.usernumber = exd.UserNumber" +
			" LEFT JOIN dbo.sfa_t_TSysDictionary sd ON sd.RowID = s.storetype" +
			" LEFT JOIN dbo.com_t_department d ON d.departmentid = s.saleareaid" +
			" INNER JOIN sfa_t_tMonDisplayPlan dp ON dp.Guid=exd.Guid" +
			" WHERE DATEDIFF(d,exd.DisplayEndTime,GETDATE())<=0 AND DATEDIFF(d,PlanTime,GETDATE())<=0" +
			" GROUP BY s.storeid,s.guid,s.storecode,s.storename,s.address,s.saleareaid,d.departmentname,d.codepath,s.storetype,sd.DicKey,us.usernumber,us.username,us.mobilephone");
		}
		System.out.println("used : " + (System.currentTimeMillis() - t1));
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
