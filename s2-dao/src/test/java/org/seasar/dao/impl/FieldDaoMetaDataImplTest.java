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

import org.seasar.dao.AnnotationReaderFactory;

/**
 * @author higa
 *  
 */
public class FieldDaoMetaDataImplTest extends DaoMetaDataImplTest {
	private AnnotationReaderFactory readerFactory;

	/**
	 * Constructor for InvocationImplTest.
	 * 
	 * @param arg0
	 */
	public FieldDaoMetaDataImplTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(FieldDaoMetaDataImplTest.class);
	}

	public void setUp() {
		include("FieldDaoMetaDataImplTest.dicon");
	}
	protected Class getBeanClass(String className) {
		if(className.equals("Employee")){
			return Employee.class;
		}
		fail();
		return null;
	}
	protected Object getBean(String className) {
		if(className.equals("Employee")){
			return new Employee();
		}else if(className.equals("Employee3")){
			return new Employee3();
		}else if(className.equals("EmployeeSearchCondition")){
			return new EmployeeSearchCondition();
		}else if(className.equals("Department")){
			return new Department();
		}
		fail();
		return null;
	}
	protected Class getDaoClass(String className) {
		if(className.equals("EmployeeDao")){
			return EmployeeDao.class;
		}else if(className.equals("EmployeeAutoDao")){
			return EmployeeAutoDao.class;
		}else if(className.equals("FormUseHistoryDao")){
			return FormUseHistoryDao.class;
		}else if(className.equals("IllegalEmployeeAutoDao")){
			return IllegalEmployeeAutoDao.class;
		}else if(className.equals("Employee2Dao")){
			return Employee2Dao.class;
		}else if(className.equals("Employee3Dao")){
			return Employee3Dao.class;
		}else if(className.equals("Employee4Dao")){
			return Employee4Dao.class;
		}else if(className.equals("Employee5Dao")){
			return Employee5Dao.class;
		}else if(className.equals("Employee6Dao")){
			return Employee6Dao.class;
		}else if(className.equals("Employee7Dao")){
			return Employee7Dao.class;
		}else if(className.equals("DepartmentTotalSalaryDao")){
			return DepartmentTotalSalaryDao.class;
		}
		fail();
		return null;
	}

}