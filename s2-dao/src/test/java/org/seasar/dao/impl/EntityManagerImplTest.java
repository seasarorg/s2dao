package org.seasar.dao.impl;

import java.util.List;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.EntityManager;
import org.seasar.dao.impl.EntityManagerImpl;
import org.seasar.extension.unit.S2TestCase;

public class EntityManagerImplTest extends S2TestCase {

	private EntityManager entityManager_;

	public EntityManagerImplTest(String arg0) {
		super(arg0);
	}

	public void testFind() throws Exception {
		List employees = entityManager_.find("empno = ?", new Integer(7788));
		assertEquals("1", 1, employees.size());
	}
    
	public void testFind_BTS6491() throws Exception {
        List employees = entityManager_.find(
            "\n SELECT * FROM EMP WHERE empno = ?", new Integer(7788));
        System.out.println(employees);
        assertEquals("1", 1, employees.size());
    }

	public void testFindArray() throws Exception {
		Employee[] employees = (Employee[]) entityManager_.findArray(
				"empno = ?", new Integer(7788));
		assertEquals("1", 1, employees.length);
	}
	
	public void testFindBean() throws Exception {
		Employee employee = (Employee) entityManager_.findBean(
				"empno = ?", new Integer(7788));
		assertEquals("1", "SCOTT", employee.getEname());
	}
	
	public void testFindObject() throws Exception {
		Integer count = (Integer) entityManager_.findObject(
				"select count(*) from emp");
		assertEquals("1", new Integer(14), count);
	}

	public void setUp() {
		include("dao.dicon");
		DaoMetaDataFactory factory = (DaoMetaDataFactory) getComponent(DaoMetaDataFactory.class);
		DaoMetaData daoMetaData = factory.getDaoMetaData(EmployeeDao.class);
		entityManager_ = new EntityManagerImpl(daoMetaData);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(EntityManagerImplTest.class);
	}

}