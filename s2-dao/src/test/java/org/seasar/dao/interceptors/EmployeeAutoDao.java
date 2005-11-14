package org.seasar.dao.interceptors;

import java.util.List;


public interface EmployeeAutoDao {

	public Class BEAN = Employee.class;
	
	public String getEmployee_ARGS = "empno";
	public Employee getEmployee(int empno);
	
	public String getEmployeesBySal_QUERY = "sal BETWEEN ? AND ?";
	public List getEmployeesBySal(float minSal, float maxSal);
	
	public int insert(Employee employee);
	
	public int insertBatch(Employee[] employees);
}
