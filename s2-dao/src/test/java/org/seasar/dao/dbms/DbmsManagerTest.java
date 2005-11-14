package org.seasar.dao.dbms;

import junit.framework.TestCase;

import org.seasar.dao.dbms.DbmsManager;

/**
 * @author higa
 *  
 */
public class DbmsManagerTest extends TestCase {

	/**
	 * Constructor for InvocationImplTest.
	 * 
	 * @param arg0
	 */
	public DbmsManagerTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DbmsManagerTest.class);
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testCreateAutoSelectList() throws Exception {
		assertNotNull("1", DbmsManager.getDbms(""));
		assertNotNull("2", DbmsManager.getDbms("HSQL Database Engine"));
	}
}