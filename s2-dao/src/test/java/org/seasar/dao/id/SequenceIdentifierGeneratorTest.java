package org.seasar.dao.id;

import org.seasar.dao.dbms.HSQL;
import org.seasar.dao.id.SequenceIdentifierGenerator;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 *  
 */
public class SequenceIdentifierGeneratorTest extends S2TestCase {

	/**
	 * Constructor for InvocationImplTest.
	 * 
	 * @param arg0
	 */
	public SequenceIdentifierGeneratorTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SequenceIdentifierGeneratorTest.class);
	}

	protected void setUp() throws Exception {
		include("j2ee.dicon");
	}

	protected void tearDown() throws Exception {
	}

	public void testGenerateTx() throws Exception {
		SequenceIdentifierGenerator generator = new SequenceIdentifierGenerator("id", new HSQL());
		generator.setSequenceName("myseq");
		Hoge hoge = new Hoge();
		generator.setIdentifier(hoge, getDataSource());
		System.out.println(hoge.getId());
		assertTrue("1", hoge.getId() > 0);
	}
}