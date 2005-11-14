package org.seasar.dao.impl;

public interface Department2AutoDao {

	public Class BEAN = Department2.class;
	
	public Department2 getDepartment(int deptno);
	
	public void insert(Department2 department);
}
