package org.seasar.dao.id;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;

/**
 * @author higa
 *
 */
public class AssignedIdentifierGenerator extends
		AbstractIdentifierGenerator {

	public AssignedIdentifierGenerator(String propertyName, Dbms dbms) {
		super(propertyName, dbms);
	}

	/**
	 * @see org.seasar.dao.IdentifierGenerator#setIdentifier(java.lang.Object, javax.sql.DataSource)
	 */
	public void setIdentifier(Object bean, DataSource ds) {
	}

	/**
	 * @see org.seasar.dao.IdentifierGenerator#isSelfGenerate()
	 */
	public boolean isSelfGenerate() {
		return true;
	}
}