package org.seasar.dao.interceptors;

import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.impl.AbstractDao;

/**
 * @author higa
 *
 */
public abstract class EmployeeDaoImpl extends AbstractDao implements EmployeeDao {

	/**
	 * @param daoMetaDataFactory
	 */
	public EmployeeDaoImpl(DaoMetaDataFactory daoMetaDataFactory) {
		super(daoMetaDataFactory);
	}

	public Employee[] getEmployeesByDeptno(int deptno) {
		return (Employee[]) getEntityManager().findArray("EMP.deptno = ?",
				new Integer(deptno));
	}
}
