package examples.dao;

import java.util.List;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

public class Employee2DaoClient {

	private static final String PATH = "examples/dao/Employee2Dao.dicon";

	public static void main(String[] args) {
		S2Container container = S2ContainerFactory.create(PATH);
		container.init();
		try {
			Employee2Dao dao = (Employee2Dao) container
					.getComponent(Employee2Dao.class);
			List employees = dao.getEmployees("CO");
			for (int i = 0; i < employees.size(); ++i) {
				System.out.println(employees.get(i));
			}
			Employee employee = dao.getEmployee(7788);
			System.out.println(employee);
		} finally {
			container.destroy();
		}

	}
}