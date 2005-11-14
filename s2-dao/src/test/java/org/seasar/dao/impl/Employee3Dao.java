package org.seasar.dao.impl;

import java.util.List;

public interface Employee3Dao {

	public Class BEAN = Employee3.class;
	
	public List getEmployees(Employee3 dto);
	
	public String getEmployees2_QUERY = "ORDER BY empno";
	public List getEmployees2(Employee3 dto);
}
