package org.seasar.dao.impl;

import junit.framework.TestCase;

import org.seasar.dao.impl.RelationKey;

/**
 * @author higa
 *
 */
public class RelationKeyTest extends TestCase {
	
	/**
	 * Constructor for InvocationImplTest.
	 * @param arg0
	 */
	public RelationKeyTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(RelationKeyTest.class);
	}

	protected void tearDown() throws Exception {
	}

	public void testEquals() throws Exception {
		Object[] values = new Object[]{"1", "2"};
		RelationKey pk = new RelationKey(values);
		assertEquals("1", pk, pk);
		assertEquals("2", pk, new RelationKey(values));
		assertEquals("3", false, new RelationKey(new Object[]{"1"}).equals(pk));
	}
	
	public void testHashCode() throws Exception {
		Object[] values = new Object[]{"1", "2"};
		RelationKey pk = new RelationKey(values);
		assertEquals("1", "1".hashCode() + "2".hashCode(), pk.hashCode());
	}
	
	public static class MyBean {
		public static final String TABLE = "MyBean";
		public static final String bbb_COLUMN = "myBbb";
		public static final int ccc_RELNO = 0;
		public static final String ccc_RELKEYS = "ddd:id";
		
		private Integer aaa_;
		private String bbb_;
		private Ccc ccc_;
		private Integer ddd_;
		
		public Integer getAaa() {
			return aaa_;
		}
		
		public void setAaa(Integer aaa) {
			aaa_ = aaa;
		}
		
		public String getBbb() {
			return bbb_;
		}
		
		public void setBbb(String bbb) {
			bbb_ = bbb;
		}
		
		public Ccc getCcc() {
			return ccc_;
		}
		
		public void setCcc(Ccc ccc) {
			ccc_ = ccc;
		}
		
		public Integer getDdd() {
			return ddd_;
		}
		
		public void setDdd(Integer ddd) {
			ddd_ = ddd;
		}
	}
	
	public static class Ccc {
		private Integer id_;
		public Integer getId() {
			return id_;
		}
		public void setId(Integer id) {
			id_ = id;
		}
	}
}