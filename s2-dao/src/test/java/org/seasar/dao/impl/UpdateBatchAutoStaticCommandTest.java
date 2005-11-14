package org.seasar.dao.impl;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.impl.DaoMetaDataImpl;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.jdbc.impl.BasicResultSetFactory;

public class UpdateBatchAutoStaticCommandTest extends S2DaoTestCase {

	public UpdateBatchAutoStaticCommandTest(String arg0) {
		super(arg0);
	}

	public void testExecuteTx() throws Exception {
		DaoMetaData dmd = new DaoMetaDataImpl(EmployeeAutoDao.class,
				getDataSource(), BasicStatementFactory.INSTANCE,
				BasicResultSetFactory.INSTANCE);
		SqlCommand cmd = dmd.getSqlCommand("updateBatch");
		Employee emp = new Employee();
		emp.setEmpno(7788);
		emp.setEname("hoge");
		Employee emp2 = new Employee();
		emp2.setEmpno(7369);
		emp2.setEname("hoge2");
		Integer count = (Integer) cmd.execute(new Object[] { new Employee[] {
				emp, emp2 } });
		assertEquals("1", new Integer(2), count);
	}

	public void setUp() {
		include("j2ee.dicon");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(UpdateBatchAutoStaticCommandTest.class);
	}

}