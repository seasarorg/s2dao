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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author manhole
 */
public class ListBindTest extends S2DaoTestCase {

    private EmployeeDao employeeDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("ListBindTest.dicon");
    }

    public void testListBindTx() throws Exception {
        // ## Arrange ##
        final List param = Arrays.asList(new Integer[] { new Integer(7566),
                new Integer(7900) });

        // ## Act ##
        final List employees = employeeDao.findByIdList(param);

        // ## Assert ##
        assertEquals(2, employees.size());
        {
            final Employee emp = (Employee) employees.get(0);
            assertEquals("JONES", emp.getEname());
        }
        {
            final Employee emp = (Employee) employees.get(1);
            assertEquals("JAMES", emp.getEname());
        }
    }

    public void testArrayBindTx() throws Exception {
        // ## Arrange ##
        final Integer[] param = new Integer[] { new Integer(7900),
                new Integer(7902) };

        // ## Act ##
        final List employees = employeeDao.findByIdArray(param);

        // ## Assert ##
        assertEquals(2, employees.size());
        {
            final Employee emp = (Employee) employees.get(0);
            assertEquals("JAMES", emp.getEname());
        }
        {
            final Employee emp = (Employee) employees.get(1);
            assertEquals("FORD", emp.getEname());
        }
    }

    public static interface EmployeeDao {

        public Class BEAN = Employee.class;

        public String findById_ARGS = "empno";

        public Employee findById(int empno);

        public String findByIdList_ARGS = "empno";

        public static String findByIdList_QUERY = "/*BEGIN*/ WHERE "
                + "/*IF empno != null*/ empno IN /*empno*/('aaa', 'bbb')/*END*/"
                + " /*END*/";

        public List findByIdList(List empnos);

        public String findByIdArray_ARGS = "empno";

        public static String findByIdArray_QUERY = "/*BEGIN*/ WHERE "
                + "/*IF empno != null*/ empno IN /*empno*/('aaa')/*END*/"
                + " /*END*/";

        public List findByIdArray(Integer[] empnos);

        public void insert(Employee employee);

        public void update(Employee employee);

    }

    public static class Employee {

        public static final String TABLE = "EMP";

        private Integer empno;

        private String ename;

        private String job;

        private Integer mgr;

        private java.util.Date hiredate;

        private Float sal;

        private Float comm;

        private int deptno;

        private Timestamp tstamp;

        public Integer getEmpno() {
            return this.empno;
        }

        public void setEmpno(Integer empno) {
            this.empno = empno;
        }

        public java.lang.String getEname() {
            return this.ename;
        }

        public void setEname(java.lang.String ename) {
            this.ename = ename;
        }

        public java.lang.String getJob() {
            return this.job;
        }

        public void setJob(java.lang.String job) {
            this.job = job;
        }

        public Integer getMgr() {
            return this.mgr;
        }

        public void setMgr(Integer mgr) {
            this.mgr = mgr;
        }

        public java.util.Date getHiredate() {
            return this.hiredate;
        }

        public void setHiredate(java.util.Date hiredate) {
            this.hiredate = hiredate;
        }

        public Float getSal() {
            return this.sal;
        }

        public void setSal(Float sal) {
            this.sal = sal;
        }

        public Float getComm() {
            return this.comm;
        }

        public void setComm(Float comm) {
            this.comm = comm;
        }

        public int getDeptno() {
            return this.deptno;
        }

        public void setDeptno(int deptno) {
            this.deptno = deptno;
        }

        public Timestamp getTstamp() {
            return tstamp;
        }

        public void setTstamp(Timestamp timestamp) {
            this.tstamp = timestamp;
        }

        public boolean equals(Object other) {
            if (!(other instanceof Employee)) {
                return false;
            }
            Employee castOther = (Employee) other;
            return getEmpno() == castOther.getEmpno();
        }

        public int hashCode() {
            return getEmpno().intValue();
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(empno).append(", ");
            buf.append(ename).append(", ");
            buf.append(job).append(", ");
            buf.append(mgr).append(", ");
            buf.append(hiredate).append(", ");
            buf.append(sal).append(", ");
            buf.append(comm).append(", ");
            buf.append(deptno).append(", ");
            buf.append(tstamp);
            return buf.toString();
        }
    }
}
