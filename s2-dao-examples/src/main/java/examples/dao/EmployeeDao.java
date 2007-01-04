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
package examples.dao;

import java.util.List;

public interface EmployeeDao {

    public Class BEAN = Employee.class;

    public List getAllEmployees();

    public String getEmployee_ARGS = "empno";

    public Employee getEmployee(int empno);

    public int getCount();

    public String getEmployeeByJobDeptno_ARGS = "job, deptno";

    public List getEmployeeByJobDeptno(String job, Integer deptno);

    public String getEmployeeByDeptno_ARGS = "deptno";

    public String getEmployeeByDeptno_QUERY = "/*IF deptno != null*/deptno = /*deptno*/123\n"
            + "-- ELSE 1=1\n" + "/*END*/";

    public List getEmployeeByDeptno(Integer deptno);

    public int update(Employee employee);

}
