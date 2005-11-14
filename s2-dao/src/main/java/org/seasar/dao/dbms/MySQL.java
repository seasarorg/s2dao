package org.seasar.dao.dbms;


/**
 * @author higa
 *
 */
public class MySQL extends Standard {

	/**
	 * @see org.seasar.dao.Dbms#getSuffix()
	 */
	public String getSuffix() {
		return "_mysql";
	}
	
	public String getIdentitySelectString() {
		return "SELECT LAST_INSERT_ID()";
	}
}
