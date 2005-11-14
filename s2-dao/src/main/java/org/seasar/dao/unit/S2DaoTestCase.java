package org.seasar.dao.unit;

import java.sql.DatabaseMetaData;
import java.util.List;

import org.seasar.dao.Dbms;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class S2DaoTestCase extends S2TestCase {

	public S2DaoTestCase() {
	}

	/**
	 * @param name
	 */
	public S2DaoTestCase(String name) {
		super(name);
	}

	protected void assertBeanEquals(String message, DataSet expected,
			Object bean) {

		S2DaoBeanReader reader = new S2DaoBeanReader(bean,
				getDatabaseMetaData());
		assertEquals(message, expected, reader.read());
	}

	protected void assertBeanListEquals(String message, DataSet expected,
			List list) {

		S2DaoBeanListReader reader = new S2DaoBeanListReader(list,
				getDatabaseMetaData());
		assertEquals(message, expected, reader.read());
	}

	protected Dbms getDbms() {
		DatabaseMetaData dbMetaData = getDatabaseMetaData();
		return DbmsManager.getDbms(dbMetaData);
	}

}