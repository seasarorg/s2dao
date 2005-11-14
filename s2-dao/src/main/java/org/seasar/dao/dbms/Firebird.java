package org.seasar.dao.dbms;


/**
 * @author higa
 *
 */
public class Firebird extends Standard {

	/**
	 * @see org.seasar.dao.Dbms#getSuffix()
	 */
	public String getSuffix() {
		return "_firebird";
	}
	
	public String getSequenceNextValString(String sequenceName) {
		return "select gen_id( " + sequenceName + ", 1 ) from RDB$DATABASE";
	}
}
