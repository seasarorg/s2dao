package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.SqlCommand;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author higa
 *  
 */
public abstract class AbstractSqlCommand implements SqlCommand {

	private DataSource dataSource_;
	
	private StatementFactory statementFactory_;

	private String sql_;
	
	private Class notSingleRowUpdatedExceptionClass_;

	public AbstractSqlCommand(DataSource dataSource,
			StatementFactory statementFactory) {
		
		dataSource_ = dataSource;
		statementFactory_ = statementFactory;
	}

	public DataSource getDataSource() {
		return dataSource_;
	}
	
	public StatementFactory getStatementFactory() {
		return statementFactory_;
	}

	public String getSql() {
		return sql_;
	}

	public void setSql(String sql) {
		sql_ = sql;
	}
	
	public Class getNotSingleRowUpdatedExceptionClass() {
		return notSingleRowUpdatedExceptionClass_;
	}
	
	public void setNotSingleRowUpdatedExceptionClass(Class notSingleRowUpdatedExceptionClass) {
		notSingleRowUpdatedExceptionClass_ = notSingleRowUpdatedExceptionClass;
	}
}