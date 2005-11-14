package org.seasar.dao.impl;

import org.seasar.dao.impl.BeanMetaDataImpl;
import org.seasar.dao.impl.BeanMetaDataResultSetHandler;
import org.seasar.dao.impl.SelectDynamicCommand;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.jdbc.impl.BasicResultSetFactory;

public class SelectDynamicCommandTest extends S2DaoTestCase {

	public SelectDynamicCommandTest(String arg0) {
		super(arg0);
	}

	public void testExecute() throws Exception {
		SelectDynamicCommand cmd = new SelectDynamicCommand(getDataSource(),
				BasicStatementFactory.INSTANCE,
				new BeanMetaDataResultSetHandler(new BeanMetaDataImpl(
						Employee.class, getDatabaseMetaData(), getDbms())),
						BasicResultSetFactory.INSTANCE);
		cmd.setSql("SELECT * FROM emp WHERE empno = /*empno*/1234");
		Employee emp = (Employee) cmd
				.execute(new Object[] { new Integer(7788) });
		System.out.println(emp);
		assertNotNull("1", emp);
	}

	public void setUp() {
		include("j2ee.dicon");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SelectDynamicCommandTest.class);
	}

}