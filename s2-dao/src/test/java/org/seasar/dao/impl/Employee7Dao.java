package org.seasar.dao.impl;


public interface Employee7Dao {

	public Class BEAN = Employee.class;
	
	public static String getCount_hsql_SQL = 
		"SELECT COUNT(*) FROM emp;";
	public int getCount();
	
	public static String deleteEmployee_SQL = 
		"DELETE FROM emp WHERE empno=?;";
	public int deleteEmployee(int empno);
}
