/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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