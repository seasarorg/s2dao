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
package org.seasar.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;

public class DepartmentTotalSalary implements Serializable {

    private static final long serialVersionUID = -7661032204184374726L;

    private Integer deptno;

    private BigDecimal totalSalary;

    /**
     * @return Returns the deptno.
     */
    public Integer getDeptno() {
        return deptno;
    }

    /**
     * @param deptno
     *            The deptno to set.
     */
    public void setDeptno(Integer deptno) {
        this.deptno = deptno;
    }

    /**
     * @return Returns the totalSalary.
     */
    public BigDecimal getTotalSalary() {
        return totalSalary;
    }

    /**
     * @param totalSalary
     *            The totalSalary to set.
     */
    public void setTotalSalary(BigDecimal totalSalary) {
        this.totalSalary = totalSalary;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(deptno).append(", ");
        buf.append(totalSalary);
        return buf.toString();
    }
}
