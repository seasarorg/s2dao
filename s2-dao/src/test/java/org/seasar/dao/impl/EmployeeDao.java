package org.seasar.dao.impl;

import java.util.List;

public interface EmployeeDao {

	public Class BEAN = Employee.class;
	
	public List getAllEmployees();
	
	public Employee[] getAllEmployeeArray();
	
	public String getEmployee_ARGS = "empno";

	public Employee getEmployee(int empno);
	
	public int getCount();
	
	public void update(Employee employee);
	
	public Employee[] getEmployeesByDeptno(int deptno);
}
