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

import java.io.Serializable;

public class Employee2 implements Serializable {

    public static final String TABLE = "EMP2";

    public static final int department2_RELNO = 0;

    public static final String department2_RELKEYS = "DEPTNUM:DEPTNO";

    private long empno;

    private String ename;

    private Short deptnum;

    private Department2 department2;

    public Employee2() {
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(empno).append(", ");
        buf.append(ename).append(", ");
        buf.append(deptnum).append(", {");
        buf.append(department2).append("}");
        return buf.toString();
    }

    /**
     * @return Returns the department2.
     */
    public Department2 getDepartment2() {
        return department2;
    }

    /**
     * @param department2
     *            The department2 to set.
     */
    public void setDepartment2(Department2 department2) {
        this.department2 = department2;
    }

    /**
     * @return Returns the deptnum.
     */
    public Short getDeptnum() {
        return deptnum;
    }

    /**
     * @param deptnum
     *            The deptnum to set.
     */
    public void setDeptnum(Short deptnum) {
        this.deptnum = deptnum;
    }

    /**
     * @return Returns the empno.
     */
    public long getEmpno() {
        return empno;
    }

    /**
     * @param empno
     *            The empno to set.
     */
    public void setEmpno(long empno) {
        this.empno = empno;
    }

    /**
     * @return Returns the ename.
     */
    public String getEname() {
        return ename;
    }

    /**
     * @param ename
     *            The ename to set.
     */
    public void setEname(String ename) {
        this.ename = ename;
    }
}
