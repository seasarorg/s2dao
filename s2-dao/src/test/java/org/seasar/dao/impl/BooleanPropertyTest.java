package org.seasar.dao.impl;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.impl.DaoMetaDataImpl;
import org.seasar.dao.impl.InsertAutoStaticCommand;
import org.seasar.dao.impl.SelectDynamicCommand;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.jdbc.impl.BasicResultSetFactory;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 *  
 */
public class BooleanPropertyTest extends S2TestCase {

	/**
	 * Constructor for InvocationImplTest.
	 * 
	 * @param arg0
	 */
	public BooleanPropertyTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(BooleanPropertyTest.class);
	}

	public void setUp() {
		include("j2ee.dicon");
	}

	public void testInsertAndSelectTx() throws Exception {
		DaoMetaData dmd = new DaoMetaDataImpl(Department2AutoDao.class,
				getDataSource(), BasicStatementFactory.INSTANCE,
				BasicResultSetFactory.INSTANCE);
		InsertAutoStaticCommand cmd = (InsertAutoStaticCommand) dmd
				.getSqlCommand("insert");
		Department2 dept = new Department2();
		dept.setDeptno(99);
		dept.setDname("hoge");
		dept.setActive(true);
		cmd.execute(new Object[] { dept });
		SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
				.getSqlCommand("getDepartment");
		Department2 dept2 = (Department2) cmd2
				.execute(new Object[] { new Integer(99) });
		assertEquals("1", true, dept2.isActive());
	}
}