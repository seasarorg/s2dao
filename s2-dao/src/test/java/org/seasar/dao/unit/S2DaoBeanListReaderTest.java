/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
import java.util.ArrayList;
import java.util.List;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;

/**
 * @author higa
 * @author manhole
 */
public class S2DaoBeanListReaderTest extends S2DaoTestCase {

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
        List list = new ArrayList();
        list.add(emp);
        S2DaoBeanListReader reader = new S2DaoBeanListReader(list,
                getBeanMetaDataFactory());
        DataSet ds = reader.read();
        DataTable table = ds.getTable(0);
        DataRow row = table.getRow(0);
        assertEquals("1", new BigDecimal(7788), row.getValue("empno"));
        assertEquals("2", "SCOTT", row.getValue("ename"));
        assertEquals("3", new BigDecimal(10), row.getValue("deptno"));
        assertEquals("4", "HOGE", row.getValue("dname_0"));
    }

}
