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

import java.util.List;

public interface EmployeeAutoDao {

    public Class BEAN = Employee.class;

    public String getEmployeeByDeptno_ARGS = "deptno";

    public String getEmployeeByDeptno_ORDER = "deptno asc, empno desc";

    public List getEmployeeByDeptno(int deptno);

    public String getEmployeesBySal_QUERY = "sal BETWEEN ? AND ? ORDER BY empno";

    public List getEmployeesBySal(Float minSal, Float maxSal);

    public String getEmployeesByEnameJob_ARGS = "enames, jobs";

    public String getEmployeesByEnameJob_QUERY = "ename IN /*enames*/('SCOTT','MARY') AND job IN /*jobs*/('ANALYST', 'FREE')";

    public List getEmployeesByEnameJob(List enames, List jobs);

    public List getEmployeesBySearchCondition(EmployeeSearchCondition dto);

    public String getEmployeesBySearchCondition2_QUERY = "department.dname = /*dto.department.dname*/'RESEARCH'";

    public List getEmployeesBySearchCondition2(EmployeeSearchCondition dto);

    public List getEmployeesByEmployee(Employee dto);

    public String getEmployee_ARGS = "empno";

    public Employee getEmployee(int empno);

    public void insert(Employee employee);

    public String insert2_NO_PERSISTENT_PROPS = "job, mgr, hiredate, sal, comm, deptno";

    public void insert2(Employee employee);

    public String insert3_PERSISTENT_PROPS = "deptno";

    public void insert3(Employee employee);

    public void insertBatch(Employee[] employees);

    public int[] insertBatch2(Employee[] employees);

    public void update(Employee employee);

    public String update2_NO_PERSISTENT_PROPS = "job, mgr, hiredate, sal, comm, deptno";

    public void update2(Employee employee);

    public String update3_PERSISTENT_PROPS = "deptno";

    public void update3(Employee employee);

    public boolean update4_CHECK_SINGLE_ROW_UPDATE = false;

    public int update4(Employee employee);

    public void updateBatch(Employee[] employees);

    public int[] updateBatch2(Employee[] employees);

    public void updateBatchByList(List employees);

    public void delete(Employee employee);

    public void deleteBatch(Employee[] employees);

    public int[] deleteBatch2(Employee[] employees);

    public void updateUnlessNull(Employee employee);

}
