/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
import org.seasar.dao.IdentifierGenerator;
import org.seasar.dao.NotSingleRowUpdatedRuntimeException;
import org.seasar.dao.PrimaryKeyNotFoundRuntimeException;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.framework.exception.SRuntimeException;

/**
 * @author higa
 * 
 */
public abstract class AbstractAutoStaticCommand extends AbstractStaticCommand {

    private PropertyType[] propertyTypes;

    public AbstractAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, BeanMetaData beanMetaData,
            String[] propertyNames) {

        super(dataSource, statementFactory, beanMetaData);
        setupPropertyTypes(propertyNames);
        setupSql();
    }

    public Object execute(Object[] args) {
        AbstractAutoHandler handler = createAutoHandler();
        handler.setSql(getSql());
        injectDaoClass(handler);
        int rows = handler.execute(args);
        if (rows != 1) {
            throw new NotSingleRowUpdatedRuntimeException(args[0], rows);
        }
        return new Integer(rows);
    }

    protected PropertyType[] getPropertyTypes() {
        return propertyTypes;
    }

    protected void setPropertyTypes(PropertyType[] propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    protected abstract AbstractAutoHandler createAutoHandler();

    protected abstract void setupPropertyTypes(String[] propertyNames);

    protected void setupInsertPropertyTypes(String[] propertyNames) {
        List types = new ArrayList();
        for (int i = 0; i < propertyNames.length; ++i) {
            PropertyType pt = getBeanMetaData().getPropertyType(
                    propertyNames[i]);
            if (isInsertTarget(pt)) {
                types.add(pt);
            }
        }
        propertyTypes = (PropertyType[]) types.toArray(new PropertyType[types
                .size()]);
    }

    protected boolean isInsertTarget(PropertyType propertyType) {
        if (propertyType.isPrimaryKey()) {
            String name = propertyType.getPropertyName();
            IdentifierGenerator generator = getBeanMetaData()
                    .getIdentifierGenerator(name);
            return generator.isSelfGenerate();
        }
        return true;
    }

    protected void setupUpdatePropertyTypes(String[] propertyNames) {
        List types = new ArrayList();
        for (int i = 0; i < propertyNames.length; ++i) {
            PropertyType pt = getBeanMetaData().getPropertyType(
                    propertyNames[i]);
            if (pt.isPrimaryKey()) {
                continue;
            }
            types.add(pt);
        }
        if (types.size() == 0) {
            throw new SRuntimeException("EDAO0020");
        }
        propertyTypes = (PropertyType[]) types.toArray(new PropertyType[types
                .size()]);
    }

    protected void setupDeletePropertyTypes(String[] propertyNames) {
    }

    protected abstract void setupSql();

    protected void setupInsertSql() {
        BeanMetaData bmd = getBeanMetaData();
        StringBuffer buf = new StringBuffer(100);
        buf.append("INSERT INTO ");
        buf.append(bmd.getTableName());
        buf.append(" (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            if (isInsertTarget(pt)) {
                buf.append(pt.getColumnName());
                buf.append(", ");
            }
        }
        buf.setLength(buf.length() - 2);
        buf.append(") VALUES (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            if (isInsertTarget(pt)) {
                buf.append("?, ");
            }
        }
        buf.setLength(buf.length() - 2);
        buf.append(")");
        setSql(buf.toString());
    }

    protected void setupUpdateSql() {
        checkPrimaryKey();
        StringBuffer buf = new StringBuffer(100);
        buf.append("UPDATE ");
        buf.append(getBeanMetaData().getTableName());
        buf.append(" SET ");
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            buf.append(pt.getColumnName());
            buf.append(" = ?, ");
        }
        buf.setLength(buf.length() - 2);
        setupUpdateWhere(buf);
        setSql(buf.toString());
    }

    protected void setupDeleteSql() {
        checkPrimaryKey();
        StringBuffer buf = new StringBuffer(100);
        buf.append("DELETE FROM ");
        buf.append(getBeanMetaData().getTableName());
        setupUpdateWhere(buf);
        setSql(buf.toString());
    }

    protected void checkPrimaryKey() {
        BeanMetaData bmd = getBeanMetaData();
        if (bmd.getPrimaryKeySize() == 0) {
            throw new PrimaryKeyNotFoundRuntimeException(bmd.getBeanClass());
        }
    }

    protected void setupUpdateWhere(StringBuffer buf) {
        BeanMetaData bmd = getBeanMetaData();
        buf.append(" WHERE ");
        for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
            buf.append(bmd.getPrimaryKey(i));
            buf.append(" = ? AND ");
        }
        buf.setLength(buf.length() - 5);
        if (bmd.hasVersionNoPropertyType()) {
            PropertyType pt = bmd.getVersionNoPropertyType();
            buf.append(" AND ");
            buf.append(pt.getColumnName());
            buf.append(" = ?");
        }
        if (bmd.hasTimestampPropertyType()) {
            PropertyType pt = bmd.getTimestampPropertyType();
            buf.append(" AND ");
            buf.append(pt.getColumnName());
            buf.append(" = ?");
        }
    }
}