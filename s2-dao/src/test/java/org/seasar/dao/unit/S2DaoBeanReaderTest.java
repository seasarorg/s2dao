package org.seasar.dao.unit;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.seasar.dao.unit.S2DaoBeanReader;
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.states.RowStates;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 *  
 */
public class S2DaoBeanReaderTest extends S2TestCase {

	/**
	 * Constructor for InvocationImplTest.
	 * 
	 * @param arg0
	 */
	public S2DaoBeanReaderTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(S2DaoBeanReaderTest.class);
	}

	protected void setUp() throws Exception {
		include("j2ee.dicon");
	}

	public void testRead() throws Exception {
		Employee emp = new Employee();
		emp.setEmpno(7788);
		emp.setEname("SCOTT");
		emp.setDeptno(10);
		Department dept = new Department();
		dept.setDeptno(10);
		dept.setDname("HOGE");
		emp.setDepartment(dept);
		S2DaoBeanReader reader = new S2DaoBeanReader(emp, getDatabaseMetaData());
		DataSet ds = reader.read();
		DataTable table = ds.getTable(0);
		DataRow row = table.getRow(0);
		assertEquals("1", new BigDecimal(7788), row.getValue("empno"));
		assertEquals("2", "SCOTT", row.getValue("ename"));
		assertEquals("3", new BigDecimal(10), row.getValue("deptno"));
		assertEquals("4", "HOGE", row.getValue("dname_0"));
		assertEquals("5", RowStates.UNCHANGED, row.getState());
	}
	
	public void testRead2() throws Exception {
		Employee emp = new Employee();
		emp.setEmpno(7788);
		emp.setEname("SCOTT");
		Timestamp ts = new Timestamp(new Date().getTime());
		emp.setTimestamp(ts);
		S2DaoBeanReader reader = new S2DaoBeanReader(emp, getDatabaseMetaData());
		DataSet ds = reader.read();
		DataTable table = ds.getTable(0);
		DataRow row = table.getRow(0);
		assertEquals("1", new BigDecimal(7788), row.getValue("empno"));
		assertEquals("2", "SCOTT", row.getValue("ename"));
		assertEquals("3", ts, row.getValue("last_update"));
	}
}