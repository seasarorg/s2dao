/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.RelationRowCreator;
import org.seasar.dao.RowCreator;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;

/**
 * @author jflute
 */
public abstract class AbstractBeanMetaDataResultSetHandler extends
        AbstractDtoMetaDataResultSetHandler {

    private BeanMetaData beanMetaData;

    protected RelationRowCreator relationRowCreator;

    /**
     * @param dtoMetaData
     *            Dto meta data. (NotNull)
     * @param rowCreator
     *            Row creator. (NotNull)
     * @param relationRowCreator
     *            Relation row creator. (NotNul)
     */
    public AbstractBeanMetaDataResultSetHandler(BeanMetaData beanMetaData,
            RowCreator rowCreator, RelationRowCreator relationRowCreator) {
        super(beanMetaData, rowCreator);
        this.beanMetaData = beanMetaData;
        this.relationRowCreator = relationRowCreator;
    }

    /**
     * @param columnNames
     *            The set of column name. (NotNull)
     * @return The map of row property cache. Map{String(columnName),
     *         PropertyType} (NotNull)
     * @throws SQLException
     */
    protected Map createPropertyCache(Set columnNames) throws SQLException {
        // - - - - - - - - -
        // Override for Bean
        // - - - - - - - - -
        return rowCreator.createPropertyCache(columnNames, beanMetaData);
    }

    /**
     * @param rs
     *            Result set. (NotNull)
     * @param propertyCache
     *            The map of property cache. Map{String(columnName),
     *            PropertyType} (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    protected Object createRow(ResultSet rs, Map propertyCache)
            throws SQLException {
        // - - - - - - - - -
        // Override for Bean
        // - - - - - - - - -
        final Class beanClass = beanMetaData.getBeanClass();
        return rowCreator.createRow(rs, propertyCache, beanClass);
    }

    /**
     * @param columnNames
     *            The set of column name. (NotNull)
     * @return The map of relation property cache. Map{String(relationNoSuffix),
     *         Map{String(columnName), PropertyType}} (NotNull)
     * @throws SQLException
     */
    protected Map createRelationPropertyCache(Set columnNames)
            throws SQLException {
        return relationRowCreator
                .createPropertyCache(columnNames, beanMetaData);
    }

    /**
     * @param rs
     *            Result set. (NotNull)
     * @param rpt
     *            The type of relation property. (NotNull)
     * @param columnNames
     *            The set of column name. (NotNull)
     * @param relKeyValues
     *            The map of rel key values. (Nullable)
     * @param relationPropertyCache
     *            The map of relation property cache.
     *            Map{String(relationNoSuffix), Map{String(columnName),
     *            PropertyType}} (NotNull)
     * @return Created relation row. (Nullable)
     * @throws SQLException
     */
    protected Object createRelationRow(ResultSet rs, RelationPropertyType rpt,
            Set columnNames, Map relKeyValues, Map relationPropertyCache)
            throws SQLException {
        return relationRowCreator.createRelationRow(rs, rpt, columnNames,
                relKeyValues, relationPropertyCache);
    }

    protected void postCreateRow(final Object row) {
        final BeanMetaData bmd = getBeanMetaData();
        final Set names = bmd.getModifiedPropertyNames(row);
        names.clear();
    }

    public BeanMetaData getBeanMetaData() {
        return beanMetaData;
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
