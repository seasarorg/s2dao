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
package org.seasar.dao.id;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;
import org.seasar.extension.jdbc.impl.ObjectResultSetHandler;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.exception.EmptyRuntimeException;

/**
 * @author higa
 *
 */
public abstract class AbstractIdentifierGenerator implements IdentifierGenerator {

	private static ResultSetHandler resultSetHandler_ = new ObjectResultSetHandler();

	private String propertyName_;
	
	private Dbms dbms_;
	
	public AbstractIdentifierGenerator(String propertyName, Dbms dbms) {
		propertyName_ = propertyName;
		dbms_ = dbms;
	}
	
	public String getPropertyName() {
		return propertyName_;
	}
	
	public Dbms getDbms() {
		return dbms_;
	}
	
	protected Object executeSql(DataSource ds, String sql, Object[] args) {
		BasicSelectHandler handler = new BasicSelectHandler(ds, sql, resultSetHandler_);
		return handler.execute(args);
	}
	
	protected void setIdentifier(Object bean, Object value) {
		if (propertyName_ == null) {
			throw new EmptyRuntimeException("propertyName");
		}
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
		PropertyDesc pd = beanDesc.getPropertyDesc(propertyName_);
		pd.setValue(bean, value);
	}
}