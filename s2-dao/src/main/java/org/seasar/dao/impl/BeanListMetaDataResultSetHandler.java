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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.PropertyModifiedSupport;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.RelationRowCreator;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.PropertyDesc;

public class BeanListMetaDataResultSetHandler extends
        AbstractBeanMetaDataResultSetHandler {

    public BeanListMetaDataResultSetHandler(BeanMetaData beanMetaData, RelationRowCreator relationRowCreator) {
        super(beanMetaData, relationRowCreator);
    }

    /**
     * @see org.seasar.extension.jdbc.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet rs) throws SQLException {
        Set columnNames = createColumnNames(rs.getMetaData());
        List list = new ArrayList();
        int relSize = getBeanMetaData().getRelationPropertyTypeSize();
        RelationRowCache relRowCache = new RelationRowCache(relSize);
        while (rs.next()) {
            Object row = createRow(rs, columnNames);
            for (int i = 0; i < relSize; ++i) {
                RelationPropertyType rpt = getBeanMetaData()
                        .getRelationPropertyType(i);
                if (rpt == null) {
                    continue;
                }
                Object relRow = null;
                Map relKeyValues = new HashMap();
                RelationKey relKey = createRelationKey(rs, rpt, columnNames,
                        relKeyValues);
                if (relKey != null) {
                    relRow = relRowCache.getRelationRow(i, relKey);
                    if (relRow == null) {
                        relRow = createRelationRow(rs, rpt, columnNames,
                                relKeyValues);
                        relRowCache.addRelationRow(i, relKey, relRow);
                    }
                }
                if (relRow != null) {
                    PropertyDesc pd = rpt.getPropertyDesc();
                    pd.setValue(row, relRow);
                }
            }
            if (row instanceof PropertyModifiedSupport) {
                ((PropertyModifiedSupport) row).getModifiedProperties().clear();
            }
            list.add(row);
        }
        return list;
    }

    protected RelationKey createRelationKey(ResultSet rs,
            RelationPropertyType rpt, Set columnNames, Map relKeyValues)
            throws SQLException {

        List keyList = new ArrayList();
        BeanMetaData bmd = rpt.getBeanMetaData();
        for (int i = 0; i < rpt.getKeySize(); ++i) {
            /*
             * PropertyType pt = bmd
             * .getPropertyTypeByColumnName(rpt.getYourKey(i)); ValueType
             * valueType = pt.getValueType(); String columnName =
             * pt.getColumnName() + "_" + rpt.getRelationNo();
             */
            ValueType valueType = null;
            String columnName = rpt.getMyKey(i);
            if (columnNames.contains(columnName)) {
                PropertyType pt = getBeanMetaData()
                        .getPropertyTypeByColumnName(columnName);
                valueType = pt.getValueType();
            } else {
                PropertyType pt = bmd.getPropertyTypeByColumnName(rpt
                        .getYourKey(i));
                columnName = pt.getColumnName() + "_" + rpt.getRelationNo();
                if (columnNames.contains(columnName)) {
                    valueType = pt.getValueType();
                } else {
                    return null;
                }
            }
            Object value = valueType.getValue(rs, columnName);
            if (value == null) {
                return null;
            }
            relKeyValues.put(columnName, value);
            keyList.add(value);
        }
        if (keyList.size() > 0) {
            Object[] keys = keyList.toArray();
            return new RelationKey(keys);
        } else {
            return null;
        }
    }
}