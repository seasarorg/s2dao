package org.seasar.dao.dbms;


/**
 * @author higa
 *
 */
public class PostgreSQL extends Standard {

	public String getSuffix() {
		return "_postgre";
	}
	
	public String getSequenceNextValString(String sequenceName) {
		return "select nextval ('" + sequenceName +"')";
	}
}