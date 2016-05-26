package org.test.test_druid;

import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

public class TableNameReplacer extends MySqlOutputVisitor {

	public TableNameReplacer(Appendable appender) {
		super(appender);
	}

	@Override
	public boolean visit(SQLTableElement x) {
		System.err.println(x);
		return super.visit(x);
	}

}