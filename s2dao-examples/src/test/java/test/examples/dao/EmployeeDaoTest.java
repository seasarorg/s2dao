package test.examples.dao;

import java.util.List;

import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.dataset.DataSet;

import examples.dao.EmployeeDao;

/**
 * @author higa
 *
 */
public class EmployeeDaoTest extends S2DaoTestCase {

	private EmployeeDao employeeDao_;

	public EmployeeDaoTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(EmployeeDaoTest.class);
	}
	
	public void setUp() {
		include("examples/dao/EmployeeDao.dicon");
	}

	public void testGetAllEmployee() throws Exception {
		DataSet expected = readXls("getAllEmployeesResult.xls");
		List actual = employeeDao_.getAllEmployees();
		assertEquals("1", expected, actual);
	}
}