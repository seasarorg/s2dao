package org.seasar.dao.id;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;

/**
 * @author higa
 *
 */
public class SequenceIdentifierGenerator extends
		AbstractIdentifierGenerator {

	private String sequenceName_;
	
	/**
	 * @param propertyName
	 * @param dbms
	 */
	public SequenceIdentifierGenerator(String propertyName, Dbms dbms) {
		super(propertyName, dbms);
	}
	
	public String getSequenceName() {
		return sequenceName_;
	}
	
	public void setSequenceName(String sequenceName) {
		sequenceName_ = sequenceName;
	}

	public void setIdentifier(Object bean, DataSource ds) {
		Object value = executeSql(ds, getDbms().getSequenceNextValString(sequenceName_), null);
		setIdentifier(bean, value);
	}
	
	public boolean isSelfGenerate() {
		return getDbms().isSelfGenerate();
	}

}
