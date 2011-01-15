/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.UpdateFailureRuntimeException;
import org.seasar.dao.unit.S2DaoTestCase;

public class DeleteAutoStaticCommandTest extends S2DaoTestCase {

    public DeleteAutoStaticCommandTest(String arg0) {
        super(arg0);
    }

    public void testExecuteTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand("delete");

        SqlCommand cmd2 = dmd.getSqlCommand("getEmployee");
        Employee emp = (Employee) cmd2
                .execute(new Object[] { new Integer(7788) });
        Integer count = (Integer) cmd.execute(new Object[] { emp });
        assertEquals("1", new Integer(1), count);
    }

    public void testExecute2Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(DepartmentAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand("delete");
        Department dept = new Department();
        dept.setDeptno(10);
        Integer count = (Integer) cmd.execute(new Object[] { dept });
        assertEquals("1", new Integer(1), count);
    }

    public void testExecute3Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(DepartmentAutoDao.class);
        DeleteAutoStaticCommand cmd = (DeleteAutoStaticCommand) dmd
                .getSqlCommand("delete");
        Department dept = new Department();
        dept.setDeptno(10);
        dept.setVersionNo(-1);
        try {
            cmd.execute(new Object[] { dept });
            fail("1");
        } catch (UpdateFailureRuntimeException ex) {
            System.out.println(ex);
        }
    }

    public void setUp() {
        include("j2ee.dicon");
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DeleteAutoStaticCommandTest.class);
    }

}