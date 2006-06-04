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
package examples.dao;

import java.util.List;

import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.dataset.DataSet;

/**
 * @author higa
 * 
 */
public class EmployeeDaoTest extends S2DaoTestCase {

    private EmployeeDao employeeDao_;

    public EmployeeDaoTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(EmployeeDaoTest.class);
    }

    public void setUp() {
        include("examples/dao/EmployeeDao.dicon");
    }

    public void testGetAllEmployee() throws Exception {
        DataSet expected = readXls("getAllEmployeesResult.xls");
        List actual = employeeDao_.getAllEmployees();
        assertEquals("1", expected, actual);
    }
}