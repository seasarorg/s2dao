package org.seasar.dao.interceptors;

import java.util.List;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 *  
 */
public class S2DaoInterceptor2Test extends S2TestCase {

	private EmployeeAutoDao dao_;

	public S2DaoInterceptor2Test(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(S2DaoInterceptor2Test.class);
	}

	public void setUp() {
		include("EmployeeAutoDao.dicon");
	}

	public void testInsertTx() throws Exception {
		Employee emp = new Employee();
		emp.setEmpno(99);
		emp.setEname("hoge");
		assertEquals("1", 1, dao_.insert(emp));
	}

	public void testSelect() throws Exception {
		Employee emp = dao_.getEmployee(7788);
		System.out.println(emp);
		assertEquals("1", 7788, emp.getEmpno());
	}
	
	public void testSelectQuery() throws Exception {
		List employees = dao_.getEmployeesBySal(0, 1000);
		System.out.println(employees);
		assertEquals("1", 2, employees.size());
	}

	public void testInsertBatchTx() throws Exception {
		Employee emp = new Employee();
		emp.setEmpno(99);
		emp.setEname("hoge");
		Employee emp2 = new Employee();
		emp2.setEmpno(98);
		emp2.setEname("hoge2");
		assertEquals("1", 2, dao_.insertBatch(new Employee[] { emp, emp2 }));
	}
	
	public void testFullWidthTildaTx() throws Exception {
		Employee emp = new Employee();
		emp.setEmpno(99);
		emp.setEname("Å`");
		dao_.insert(emp);
		Employee emp2 = dao_.getEmployee(99);
		assertEquals("1", emp.getEname(), emp2.getEname());
	}
}