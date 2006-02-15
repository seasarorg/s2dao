package org.seasar.dao.impl;

import org.seasar.dao.SqlCommand;
import org.seasar.dao.handler.ProcedureHandler;

public class StaticStoredProcedureCommand implements SqlCommand {
	private ProcedureHandler handler;
	public StaticStoredProcedureCommand(ProcedureHandler handler) {
		this.handler = handler;
	}
	public Object execute(Object[] args) {
		return handler.execute(args);
	}

}
