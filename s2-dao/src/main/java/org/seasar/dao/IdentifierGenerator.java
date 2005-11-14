package org.seasar.dao;

import javax.sql.DataSource;

/**
 * @author higa
 *
 */
public interface IdentifierGenerator {

	public boolean isSelfGenerate();
	
	public void setIdentifier(Object bean, DataSource ds);
}
