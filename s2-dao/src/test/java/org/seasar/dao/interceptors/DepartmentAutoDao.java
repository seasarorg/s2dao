package org.seasar.dao.interceptors;

public interface DepartmentAutoDao {

	public Class BEAN = Department.class;
	
	public int update(Department department);
	
	public int delete(Department department);
}
