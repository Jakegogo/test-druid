package org.test.test_druid;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

public class TestSqlParser {
	
	public static void main(String[] args) {
		
		String sql = "select * from test where id = 1";
		
		StringBuilder out = new StringBuilder();
		TableNameReplacer visitor = new TableNameReplacer(out);
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        
		for (SQLStatement statement : statementList) {
			statement.accept(visitor);
			visitor.println();
		}
       System.out.println(out.toString());
	}

}
