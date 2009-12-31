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
package org.seasar.dao.impl;

import java.util.List;
import java.util.Map;

public interface EmployeeDao {

    public Class BEAN = Employee.class;

    public String getAllEmployees_SQL_FILE = null;

    public List getAllEmployees();

    public Employee[] getAllEmployeeArray();

    public Map[] getAllEmployeeMap();

    public String findAll_SQL = "SELECT empno, ename, dname FROM emp, dept where emp.deptno = dept.deptno";

    public EmployeeDto[] findAll();

    public String getEmployee_ARGS = "empno";

    public String getEmployee_SQL_FILE = null;

    public Employee getEmployee(int empno);

    public String getCount_SQL_FILE = null;

    public int getCount();

    public String getCount2_SQL_FILE = "org/seasar/dao/impl/sqlfile/getCount.sql";

    public int getCount2();

    public void update(Employee employee);

    public Employee[] getEmployeesByDeptno(int deptno);
}
