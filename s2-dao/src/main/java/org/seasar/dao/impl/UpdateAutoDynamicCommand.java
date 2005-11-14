package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.CommandContext;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.BasicUpdateHandler;

/**
 * @author higa
 *  
 */
public class UpdateAutoDynamicCommand extends AbstractDynamicCommand {

	public UpdateAutoDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
		super(dataSource, statementFactory);
	}

	public Object execute(Object[] args) {
		CommandContext ctx = apply(args);
		BasicUpdateHandler updateHandler = new BasicUpdateHandler(
				getDataSource(), ctx.getSql(), getStatementFactory());
		return new Integer(updateHandler.execute(ctx.getBindVariables(), ctx.getBindVariableTypes()));
	}

}