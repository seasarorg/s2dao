package org.seasar.dao.impl;

public interface Employee4Dao {

	public Class BEAN = Employee4.class;
	
	public String getEmployee_ARGS = "empno";

	public Employee4 getEmployee(int empno);
}
