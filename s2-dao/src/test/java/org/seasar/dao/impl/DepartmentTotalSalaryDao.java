package org.seasar.dao.impl;

import java.util.List;

public interface DepartmentTotalSalaryDao {

	public Class BEAN = DepartmentTotalSalary.class;

	public List getTotalSalaries();
}
