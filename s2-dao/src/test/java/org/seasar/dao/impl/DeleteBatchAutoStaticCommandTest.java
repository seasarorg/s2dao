/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

import java.sql.Timestamp;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.unit.S2DaoTestCase;

public class DeleteBatchAutoStaticCommandTest extends S2DaoTestCase {

    public DeleteBatchAutoStaticCommandTest(String arg0) {
        super(arg0);
    }

    public void testExecuteTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand("deleteBatch");
        Employee emp = new Employee();
        emp.setEmpno(7499);
        emp.setEname("hoge");
        emp.setTimestamp(Timestamp.valueOf("2000-01-01 00:00:00.0"));
        Employee emp2 = new Employee();
        emp2.setEmpno(7369);
        emp2.setEname("hoge2");
        emp2.setTimestamp(Timestamp.valueOf("2000-01-01 00:00:00.0"));
        Integer count = (Integer) cmd.execute(new Object[] { new Employee[] {
                emp, emp2 } });
        assertEquals("1", new Integer(2), count);

        // delete failure test
        SqlCommand cmd2 = dmd.getSqlCommand("deleteBatch2");
        Employee emp3 = new Employee();
        emp3.setEmpno(7782);
        emp3.setEname("hoge");
        emp3.setTimestamp(Timestamp.valueOf("2000-01-01 00:00:00.0"));
        Employee emp4 = new Employee();
        emp4.setEmpno(7788);
        emp4.setEname("hoge2");
        emp4.setTimestamp(Timestamp.valueOf("2000-01-01 00:00:00.0")); // timestamp unmatch
        int[] ret = (int[]) cmd2.execute(new Object[] { new Employee[] { emp3,
                emp4 } });
        assertEquals("2", 2, ret.length);
        assertEquals("3", 1, ret[0]);
        assertEquals("4", 0, ret[1]); // delete failure
    }

    public void setUp() {
        include("j2ee.dicon");
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DeleteBatchAutoStaticCommandTest.class);
    }

}