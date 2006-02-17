/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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