/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.dao.interceptors;

import java.util.List;
import java.util.Map;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class S2DaoInterceptorTest extends S2TestCase {

    private EmployeeDao dao;

    public void setUp() {
        include("EmployeeDao.dicon");
    }

    public void testSelectBeanList() throws Exception {
        List employees = dao.getAllEmployees();
        for (int i = 0; i < employees.size(); ++i) {
            System.out.println(employees.get(i));
        }
        assertEquals("1", true, employees.size() > 0);
    }

    public void testSelectBean() throws Exception {
        Employee employee = dao.getEmployee(7788);
        System.out.println(employee);
        assertEquals("1", "SCOTT", employee.getEname());
    }

    public void testSelectDto() throws Exception {
        EmployeeDto dto = dao.findEmployeeDto(7788);
        assertEquals("SCOTT", dto.getEname());
        assertEquals("RESEARCH", dto.getDname());
    }

    public void testSelectMap() throws Exception {
        Map[] ret = dao.getLabelValue();
        assertTrue(ret.length > 0);
        for (int i = 0; i < ret.length; i++) {
            System.out.println(ret[i]);
        }
    }

    public void testSelectObject() throws Exception {
        int count = dao.getCount();
        System.out.println("count:" + count);
        assertEquals("1", true, count > 0);
    }

    public void testUpdateTx() throws Exception {
        Employee employee = dao.getEmployee(7788);
        assertEquals("1", 1, dao.update(employee));
    }

    public void testEntityManager() throws Exception {
        Employee[] employees = dao.getEmployeesByDeptno(10);
        assertEquals("1", 3, employees.length);
    }

    public void testInsertTx() throws Exception {
        dao.insert(9999, "hoge");
    }
}