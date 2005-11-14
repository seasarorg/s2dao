package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author higa
 *  
 */
public class InsertAutoStaticCommand extends AbstractAutoStaticCommand {

	public InsertAutoStaticCommand(DataSource dataSource,
			StatementFactory statementFactory,
			BeanMetaData beanMetaData, String[] propertyNames) {

		super(dataSource, statementFactory, beanMetaData, propertyNames);
	}

	protected AbstractAutoHandler createAutoHandler() {
		return new InsertAutoHandler(getDataSource(),
				getStatementFactory(), getBeanMetaData(),
				getPropertyTypes());
	}

	protected void setupSql() {
		setupInsertSql();
	}

	protected void setupPropertyTypes(String[] propertyNames) {
		setupInsertPropertyTypes(propertyNames);

	}
}