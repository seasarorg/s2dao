package org.seasar.dao.impl;

import java.io.Serializable;

public class Department2 implements Serializable {
	
	public static final String TABLE = "DEPT2";

	private int deptno;
    private String dname;
    private boolean active;
        
	/**
	 * @return Returns the deptno.
	 */
	public int getDeptno() {
		return deptno;
	}
	/**
	 * @param deptno The deptno to set.
	 */
	public void setDeptno(int deptno) {
		this.deptno = deptno;
	}
	/**
	 * @return Returns the dname.
	 */
	public String getDname() {
		return dname;
	}
	/**
	 * @param dname The dname to set.
	 */
	public void setDname(String dname) {
		this.dname = dname;
	}
	/**
	 * @return Returns the active.
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active The active to set.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
    public String toString() {
    	StringBuffer buf = new StringBuffer();
    	buf.append(deptno).append(", ");
		buf.append(dname);
    	return buf.toString();
    }
}
