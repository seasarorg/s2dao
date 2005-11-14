package examples.dao;

import java.util.List;

public interface EmployeeDao {

	public Class BEAN = Employee.class;
	
	public List getAllEmployees();
	
	public String getEmployee_ARGS = "empno";

	public Employee getEmployee(int empno);
	
	public int getCount();
	
	public String getEmployeeByJobDeptno_ARGS = "job, deptno";
	
	public List getEmployeeByJobDeptno(String job, Integer deptno);
	
	public String getEmployeeByDeptno_ARGS = "deptno";
	public String getEmployeeByDeptno_QUERY = "/*IF deptno != null*/deptno = /*deptno*/123\n"+
			"  /*ELSE*/ 1=1\n"+
			"/*END*/";
	public List getEmployeeByDeptno(Integer deptno);

	public int update(Employee employee);
}
