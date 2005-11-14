package org.seasar.dao.dbms;


/**
 * @author higa
 *
 */
public class DB2 extends Standard {

	/**
	 * @see org.seasar.dao.Dbms#getSuffix()
	 */
	public String getSuffix() {
		return "_db2";
	}
	
	public String getIdentitySelectString() {
		return "values IDENTITY_VAL_LOCAL()";
	}

	public String getSequenceNextValString(String sequenceName) {
		return "values nextval for " + sequenceName;
	}
}
