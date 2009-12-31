/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.exception.EmptyRuntimeException;

/**
 * @author higa
 * 
 */
public abstract class AbstractIdentifierGenerator implements
        IdentifierGenerator {

    protected ResultSetHandler resultSetHandler;

    protected PropertyType propertyType;

    protected Dbms dbms;

    public AbstractIdentifierGenerator(PropertyType propertyType, Dbms dbms) {
        this.propertyType = propertyType;
        this.dbms = dbms;
        resultSetHandler = new IdentifierResultSetHandler(propertyType
                .getValueType());
    }

    public String getPropertyName() {
        return propertyType.getPropertyName();
    }

    public Dbms getDbms() {
        return dbms;
    }

    protected Object executeSql(DataSource ds, String sql, Object[] args) {
        BasicSelectHandler handler = new BasicSelectHandler(ds, sql,
                resultSetHandler);
        // [DAO-139]
        handler.setFetchSize(-1);
        return handler.execute(args);
    }

    protected void setIdentifier(Object bean, Object value) {
        if (propertyType == null) {
            throw new EmptyRuntimeException("propertyType");
        }
        PropertyDesc pd = propertyType.getPropertyDesc();
        pd.setValue(bean, value);
    }
}