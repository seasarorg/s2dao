package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author higa
 *  
 */
public class UpdateBatchAutoStaticCommand extends
		AbstractBatchAutoStaticCommand {

	public UpdateBatchAutoStaticCommand(DataSource dataSource,
			StatementFactory statementFactory,
			BeanMetaData beanMetaData, String[] propertyNames) {

		super(dataSource, statementFactory, beanMetaData, propertyNames);
	}

	protected AbstractAutoHandler createAutoHandler() {
		return new UpdateBatchAutoHandler(getDataSource(),
				getStatementFactory(), getBeanMetaData(),
				getPropertyTypes());
	}

	protected void setupSql() {
		setupUpdateSql();
	}

	protected void setupPropertyTypes(String[] propertyNames) {
		setupUpdatePropertyTypes(propertyNames);

	}
}