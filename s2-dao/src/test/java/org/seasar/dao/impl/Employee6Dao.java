package org.seasar.dao.impl;


public interface Employee6Dao {

	public Class BEAN = Employee.class;
	
	public static String getEmployees_QUERY = "/*IF $dto.orderByString != null*/order by /*$dto.orderByString*/ENAME /*END*/";
	public Employee[] getEmployees(EmployeeSearchCondition dto);
}
