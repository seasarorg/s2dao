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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.UpdateHandler;
import org.seasar.extension.jdbc.impl.BasicHandler;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.IntegerConversionUtil;
import org.seasar.framework.util.PreparedStatementUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * @author higa
 *  
 */
public abstract class AbstractAutoHandler extends BasicHandler implements
		UpdateHandler {

	private static Logger logger_ = Logger.getLogger(AbstractAutoHandler.class);

	private BeanMetaData beanMetaData_;

	private Object[] bindVariables_;

	private Class[] bindVariableTypes_;

	private Timestamp timestamp_;

	private Integer versionNo_;

	private PropertyType[] propertyTypes_;

	public AbstractAutoHandler(DataSource dataSource,
			StatementFactory statementFactory,
			BeanMetaData beanMetaData, PropertyType[] propertyTypes) {

		setDataSource(dataSource);
		setStatementFactory(statementFactory);
		beanMetaData_ = beanMetaData;
		propertyTypes_ = propertyTypes;
	}

	public BeanMetaData getBeanMetaData() {
		return beanMetaData_;
	}

	protected static Logger getLogger() {
		return logger_;
	}

	protected Object[] getBindVariables() {
		return bindVariables_;
	}

	protected void setBindVariables(Object[] bindVariables) {
		bindVariables_ = bindVariables;
	}

	protected Class[] getBindVariableTypes() {
		return bindVariableTypes_;
	}

	protected void setBindVariableTypes(Class[] types) {
		bindVariableTypes_ = types;
	}

	protected Timestamp getTimestamp() {
		return timestamp_;
	}

	protected void setTimestamp(Timestamp timestamp) {
		timestamp_ = timestamp;
	}

	protected Integer getVersionNo() {
		return versionNo_;
	}

	protected void setVersionNo(Integer versionNo) {
		versionNo_ = versionNo;
	}

	protected PropertyType[] getPropertyTypes() {
		return propertyTypes_;
	}

	protected void setPropertyTypes(PropertyType[] propertyTypes) {
		propertyTypes_ = propertyTypes;
	}

	public int execute(Object[] args) throws SQLRuntimeException {
		Connection connection = getConnection();
		try {
			return execute(connection, args[0]);
		} finally {
			ConnectionUtil.close(connection);
		}
	}

	public int execute(Object[] args, Class[] argTypes)
			throws SQLRuntimeException {
		return execute(args);
	}

	protected int execute(Connection connection, Object bean) {
		preUpdateBean(bean);
		setupBindVariables(bean);
		if (logger_.isDebugEnabled()) {
			logger_.debug(getCompleteSql(bindVariables_));
		}
		PreparedStatement ps = prepareStatement(connection);
		int ret = -1;
		try {
			bindArgs(ps, bindVariables_, bindVariableTypes_);
			ret = PreparedStatementUtil.executeUpdate(ps);
		} finally {
			StatementUtil.close(ps);
		}
		postUpdateBean(bean);
		return ret;
	}

	protected void preUpdateBean(Object bean) {
	}

	protected void postUpdateBean(Object bean) {
	}

	protected abstract void setupBindVariables(Object bean);

	protected void setupInsertBindVariables(Object bean) {
		List varList = new ArrayList();
		List varTypeList = new ArrayList();
		for (int i = 0; i < propertyTypes_.length; ++i) {
			PropertyType pt = propertyTypes_[i];
			if (pt.getPropertyName().equalsIgnoreCase(
					getBeanMetaData().getTimestampPropertyName())) {
				setTimestamp(new Timestamp(new Date().getTime()));
				varList.add(getTimestamp());
			} else if (pt.getPropertyName().equals(
					getBeanMetaData().getVersionNoPropertyName())) {
				setVersionNo(new Integer(0));
				varList.add(getVersionNo());
			} else {
				varList.add(pt.getPropertyDesc().getValue(bean));
			}
			varTypeList.add(pt.getPropertyDesc().getPropertyType());
		}
		setBindVariables(varList.toArray());
		setBindVariableTypes((Class[]) varTypeList
				.toArray(new Class[varTypeList.size()]));
	}

	protected void setupUpdateBindVariables(Object bean) {
		List varList = new ArrayList();
		List varTypeList = new ArrayList();
		for (int i = 0; i < propertyTypes_.length; ++i) {
			PropertyType pt = propertyTypes_[i];
			if (pt.getPropertyName().equalsIgnoreCase(
					getBeanMetaData().getTimestampPropertyName())) {
				setTimestamp(new Timestamp(new Date().getTime()));
				varList.add(getTimestamp());
			} else if (pt.getPropertyName().equals(
					getBeanMetaData().getVersionNoPropertyName())) {
				Object value = pt.getPropertyDesc().getValue(bean);
				int intValue = IntegerConversionUtil.toPrimitiveInt(value) + 1;
				setVersionNo(new Integer(intValue));
				varList.add(getVersionNo());
			} else {
				varList.add(pt.getPropertyDesc().getValue(bean));
			}
			varTypeList.add(pt.getPropertyDesc().getPropertyType());
		}
		addAutoUpdateWhereBindVariables(varList, varTypeList, bean);
		setBindVariables(varList.toArray());
		setBindVariableTypes((Class[]) varTypeList
				.toArray(new Class[varTypeList.size()]));
	}

	protected void setupDeleteBindVariables(Object bean) {
		List varList = new ArrayList();
		List varTypeList = new ArrayList();
		addAutoUpdateWhereBindVariables(varList, varTypeList, bean);
		setBindVariables(varList.toArray());
		setBindVariableTypes((Class[]) varTypeList
				.toArray(new Class[varTypeList.size()]));
	}

	protected void addAutoUpdateWhereBindVariables(List varList,
			List varTypeList, Object bean) {
		BeanMetaData bmd = getBeanMetaData();
		for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
			PropertyType pt = bmd.getPropertyTypeByColumnName(bmd
					.getPrimaryKey(i));
			PropertyDesc pd = pt.getPropertyDesc();
			varList.add(pd.getValue(bean));
			varTypeList.add(pd.getPropertyType());
		}
		if (bmd.hasVersionNoPropertyType()) {
			PropertyType pt = bmd.getVersionNoPropertyType();
			PropertyDesc pd = pt.getPropertyDesc();
			varList.add(pd.getValue(bean));
			varTypeList.add(pd.getPropertyType());
		}
		if (bmd.hasTimestampPropertyType()) {
			PropertyType pt = bmd.getTimestampPropertyType();
			PropertyDesc pd = pt.getPropertyDesc();
			varList.add(pd.getValue(bean));
			varTypeList.add(pd.getPropertyType());
		}
	}

	protected void updateTimestampIfNeed(Object bean) {
		if (getTimestamp() != null) {
			PropertyDesc pd = getBeanMetaData().getTimestampPropertyType()
					.getPropertyDesc();
			pd.setValue(bean, getTimestamp());
		}
	}

	protected void updateVersionNoIfNeed(Object bean) {
		if (getVersionNo() != null) {
			PropertyDesc pd = getBeanMetaData().getVersionNoPropertyType()
					.getPropertyDesc();
			pd.setValue(bean, getVersionNo());
		}
	}
}