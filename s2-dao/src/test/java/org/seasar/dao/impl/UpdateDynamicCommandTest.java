package org.seasar.dao.impl;

import org.seasar.dao.impl.UpdateDynamicCommand;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.unit.S2TestCase;

public class UpdateDynamicCommandTest extends S2TestCase {

	public UpdateDynamicCommandTest(String arg0) {
		super(arg0);
	}

	public void testExecuteTx() throws Exception {
		UpdateDynamicCommand cmd = new UpdateDynamicCommand(getDataSource(),
				BasicStatementFactory.INSTANCE);
		cmd
				.setSql("UPDATE emp SET ename = /*employee.ename*/'HOGE' WHERE empno = /*employee.empno*/1234");
		cmd.setArgNames(new String[] { "employee" });

		Employee emp = new Employee();
		emp.setEmpno(7788);
		emp.setEname("SCOTT");
		Integer count = (Integer) cmd.execute(new Object[] { emp });
		assertEquals("1", new Integer(1), count);
	}

	public void setUp() {
		include("j2ee.dicon");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(UpdateDynamicCommandTest.class);
	}

}