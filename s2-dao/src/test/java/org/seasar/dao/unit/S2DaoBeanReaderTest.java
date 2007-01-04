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
package org.seasar.dao.unit;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.states.RowStates;

/**
 * @author higa
 * 
 */
public class S2DaoBeanReaderTest extends S2DaoTestCase {

    /**
     * Constructor for InvocationImplTest.
     * 
     * @param arg0
     */
    public S2DaoBeanReaderTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(S2DaoBeanReaderTest.class);
    }

    protected void setUp() throws Exception {
        include("j2ee.dicon");
    }

    public void testRead() throws Exception {
        Employee emp = new Employee();
        emp.setEmpno(7788);
        emp.setEname("SCOTT");
        emp.setDeptno(10);
        Department dept = new Department();
        dept.setDeptno(10);
        dept.setDname("HOGE");
        emp.setDepartment(dept);
        S2DaoBeanReader reader = new S2DaoBeanReader(emp,
                createBeanMetaData(emp.getClass()));
        DataSet ds = reader.read();
        DataTable table = ds.getTable(0);
        DataRow row = table.getRow(0);
        assertEquals("1", new BigDecimal(7788), row.getValue("empno"));
        assertEquals("2", "SCOTT", row.getValue("ename"));
        assertEquals("3", new BigDecimal(10), row.getValue("deptno"));
        assertEquals("4", "HOGE", row.getValue("dname_0"));
        assertEquals("5", RowStates.UNCHANGED, row.getState());
    }

    public void testRead2() throws Exception {
        Employee emp = new Employee();
        emp.setEmpno(7788);
        emp.setEname("SCOTT");
        Timestamp ts = new Timestamp(new Date().getTime());
        emp.setTimestamp(ts);
        S2DaoBeanReader reader = new S2DaoBeanReader(emp,
                createBeanMetaData(emp.getClass()));
        DataSet ds = reader.read();
        DataTable table = ds.getTable(0);
        DataRow row = table.getRow(0);
        assertEquals("1", new BigDecimal(7788), row.getValue("empno"));
        assertEquals("2", "SCOTT", row.getValue("ename"));
        assertEquals("3", ts, row.getValue("last_update"));
    }
}