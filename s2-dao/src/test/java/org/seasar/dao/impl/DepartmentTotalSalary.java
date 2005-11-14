package org.seasar.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;

public class DepartmentTotalSalary implements Serializable {

	private Integer deptno;
    private BigDecimal totalSalary;
        
	
	/**
	 * @return Returns the deptno.
	 */
	public Integer getDeptno() {
		return deptno;
	}
	/**
	 * @param deptno The deptno to set.
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
	 * @param totalSalary The totalSalary to set.
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
