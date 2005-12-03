package org.seasar.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.impl.BeanMetaDataImpl;
import org.seasar.dao.impl.BeanMetaDataResultSetHandler;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;

public class BeanMetaDataResultSetHandlerTest extends S2DaoTestCase {

	private BeanMetaData beanMetaData_;

	public BeanMetaDataResultSetHandlerTest(String arg0) {
		super(arg0);
	}

	public void testHandle() throws Exception {
		ResultSetHandler handler = new BeanMetaDataResultSetHandler(
				beanMetaData_);
		String sql = "select emp.*, dept.deptno as deptno_0, dept.dname as dname_0 from emp, dept where empno = 7788 and emp.deptno = dept.deptno";
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(sql);
		Employee ret = null;
		try {
			ResultSet rs = ps.executeQuery();
			try {
				ret = (Employee) handler.handle(rs);
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
		assertNotNull("1", ret);
		System.out.println(ret.getEmpno() + "," + ret.getEname());
		Department dept = ret.getDepartment();
		assertNotNull("2", dept);
		assertEquals("3", 20, dept.getDeptno());
		assertEquals("4", "RESEARCH", dept.getDname());
	}

	public void testHandle2() throws Exception {
		ResultSetHandler handler = new BeanMetaDataResultSetHandler(
				beanMetaData_);
		String sql = "select ename, job from emp where empno = 7788";
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(sql);
		Employee ret = null;
		try {
			ResultSet rs = ps.executeQuery();
			try {
				ret = (Employee) handler.handle(rs);
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
		assertNotNull("1", ret);
		System.out.println(ret.getEmpno() + "," + ret.getEname());
		Department dept = ret.getDepartment();
		assertNull("2", dept);
	}

	public void testHandle3() throws Exception {
		ResultSetHandler handler = new BeanMetaDataResultSetHandler(
				beanMetaData_);
		String sql = "select ename, dept.dname as dname_0 from emp, dept where empno = 7788 and emp.deptno = dept.deptno";
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(sql);
		Employee ret = null;
		try {
			ResultSet rs = ps.executeQuery();
			try {
				ret = (Employee) handler.handle(rs);
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
		assertNotNull("1", ret);
		System.out.println(ret.getEname());
		Department dept = ret.getDepartment();
		assertNotNull("2", dept);
		assertEquals("3", "RESEARCH", dept.getDname());
	}

	public void setUp() {
		include("j2ee.dicon");
	}

	protected void setUpAfterContainerInit() throws Throwable {
        super.setUpAfterContainerInit();
		beanMetaData_ = new BeanMetaDataImpl(Employee.class, getDatabaseMetaData(), getDbms());
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(BeanMetaDataResultSetHandlerTest.class);
	}

}