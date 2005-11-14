package org.seasar.dao.interceptors;

import java.util.List;

public interface EmployeeDao {

	public Class BEAN = Employee.class;
	
	public List getAllEmployees();
	
	public String getEmployee_ARGS = "empno";
	public Employee getEmployee(int empno);
	
	public Employee[] getEmployeesByDeptno(int deptno);
	
	public int getCount();
	
	public String insert_ARGS = "empno, ename";
	
	public int insert(int empno, String ename);
	
	public int update(Employee employee);
}
