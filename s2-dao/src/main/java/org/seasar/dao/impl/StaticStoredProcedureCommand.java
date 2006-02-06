package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.SqlCommand;
import org.seasar.dao.handler.BasicProcedureHandler;
import org.seasar.dao.handler.ProcedureHandler;

public class StaticStoredProcedureCommand implements SqlCommand {
	private ProcedureHandler handler;
	public StaticStoredProcedureCommand(DataSource dataSource,String storedProcedureName) {
		handler = new BasicProcedureHandler(dataSource,storedProcedureName);
	}
	public Object execute(Object[] args) {
		return handler.execute(args);
	}

}
