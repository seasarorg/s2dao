package examples.dao;

public interface DepartmentDao {

	public Class BEAN = Department.class;
	
	public void insert(Department department);
	
	public void update(Department department);
	
	public void delete(Department department);
}
