package org.seasar.dao.interceptors;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 *
 */
public class S2DaoInterceptor3Test extends S2TestCase {

	private DepartmentAutoDao dao_;
	
	public S2DaoInterceptor3Test(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(S2DaoInterceptor3Test.class);
	}
	
	public void setUp() {
		include("DepartmentAutoDao.dicon");
	}

	public void testUpdateTx() throws Exception {
		Department dept = new Department();
		dept.setDeptno(10);
		assertEquals("1", 1, dao_.update(dept));
	}
	
	public void testDeleteTx() throws Exception {
		Department dept = new Department();
		dept.setDeptno(10);
		assertEquals("1", 1, dao_.delete(dept));
	}

}