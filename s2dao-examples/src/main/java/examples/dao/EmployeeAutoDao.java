package examples.dao;

import java.util.List;

public interface EmployeeAutoDao {

	public Class BEAN = Employee.class;

	public List getAllEmployees();

	public String getEmployeeByJobDeptno_ARGS = "job, deptno";

	public List getEmployeeByJobDeptno(String job, Integer deptno);
	
	public String getEmployeeByEmpno_ARGS = "empno";

	public Employee getEmployeeByEmpno(int empno);

	public String getEmployeesBySal_QUERY = "sal BETWEEN ? AND ? ORDER BY empno";

	public List getEmployeesBySal(float minSal, float maxSal);

	public String getEmployeeByDname_ARGS = "dname_0";

	public List getEmployeeByDname(String dname);
	
	public List getEmployeesBySearchCondition(EmployeeSearchCondition dto);
	
	public void update(Employee employee);
}