package org.seasar.dao.id;

import org.seasar.dao.Dbms;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.dao.dbms.HSQL;
import org.seasar.dao.id.AssignedIdentifierGenerator;
import org.seasar.dao.id.IdentifierGeneratorFactory;
import org.seasar.dao.id.IdentityIdentifierGenerator;
import org.seasar.dao.id.SequenceIdentifierGenerator;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 *  
 */
public class IdentifierGeneratorFactoryTest extends S2TestCase {

	/**
	 * Constructor for InvocationImplTest.
	 * 
	 * @param arg0
	 */
	public IdentifierGeneratorFactoryTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(IdentifierGeneratorFactoryTest.class);
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testCreateIdentifierGenerator() throws Exception {
		Dbms dbms = new HSQL();
		Hoge hoge = new Hoge();
		hoge.setId(1);
		IdentifierGenerator generator = IdentifierGeneratorFactory.createIdentifierGenerator("id", dbms, null);
		assertEquals("1", AssignedIdentifierGenerator.class, generator.getClass());
		generator = IdentifierGeneratorFactory.createIdentifierGenerator("id", dbms, "identity");
		assertEquals("2", IdentityIdentifierGenerator.class, generator.getClass());
		generator = IdentifierGeneratorFactory.createIdentifierGenerator("id", dbms, "sequence, sequenceName = myseq");
		assertEquals("3", "myseq", ((SequenceIdentifierGenerator) generator).getSequenceName());
	}
}