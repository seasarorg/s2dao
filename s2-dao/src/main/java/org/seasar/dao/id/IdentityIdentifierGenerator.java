package org.seasar.dao.id;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;

/**
 * @author higa
 *
 */
public class IdentityIdentifierGenerator extends
		AbstractIdentifierGenerator {

	/**
	 * @param propertyName
	 * @param dbms
	 */
	public IdentityIdentifierGenerator(String propertyName, Dbms dbms) {
		super(propertyName, dbms);
	}

	/**
	 * @see org.seasar.dao.IdentifierGenerator#setIdentifier(java.lang.Object, javax.sql.DataSource)
	 */
	public void setIdentifier(Object bean, DataSource ds) {
		Object value = executeSql(ds, getDbms().getIdentitySelectString(), null);
		setIdentifier(bean, value);
	}

	/**
	 * @see org.seasar.dao.IdentifierGenerator#isSelfGenerate()
	 */
	public boolean isSelfGenerate() {
		return false;
	}

}
