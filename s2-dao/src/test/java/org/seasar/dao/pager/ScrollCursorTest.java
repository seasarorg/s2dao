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
package org.seasar.dao.pager;

import java.util.List;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author manhole
 */
public class ScrollCursorTest extends S2TestCase {

    private EmployeeDao employeeDao;

    private PagerResultSetFactoryWrapper pagerResultSetFactoryWrapper;

    protected void setUp() throws Exception {
        super.setUp();
        include("ScrollCursorTest.dicon");
    }

    protected void setUpAfterBindFields() throws Throwable {
        super.setUpAfterBindFields();
        pagerResultSetFactoryWrapper.setUseScrollCursor(isScrollCursor());
    }

    protected boolean isScrollCursor() {
        return true;
    }

    public void testPageLimitTx() throws Exception {
        // ## Arrange ##
        DefaultPagerCondition condition = new DefaultPagerCondition();
        condition.setLimit(2);
        assertEquals(0, condition.getOffset());
        assertEquals(0, condition.getCount());

        // ## Act ##
        List employees = employeeDao.getEmployees(condition);

        // ## Assert ##
        assertEquals(14, condition.getCount());
        assertEquals(2, employees.size());
        assertEquals(7369L, getEmployee(employees, 0).getEmpno());
        assertEquals(7499L, getEmployee(employees, 1).getEmpno());
    }

    public void testOffsetTx() throws Exception {
        // ## Arrange ##
        DefaultPagerCondition condition = new DefaultPagerCondition();
        condition.setLimit(2);
        condition.setOffset(1);

        // ## Act ##
        List employees = employeeDao.getEmployees(condition);

        // ## Assert ##
        assertEquals(14, condition.getCount());
        assertEquals(2, employees.size());
        assertEquals(7499L, getEmployee(employees, 0).getEmpno());
        assertEquals(7521L, getEmployee(employees, 1).getEmpno());
    }

    public void testLastPageTx() throws Exception {
        // ## Arrange ##
        DefaultPagerCondition condition = new DefaultPagerCondition();
        condition.setLimit(5);
        condition.setOffset(10);

        // ## Act ##
        List employees = employeeDao.getEmployees(condition);

        // ## Assert ##
        assertEquals(14, condition.getCount());
        assertEquals(4, employees.size());
        assertEquals(7876L, getEmployee(employees, 0).getEmpno());
        assertEquals(7900L, getEmployee(employees, 1).getEmpno());
        assertEquals(7902L, getEmployee(employees, 2).getEmpno());
        assertEquals(7934L, getEmployee(employees, 3).getEmpno());
    }

    private Employee getEmployee(List employees, int i) {
        return (Employee) employees.get(i);
    }

}
