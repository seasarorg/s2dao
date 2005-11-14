package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author higa
 *  
 */
public abstract class AbstractBatchAutoStaticCommand extends
		AbstractAutoStaticCommand {

	public AbstractBatchAutoStaticCommand(DataSource dataSource,
			StatementFactory statementFactory,
			BeanMetaData beanMetaData, String[] propertyNames) {

		super(dataSource, statementFactory, beanMetaData, propertyNames);
	}

	public Object execute(Object[] args) {
		AbstractAutoHandler handler = createAutoHandler();
		handler.setSql(getSql());
		int updatedRows = handler.execute(args);
		return new Integer(updatedRows);
	}
}