package org.seasar.dao.dbms;


/**
 * @author higa
 *
 */
public class HSQL extends Standard {

	/**
	 * @see org.seasar.dao.Dbms#getSuffix()
	 */
	public String getSuffix() {
		return "_hsql";
	}
	
	public String getIdentitySelectString() {
		return "CALL IDENTITY()";
	}
	
	public String getSequenceNextValString(String sequenceName) {
		return "SELECT NEXT VALUE FOR " + sequenceName + " FROM SYSTEM_TABLES WHERE table_name = 'SYSTEM_TABLES'";
	}

}
