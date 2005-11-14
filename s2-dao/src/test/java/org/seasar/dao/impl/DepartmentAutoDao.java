package org.seasar.dao.impl;

public interface DepartmentAutoDao {

	public Class BEAN = Department.class;
	
	public void insert(Department department);
	
	public void insertBatch(Department[] departents);
	
	public void update(Department department);
	
	public void updateBatch(Department[] departents);
	
	public void delete(Department department);
	
	public void deleteBatch(Department[] departents);
}
