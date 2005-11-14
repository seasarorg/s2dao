package examples.dao;

import java.util.List;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

public class EmployeeDaoClient {

	private static final String PATH = "examples/dao/EmployeeDao.dicon";

	public static void main(String[] args) {
		S2Container container = S2ContainerFactory.create(PATH);
		container.init();
		try {
			EmployeeDao dao = (EmployeeDao) container
					.getComponent(EmployeeDao.class);
			List employees = dao.getAllEmployees();
			for (int i = 0; i < employees.size(); ++i) {
				System.out.println(employees.get(i));
			}
			
			Employee employee = dao.getEmployee(7788);
			System.out.println(employee);
			
			int count = dao.getCount();
			System.out.println("count:" + count);
			
			dao.getEmployeeByJobDeptno(null, null);
			dao.getEmployeeByJobDeptno("CLERK", null);
			dao.getEmployeeByJobDeptno(null, new Integer(20));
			dao.getEmployeeByJobDeptno("CLERK", new Integer(20));
			dao.getEmployeeByDeptno(new Integer(20));
			dao.getEmployeeByDeptno(null);
			
			System.out.println("updatedRows:" + dao.update(employee));
		} finally {
			container.destroy();
		}

	}
}