package org.seasar.dao.impl;

public interface Employee5Dao {

	public Class BEAN = Employee5.class;
	
	public String getEmployee_ARGS = "empno";

	public Employee5 getEmployee(int empno);
}
