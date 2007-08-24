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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RowCreator;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author jflute
 */
public class RowCreatorImpl implements RowCreator {

    /**
     * @param rs Result set. (NotNull)
     * @param columnNames The set of column name. (NotNull)
     * @param beanMetaData Bean meta data. (NotNull)
     * @param propertyCache The set of property cache. The element type of set is PropertyType. (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    public Object createRow(ResultSet rs, Set columnNames,
            BeanMetaData beanMetaData, Set propertyCache) throws SQLException {
        final Object row = ClassUtil.newInstance(beanMetaData.getBeanClass());
        if (!propertyCache.isEmpty()) {
            createRowUsingCache(rs, columnNames, propertyCache, row);
        } else {
            createRowUsingBeanMetaData(rs, columnNames, beanMetaData,
                    propertyCache, row);
        }
        return row;
    }

    protected void createRowUsingBeanMetaData(ResultSet rs, Set columnNames,
            BeanMetaData beanMetaData, Set propertyCache, final Object row)
            throws SQLException {
        for (int i = 0; i < beanMetaData.getPropertyTypeSize(); ++i) {
            PropertyType pt = beanMetaData.getPropertyType(i);
            setupProperty(rs, columnNames, row, pt, propertyCache);
        }
    }

    protected void createRowUsingCache(ResultSet rs, Set columnNames,
            Set propertyCache, Object row) throws SQLException {
        for (final Iterator ite = propertyCache.iterator(); ite.hasNext();) {
            final PropertyType pt = (PropertyType) ite.next();
            setupProperty(rs, columnNames, row, pt, propertyCache);
        }
    }

    protected void setupProperty(ResultSet rs, Set columnNames, Object row,
            PropertyType pt, Set propertyCache) throws SQLException {
        // If the property is not writable, the property is out of target!
        if (!pt.getPropertyDesc().hasWriteMethod()) {
            return;
        }
        if (columnNames.contains(pt.getColumnName())) {
            registerValue(rs, row, pt, pt.getColumnName(), propertyCache);
        } else if (columnNames.contains(pt.getPropertyName())) {
            registerValue(rs, row, pt, pt.getPropertyName(), propertyCache);
        } else if (!pt.isPersistent()) {
            setupNotPersistentProperty(rs, columnNames, row, pt, propertyCache);
        }
    }

    protected void setupNotPersistentProperty(ResultSet rs, Set columnNames,
            Object row, PropertyType pt, Set propertyCache) throws SQLException {
        for (Iterator iter = columnNames.iterator(); iter.hasNext();) {
            String columnName = (String) iter.next();
            String columnName2 = StringUtil.replace(columnName, "_", "");
            if (columnName2.equalsIgnoreCase(pt.getColumnName())) {
                registerValue(rs, row, pt, columnName, propertyCache);
                break;
            }
        }
    }

    protected void registerValue(ResultSet rs, Object row, PropertyType pt,
            String name, Set propertyCache) throws SQLException {
        ValueType valueType = pt.getValueType();
        Object value = valueType.getValue(rs, name);
        PropertyDesc pd = pt.getPropertyDesc();
        pd.setValue(row, value);

        // Add property type to cache as target.
        addPropertyCache(propertyCache, pt);
    }

    protected void addPropertyCache(Set propertyCache, PropertyType pt) {
        propertyCache.add(pt);
    }
}
