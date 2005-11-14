package org.seasar.dao.impl;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.UpdateFailureRuntimeException;
import org.seasar.dao.impl.DaoMetaDataImpl;
import org.seasar.dao.impl.DeleteAutoStaticCommand;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.jdbc.impl.BasicResultSetFactory;

public class DeleteAutoStaticCommandTest extends S2DaoTestCase {

	public DeleteAutoStaticCommandTest(String arg0) {
		super(arg0);
	}

	public void testExecuteTx() throws Exception {
		DaoMetaData dmd = new DaoMetaDataImpl(EmployeeAutoDao.class,
				getDataSource(), BasicStatementFactory.INSTANCE,
				BasicResultSetFactory.INSTANCE);
		SqlCommand cmd = dmd.getSqlCommand("delete");

		SqlCommand cmd2 = dmd.getSqlCommand("getEmployee");
		Employee emp = (Employee) cmd2
				.execute(new Object[] { new Integer(7788) });
		Integer count = (Integer) cmd.execute(new Object[] { emp });
		assertEquals("1", new Integer(1), count);
	}

	public void testExecute2Tx() throws Exception {
		DaoMetaData dmd = new DaoMetaDataImpl(DepartmentAutoDao.class,
				getDataSource(), BasicStatementFactory.INSTANCE,
				BasicResultSetFactory.INSTANCE);
		SqlCommand cmd = dmd.getSqlCommand("delete");
		Department dept = new Department();
		dept.setDeptno(10);
		Integer count = (Integer) cmd.execute(new Object[] { dept });
		assertEquals("1", new Integer(1), count);
	}

	public void testExecute3Tx() throws Exception {
		DaoMetaData dmd = new DaoMetaDataImpl(DepartmentAutoDao.class,
				getDataSource(), BasicStatementFactory.INSTANCE,
				BasicResultSetFactory.INSTANCE);
		DeleteAutoStaticCommand cmd = (DeleteAutoStaticCommand) dmd
				.getSqlCommand("delete");
		Department dept = new Department();
		dept.setDeptno(10);
		dept.setVersionNo(-1);
		try {
			cmd.execute(new Object[] { dept });
			fail("1");
		} catch (UpdateFailureRuntimeException ex) {
			System.out.println(ex);
		}
	}

	public void setUp() {
		include("j2ee.dicon");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DeleteAutoStaticCommandTest.class);
	}

}