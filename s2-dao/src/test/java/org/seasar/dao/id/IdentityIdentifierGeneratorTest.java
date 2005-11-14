package org.seasar.dao.id;

import org.seasar.dao.dbms.HSQL;
import org.seasar.dao.id.IdentityIdentifierGenerator;
import org.seasar.extension.jdbc.impl.BasicUpdateHandler;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 *  
 */
public class IdentityIdentifierGeneratorTest extends S2TestCase {

	/**
	 * Constructor for InvocationImplTest.
	 * 
	 * @param arg0
	 */
	public IdentityIdentifierGeneratorTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(IdentityIdentifierGeneratorTest.class);
	}

	protected void setUp() throws Exception {
		include("j2ee.dicon");
	}

	protected void tearDown() throws Exception {
	}

	public void testGetGeneratedValueTx() throws Exception {
		BasicUpdateHandler updateHandler = new BasicUpdateHandler(
				getDataSource(), "insert into identitytable(id_name) values('hoge')");
		updateHandler.execute(null);
		IdentityIdentifierGenerator generator = new IdentityIdentifierGenerator("id", new HSQL());
		Hoge hoge = new Hoge();
		generator.setIdentifier(hoge, getDataSource());
		System.out.println(hoge.getId());
		assertTrue("1", hoge.getId() >= 0);
	}
}