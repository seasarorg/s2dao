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
package org.seasar.dao.interceptors;

import org.seasar.dao.NotExactlyOneRowUpdatedRuntimeException;
import org.seasar.dao.pager.PagerContext;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author manhole
 */
public class AssertExactlyOneRowInterceptorTest extends S2TestCase {

    private EmployeeDao employeeDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("AssertExactlyOneRowInterceptorTest.dicon");
        PagerContext.init();
    }

    public void testOneRowTx() throws Exception {
        final int ret = employeeDao.updateSal("AD%");
        assertEquals(1, ret);
    }

    public void testMoreThanOneRowTx() throws Exception {
        try {
            final int ret = employeeDao.updateSal("A%");
            fail("count: " + ret);
        } catch (NotExactlyOneRowUpdatedRuntimeException e) {
            e.printStackTrace();
            assertEquals("EDAO0016", e.getMessageCode());
        }
    }

    public void testNoRowTx() throws Exception {
        try {
            final int ret = employeeDao.updateSal("ZZ%");
            fail("count: " + ret);
        } catch (NotExactlyOneRowUpdatedRuntimeException e) {
            assertEquals("EDAO0016", e.getMessageCode());
        }
    }

    public static interface EmployeeDao {

        public Class BEAN = Employee.class;

        public String updateSal_ARGS = "ename";

        public String updateSal_SQL = "update EMP set SAL = SAL * 2 where ENAME LIKE /*ename*/'ABC'";

        public int updateSal(String name);

    }

}
