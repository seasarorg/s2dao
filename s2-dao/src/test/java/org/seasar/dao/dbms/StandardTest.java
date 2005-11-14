package org.seasar.dao.dbms;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.dbms.Standard;
import org.seasar.dao.impl.BeanMetaDataImpl;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 *  
 */
public class StandardTest extends S2TestCase {

	/**
	 * Constructor for InvocationImplTest.
	 * 
	 * @param arg0
	 */
	public StandardTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(StandardTest.class);
	}

	protected void setUp() throws Exception {
		include("j2ee.dicon");
	}

	protected void tearDown() throws Exception {
	}

	public void testCreateAutoSelectList() throws Exception {
		Dbms dbms = new Standard();
		BeanMetaData bmd = new BeanMetaDataImpl(Employee.class,
				getDatabaseMetaData(), dbms);
		String sql = dbms.getAutoSelectSql(bmd);
		System.out.println(sql);
	}
}