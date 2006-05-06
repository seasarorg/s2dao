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

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.unit.S2DaoTestCase;

public class InsertAutoStaticCommandTest extends S2DaoTestCase {

    public InsertAutoStaticCommandTest(String arg0) {
        super(arg0);
    }

    public void testExecuteTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand("insert");
        Employee emp = new Employee();
        emp.setEmpno(99);
        emp.setEname("hoge");
        Integer count = (Integer) cmd.execute(new Object[] { emp });
        assertEquals("1", new Integer(1), count);
    }

    public void testExecute2Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(IdentityTableAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand("insert");
        IdentityTable table = new IdentityTable();
        table.setIdName("hoge");
        Integer count1 = (Integer) cmd.execute(new Object[] { table });
        assertEquals("1", new Integer(1), count1);
        int id1 = table.getMyid();
        System.out.println(id1);
        Integer count2 = (Integer) cmd.execute(new Object[] { table });
        assertEquals("1", new Integer(1), count2);
        int id2 = table.getMyid();
        System.out.println(id2);

        assertEquals("2", 1, id2 - id1);
    }

    public void testExecute3Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(SeqTableAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand("insert");
        SeqTable table = new SeqTable();
        table.setName("hoge");
        Integer count = (Integer) cmd.execute(new Object[] { table });
        assertEquals("1", new Integer(1), count);
        System.out.println(table.getId());
        assertTrue("2", table.getId() > 0);
    }

    public void testExecute4Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand("insert2");
        Employee emp = new Employee();
        emp.setEmpno(99);
        emp.setEname("hoge");
        Integer count = (Integer) cmd.execute(new Object[] { emp });
        assertEquals("1", new Integer(1), count);
    }

    public void testExecute5Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand("insert3");
        Employee emp = new Employee();
        emp.setEmpno(99);
        emp.setEname("hoge");
        emp.setDeptno(10);
        Integer count = (Integer) cmd.execute(new Object[] { emp });
        assertEquals("1", new Integer(1), count);
    }

    public void setUp() {
        include("j2ee.dicon");
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(InsertAutoStaticCommandTest.class);
    }

}