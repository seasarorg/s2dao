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

import java.util.List;

import org.seasar.extension.unit.S2TestCase;

/**
 * https://www.seasar.org/issues/browse/DAO-19
 * 
 * @author manhole
 */
public class ReturnsArrayDaoTest extends S2TestCase {

    private EmployeeDao employeeDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("ReturnsArrayDaoTest.dicon");
    }

    public void test1() throws Exception {
        final List list = employeeDao.getAllEmployeesAsList();
        final Employee[] array = employeeDao.getAllEmployeesAsArray();
        assertEquals(list.size(), array.length);
        assertEquals(true, 0 < array.length);
    }

    public static interface EmployeeDao {
        Class BEAN = EmployeeImpl.class;

        Employee[] getAllEmployeesAsArray();

        List getAllEmployeesAsList();
    }

    public static interface Employee {
    }

    public static class EmployeeImpl implements Employee {

        public static final String TABLE = "EMP";

        private String ename;

        public String getEname() {
            return ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }
    }

}
