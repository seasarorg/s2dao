/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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

/**
 * @author higa
 * @author manhole
 */
public class FieldDaoMetaDataImplTest extends DaoMetaDataImplTest {

    public void setUp() {
        include("FieldDaoMetaDataImplTest.dicon");
    }

    protected Class getBeanClass(String className) {
        if (className.equals("Employee")) {
            return Employee.class;
        }
        fail(className);
        return null;
    }

    protected Object getBean(String className) {
        if (className.equals("Employee")) {
            return new Employee();
        } else if (className.equals("Employee3")) {
            return new Employee3();
        } else if (className.equals("Employee9")) {
            return new Employee9();
        } else if (className.equals("EmployeeSearchCondition")) {
            return new EmployeeSearchCondition();
        } else if (className.equals("Department")) {
            return new Department();
        }
        fail(className);
        return null;
    }

    protected Class getDaoClass(String className) {
        if (className.equals("EmployeeDao")) {
            return EmployeeDao.class;
        } else if (className.equals("EmployeeAutoDao")) {
            return EmployeeAutoDao.class;
        } else if (className.equals("IllegalEmployeeAutoDao")) {
            return IllegalEmployeeAutoDao.class;
        } else if (className.equals("Employee2Dao")) {
            return Employee2Dao.class;
        } else if (className.equals("Employee3Dao")) {
            return Employee3Dao.class;
        } else if (className.equals("Employee4Dao")) {
            return Employee4Dao.class;
        } else if (className.equals("Employee5Dao")) {
            return Employee5Dao.class;
        } else if (className.equals("Employee6Dao")) {
            return Employee6Dao.class;
        } else if (className.equals("Employee7Dao")) {
            return Employee7Dao.class;
        } else if (className.equals("Employee8Dao")) {
            return Employee8Dao.class;
        } else if (className.equals("Employee9Dao")) {
            return Employee9Dao.class;
        } else if (className.equals("Employee10Dao")) {
            return Employee10Dao.class;
        } else if (className.equals("Employee8Manager")) {
            return Employee8Manager.class;
        } else if (className.equals("DepartmentTotalSalaryDao")) {
            return DepartmentTotalSalaryDao.class;
        } else if (className.equals("EmployeeDaoImpl")) {
            return EmployeeDaoImpl.class;
        } else if (className.equals("EmployeeExDao")) {
            return EmployeeExDao.class;
        }
        fail(className);
        return null;
    }

}
