package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.CommandContext;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.SelectHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;

/**
 * @author higa
 *  
 */
public class SelectDynamicCommand extends AbstractDynamicCommand {

	private ResultSetHandler resultSetHandler_;

	private ResultSetFactory resultSetFactory_;

	public SelectDynamicCommand(DataSource dataSource,
			StatementFactory statementFactory,
			ResultSetHandler resultSetHandler, ResultSetFactory resultSetFactory) {

		super(dataSource, statementFactory);
		resultSetHandler_ = resultSetHandler;
		resultSetFactory_ = resultSetFactory;
	}

	public ResultSetHandler getResultSetHandler() {
		return resultSetHandler_;
	}

	public Object execute(Object[] args) {
		CommandContext ctx = apply(args);
		SelectHandler selectHandler = new BasicSelectHandler(getDataSource(),
				ctx.getSql(), resultSetHandler_, getStatementFactory(),
				resultSetFactory_);
		return selectHandler.execute(ctx.getBindVariables(), ctx.getBindVariableTypes());
	}
}