package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author higa
 *  
 */
public class InsertAutoHandler extends AbstractAutoHandler {

	public InsertAutoHandler(DataSource dataSource,
			StatementFactory statementFactory,
			BeanMetaData beanMetaData, PropertyType[] propertyTypes) {

		super(dataSource, statementFactory, beanMetaData, propertyTypes);
	}

	protected void setupBindVariables(Object bean) {
		setupInsertBindVariables(bean);
	}

	protected void preUpdateBean(Object bean) {
		IdentifierGenerator generator = getBeanMetaData()
				.getIdentifierGenerator();
		if (generator.isSelfGenerate()) {
			generator.setIdentifier(bean, getDataSource());
		}
	}

	protected void postUpdateBean(Object bean) {
		IdentifierGenerator generator = getBeanMetaData()
				.getIdentifierGenerator();
		if (!generator.isSelfGenerate()) {
			generator.setIdentifier(bean, getDataSource());
		}
		updateVersionNoIfNeed(bean);
		updateTimestampIfNeed(bean);
	}
}