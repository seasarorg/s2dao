package org.seasar.dao.impl;

import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.impl.AbstractDao;

public abstract class EmployeeDaoImpl extends AbstractDao implements
		EmployeeDao {

	public EmployeeDaoImpl(DaoMetaDataFactory factory) {
		super(factory);
	}

	public Employee[] getEmployeesByDeptno(int deptno) {
		return (Employee[]) getEntityManager().findArray("deptno = ?",
				new Integer(deptno));
	}
}