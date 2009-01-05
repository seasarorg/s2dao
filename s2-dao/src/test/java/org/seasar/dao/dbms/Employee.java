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
package org.seasar.dao.dbms;

import java.io.Serializable;

public class Employee implements Serializable {

    private static final long serialVersionUID = 3099497274013211707L;

    public static final String TABLE = "EMP";

    public static final int department_RELNO = 0;

    public static final int department2_RELNO = 1;

    private long empno;

    private String ename;

    private String job;

    private Short mgr;

    private java.util.Date hiredate;

    private Float sal;

    private Float comm;

    private short deptno;

    private byte[] password;

    private String dummy;

    private Department department;

    private Department department2;

    public Employee() {
    }

    public Employee(long empno) {
        this.empno = empno;
    }

    public long getEmpno() {
        return this.empno;
    }

    public void setEmpno(long empno) {
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

    public Short getMgr() {
        return this.mgr;
    }

    public void setMgr(Short mgr) {
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

    public short getDeptno() {
        return this.deptno;
    }

    public void setDeptno(short deptno) {
        this.deptno = deptno;
    }

    public byte[] getPassword() {
        return this.password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getDummy() {
        return this.dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Department getDepartment2() {
        return this.department2;
    }

    public void setDepartment2(Department department2) {
        this.department2 = department2;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Employee))
            return false;
        Employee castOther = (Employee) other;
        return this.getEmpno() == castOther.getEmpno();
    }

    public int hashCode() {
        return (int) this.getEmpno();
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
        buf.append(deptno).append(" {");
        buf.append(department).append("}");
        return buf.toString();
    }
}
