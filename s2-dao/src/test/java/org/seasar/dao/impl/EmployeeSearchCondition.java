package org.seasar.dao.impl;

public class EmployeeSearchCondition {

	public static final String dname_COLUMN = "dname_0";
	private Department department;
	private String job;
	private String dname;
	private String orderByString;
	
	/**
	 * @return Returns the department.
	 */
	public Department getDepartment() {
		return department;
	}
	/**
	 * @param department The department to set.
	 */
	public void setDepartment(Department department) {
		this.department = department;
	}
	/**
	 * @return Returns the dname.
	 */
	public String getDname() {
		return dname;
	}
	/**
	 * @param dname The dname to set.
	 */
	public void setDname(String dname) {
		this.dname = dname;
	}
	/**
	 * @return Returns the job.
	 */
	public String getJob() {
		return job;
	}
	/**
	 * @param job The job to set.
	 */
	public void setJob(String job) {
		this.job = job;
	}
	
	/**
	 * @return Returns the orderByString.
	 */
	public String getOrderByString() {
		return orderByString;
	}
	/**
	 * @param orderByString The orderByString to set.
	 */
	public void setOrderByString(String orderByString) {
		this.orderByString = orderByString;
	}
}
