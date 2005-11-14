package org.seasar.dao.impl;

import java.io.Serializable;

public class Employee3 implements Serializable {
	
	public static final String TABLE = "EMP";
	public static final int department_RELNO = 0;
	public static final String manager_COLUMN = "mgr";

    private Long empno;

    private String ename;

    private String job;

    private Short mgr;

    private java.util.Date hiredate;

    private Float sal;

    private Float comm;

    private Integer deptno;
    
    private Department department;

    public Employee3() {
    }

    public Employee3(Long empno) {
        this.empno = empno;
    }

    public Long getEmpno() {
        return this.empno;
    }

    public void setEmpno(Long empno) {
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

    public Short getManager() {
        return this.mgr;
    }

    public void setManager(Short mgr) {
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

    public Integer getDeptno() {
        return this.deptno;
    }

    public void setDeptno(Integer deptno) {
        this.deptno = deptno;
    }
    
    public Department getDepartment() {
    	return this.department;
    }
    
    public void setDepartment(Department department) {
    	this.department = department;
    }

    public boolean equals(Object other) {
        if ( !(other instanceof Employee3) ) return false;
        Employee3 castOther = (Employee3) other;
        return this.getEmpno() == castOther.getEmpno();
    }

    public int hashCode() {
        return this.getEmpno().hashCode();
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