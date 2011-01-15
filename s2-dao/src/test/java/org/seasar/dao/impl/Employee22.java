/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import java.util.HashSet;
import java.util.Set;

/**
 * @author jundu
 *
 */
public class Employee22 {

    private static final long serialVersionUID = 1L;

    public static final String TABLE = "EMP";

    public static final int department_RELNO = 0;

    public static final String empno_ID = "sequence";

    public static final String manager_COLUMN = "mgr";

    public static final String insert_NO_PERSISTENT_PROPS = "dummy";

    private Long empno;

    private Short mgr;

    private Integer deptno;

    private Department department;

    private String dummy;
    
    private Set modifiedPropertySet = new HashSet();

    public Employee22() {
    }

    public Employee22(Long empno) {
        this.empno = empno;
    }

    public Long getEmpno() {
        return this.empno;
    }

    public void setEmpno(Long empno) {
        this.modifiedPropertySet.add("empno");
        this.empno = empno;
    }

    public String getDummy() {
        return this.dummy;
    }

    public void setDummy(String dummy) {
        this.modifiedPropertySet.add("dummy");
        this.dummy = dummy;
    }

    public Short getManager() {
        return this.mgr;
    }

    public void setManager(Short mgr) {
        this.modifiedPropertySet.add("mgr");
        this.mgr = mgr;
    }

    public Integer getDeptno() {
        return this.deptno;
    }

    public void setDeptno(Integer deptno) {
        this.modifiedPropertySet.add("deptno");
        this.deptno = deptno;
    }

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set getModifiedPropertyNames() {
        return this.modifiedPropertySet;
    }

}
