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

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.PrimaryKeyNotFoundRuntimeException;
import org.seasar.dao.SqlCommand;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author taichi
 * 
 */
public class UpdateAutoDynamicCommand extends UpdateDynamicCommand implements
		SqlCommand {

	/**
	 * 
	 */
	public UpdateAutoDynamicCommand(DataSource dataSource,
			StatementFactory statementFactory, BeanMetaData beanMetaData,
			String[] propertyNames) {
		super(dataSource, statementFactory);
		Class beanClass = beanMetaData.getBeanClass();
		String argName = beanMetaData.getTableName();
		setupSql(beanMetaData, argName, propertyNames);
		setArgNames(new String[] { argName });
		setArgTypes(new Class[] { beanClass });
	}

	protected void setupSql(BeanMetaData bmd, String argName,
			String[] propertyNames) {
		if (bmd.getPrimaryKeySize() == 0) {
			throw new PrimaryKeyNotFoundRuntimeException(bmd.getBeanClass());
		}
		StringBuffer buf = new StringBuffer(200);
		buf.append("UPDATE ");
		buf.append(bmd.getTableName());
		buf.append(" SET /*BEGIN*/");
		PropertyType[] propertyTypes = cretePropertyTypes(bmd, propertyNames);
		for (int i = 0; i < propertyTypes.length; ++i) {
			PropertyType pt = propertyTypes[i];
			if (pt.isPrimaryKey() || pt.isPersistent() == false) {
				continue;
			}
			buf.append("/*IF ");
			buf.append(argName);
			buf.append('.');
			buf.append(pt.getPropertyName());
			buf.append(" != null*/");
			buf.append(i == 0 ? "" : ",");
			buf.append(pt.getColumnName());
			buf.append(" = /*");
			buf.append(argName);
			buf.append('.');
			buf.append(pt.getPropertyName());
			buf.append("*//*END*/");
		}
		buf.append("/*END*/");
		setupUpdateWhere(buf, argName, bmd);
		setSql(buf.toString());
	}

	protected PropertyType[] cretePropertyTypes(BeanMetaData bmd,
			String[] propertyNames) {
		List types = new ArrayList();
		for (int i = 0; i < propertyNames.length; ++i) {
			PropertyType pt = bmd.getPropertyType(propertyNames[i]);
			if (pt.isPrimaryKey()
					&& !bmd.getIdentifierGenerator().isSelfGenerate()) {
				continue;
			}
			types.add(pt);
		}
		return (PropertyType[]) types.toArray(new PropertyType[types.size()]);

	}

	protected void setupUpdateWhere(StringBuffer buf, String argName,
			BeanMetaData bmd) {
		buf.append(" WHERE ");
		for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
			String column = bmd.getPrimaryKey(i);
			PropertyType pt = bmd.getPropertyTypeByColumnName(column);
			appendColumn(buf, argName, pt);
			buf.append(" AND ");
		}
		buf.setLength(buf.length() - 5);
		if (bmd.hasVersionNoPropertyType()) {
			PropertyType pt = bmd.getVersionNoPropertyType();
			buf.append(" AND ");
			appendColumn(buf, argName, pt);
		}
		if (bmd.hasTimestampPropertyType()) {
			PropertyType pt = bmd.getTimestampPropertyType();
			buf.append(" AND ");
			appendColumn(buf, argName, pt);
		}
	}

	private void appendColumn(StringBuffer buf, String argName, PropertyType pt) {
		buf.append(pt.getColumnName());
		buf.append(" = /*");
		buf.append(argName);
		buf.append('.');
		buf.append(pt.getPropertyName());
		buf.append("*/");
	}
}
