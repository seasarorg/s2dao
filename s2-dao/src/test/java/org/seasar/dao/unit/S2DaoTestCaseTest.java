package org.seasar.dao.unit;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.DataSetImpl;

public class S2DaoTestCaseTest extends S2DaoTestCase {

	public S2DaoTestCaseTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(S2DaoTestCaseTest.class);
	}
	
	protected void setUp() {
		include("j2ee.dicon");
	}

	public void testAssertBeanEquals() {
		DataSet expected = new DataSetImpl();
		DataTable table = expected.addTable("emp");
		table.addColumn("aaa");
		table.addColumn("bbb_0");
		DataRow row = table.addRow();
		row.setValue("aaa", "111");
		row.setValue("bbb_0", "222");
		Hoge bean = new Hoge();
		bean.setAaa("111");
		Foo foo = new Foo();
		foo.setBbb("222");
		bean.setFoo(foo);
		assertEquals("1", expected, bean);
	}

	public void testAssertBeanListEquals() {
		DataSet expected = new DataSetImpl();
		DataTable table = expected.addTable("emp");
		table.addColumn("aaa");
		table.addColumn("bbb_0");
		DataRow row = table.addRow();
		row.setValue("aaa", "111");
		row.setValue("bbb_0", "222");
		Hoge bean = new Hoge();
		bean.setAaa("111");
		Foo foo = new Foo();
		foo.setBbb("222");
		bean.setFoo(foo);
		List list = new ArrayList();
		list.add(bean);
		assertEquals("1", expected, list);
	}

	public static class Hoge {

		public static final int foo_RELNO = 0;
		
		public static final String aaa_ID = "assigned";

		private String aaa;

		private Foo foo;

		/**
		 * @return Returns the aaa.
		 */
		public String getAaa() {
			return aaa;
		}

		/**
		 * @param aaa
		 *            The aaa to set.
		 */
		public void setAaa(String aaa) {
			this.aaa = aaa;
		}

		/**
		 * @return Returns the foo.
		 */
		public Foo getFoo() {
			return foo;
		}

		/**
		 * @param foo
		 *            The foo to set.
		 */
		public void setFoo(Foo foo) {
			this.foo = foo;
		}
	}

	public static class Foo {

		public static final String bbb_ID = "assigned";
		
		private String bbb;

		/**
		 * @return Returns the bbb.
		 */
		public String getBbb() {
			return bbb;
		}

		/**
		 * @param bbb
		 *            The bbb to set.
		 */
		public void setBbb(String bbb) {
			this.bbb = bbb;
		}
	}
}