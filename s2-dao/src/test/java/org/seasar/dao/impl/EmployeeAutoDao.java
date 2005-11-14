package org.seasar.dao.impl;

import java.util.List;

public interface EmployeeAutoDao {

	public Class BEAN = Employee.class;
	
	public String getEmployeeByDeptno_ARGS = "deptno";
	public String getEmployeeByDeptno_ORDER = "deptno asc, empno desc";
	public List getEmployeeByDeptno(int deptno);
	
	public String getEmployeesBySal_QUERY = "sal BETWEEN ? AND ? ORDER BY empno";
	public List getEmployeesBySal(Float minSal, Float maxSal);
	
	public String getEmployeesByEnameJob_ARGS = "enames, jobs";
	public String getEmployeesByEnameJob_QUERY = "ename IN /*enames*/('SCOTT','MARY') AND job IN /*jobs*/('ANALYST', 'FREE')";
	public List getEmployeesByEnameJob(List enames, List jobs);
	
	public List getEmployeesBySearchCondition(EmployeeSearchCondition dto);

	public String getEmployeesBySearchCondition2_QUERY = "department.dname = /*dto.department.dname*/'RESEARCH'";
	public List getEmployeesBySearchCondition2(EmployeeSearchCondition dto);
	
	public List getEmployeesByEmployee(Employee dto);
	
	public String getEmployee_ARGS = "empno";
	public Employee getEmployee(int empno);

	public void insert(Employee employee);
	
	public String insert2_NO_PERSISTENT_PROPS = "job, mgr, hiredate, sal, comm, deptno";
	public void insert2(Employee employee);
	
	public String insert3_PERSISTENT_PROPS = "deptno";
	public void insert3(Employee employee);
	
	public void insertBatch(Employee[] employees);
	
	public void update(Employee employee);
	
	public String update2_NO_PERSISTENT_PROPS = "job, mgr, hiredate, sal, comm, deptno";
	public void update2(Employee employee);
	
	public String update3_PERSISTENT_PROPS = "deptno";
	public void update3(Employee employee);
	
	public void updateBatch(Employee[] employees);
	
	public void delete(Employee employee);
	
	public void deleteBatch(Employee[] employees);
}
