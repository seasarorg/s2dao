package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author higa
 *  
 */
public class DeleteAutoStaticCommand extends AbstractAutoStaticCommand {

	public DeleteAutoStaticCommand(DataSource dataSource,
			StatementFactory statementFactory,
			BeanMetaData beanMetaData, String[] propertyNames) {

		super(dataSource, statementFactory, beanMetaData, propertyNames);
	}

	protected AbstractAutoHandler createAutoHandler() {
		return new DeleteAutoHandler(getDataSource(),
				getStatementFactory(), getBeanMetaData(),
				getPropertyTypes());
	}
	
	protected void setupSql() {
		setupDeleteSql();
	}

	protected void setupPropertyTypes(String[] propertyNames) {
		setupDeletePropertyTypes(propertyNames);
		
	}
}