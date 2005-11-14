package examples.dao;

import java.util.List;

import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.impl.AbstractDao;

public abstract class Employee2DaoImpl extends AbstractDao implements Employee2Dao {

	public static Class BEAN = Employee.class;

	public Employee2DaoImpl(DaoMetaDataFactory daoMetaDataFactory) {
		super(daoMetaDataFactory);
	}
	
	public static String getEmployee_ARGS = "empno";

	public List getEmployees(String ename) {
		return getEntityManager().find("ename LIKE ?", "%" + ename + "%");
	}
}