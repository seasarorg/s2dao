package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author higa
 *  
 */
public abstract class AbstractStaticCommand extends AbstractSqlCommand {

	private BeanMetaData beanMetaData_;

	public AbstractStaticCommand(DataSource dataSource,
			StatementFactory statementFactory,
			BeanMetaData beanMetaData) {

		super(dataSource, statementFactory);
		beanMetaData_ = beanMetaData;
	}

	public BeanMetaData getBeanMetaData() {
		return beanMetaData_;
	}
}