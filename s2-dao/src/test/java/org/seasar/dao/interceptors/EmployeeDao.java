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

import java.util.List;
import java.util.Map;

public interface EmployeeDao {

    public Class BEAN = Employee.class;

    public List getAllEmployees();

    public String findEmployeeDto_SQL = "select empno, ename, dname from emp, dept where empno = ? and emp.deptno = dept.deptno";

    public String findEmployeeDto_ARGS = "empno";

    public EmployeeDto findEmployeeDto(int empno);

    public String getLabelValue_SQL = "select empno as value, ename as label from emp";

    public Map[] getLabelValue();

    public String getEmployee_ARGS = "empno";

    public Employee getEmployee(int empno);

    public Employee[] getEmployeesByDeptno(int deptno);

    public int getCount();

    public String insert_ARGS = "empno, ename";

    public int insert(int empno, String ename);

    public int update(Employee employee);
}
