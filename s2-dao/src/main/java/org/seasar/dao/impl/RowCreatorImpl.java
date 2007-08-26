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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.DtoMetaData;
import org.seasar.dao.RowCreator;
import org.seasar.dao.util.DaoNamingConventionUtil;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author jflute
 */
public class RowCreatorImpl implements RowCreator {

    // ===================================================================================
    //                                                                        Row Creation
    //                                                                        ============
    /**
     * @param rs Result set. (NotNull)
     * @param propertyCache The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @param beanClass Bean class. (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    public Object createRow(ResultSet rs, Map propertyCache, Class beanClass)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Object row = newBean(beanClass);
        final Set columnNameSet = propertyCache.keySet();
        for (final Iterator ite = columnNameSet.iterator(); ite.hasNext();) {
            final String columnName = (String) ite.next();
            final PropertyType pt = (PropertyType) propertyCache
                    .get(columnName);
            registerValue(rs, row, pt, columnName);
        }
        return row;
    }

    protected Object newBean(Class beanClass) {
        return ClassUtil.newInstance(beanClass);
    }

    protected void registerValue(ResultSet rs, Object row, PropertyType pt,
            String name) throws SQLException {
        final ValueType valueType = pt.getValueType();
        final Object value = valueType.getValue(rs, name);
        final PropertyDesc pd = pt.getPropertyDesc();
        pd.setValue(row, value);
    }

    // ===================================================================================
    //                                                             Property Cache Creation
    //                                                             =======================
    // -----------------------------------------------------
    //                                                  Bean
    //                                                  ----
    /**
     * @param columnNames The set of column name. (NotNull)
     * @param beanMetaData Bean meta data. (NotNull)
     * @return The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @throws SQLException
     */
    public Map createPropertyCache(Set columnNames, BeanMetaData beanMetaData)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Map proprertyCache = newPropertyCache();
        setupPropertyCache(proprertyCache, columnNames, beanMetaData);
        return proprertyCache;
    }

    protected void setupPropertyCache(Map proprertyCache, Set columnNames,
            BeanMetaData beanMetaData) throws SQLException {
        for (int i = 0; i < beanMetaData.getPropertyTypeSize(); ++i) {
            PropertyType pt = beanMetaData.getPropertyType(i);
            setupPropertyCacheElement(proprertyCache, columnNames, pt);
        }
    }

    protected void setupPropertyCacheElement(Map proprertyCache,
            Set columnNames, PropertyType pt) throws SQLException {
        if (!isTargetProperty(pt)) {
            return;
        }
        if (columnNames.contains(pt.getColumnName())) {
            proprertyCache.put(pt.getColumnName(), pt);
        } else if (columnNames.contains(pt.getPropertyName())) {
            proprertyCache.put(pt.getPropertyName(), pt);
        } else if (!pt.isPersistent()) {
            setupPropertyCacheNotPersistentElement(proprertyCache, columnNames,
                    pt);
        }
    }

    protected void setupPropertyCacheNotPersistentElement(Map proprertyCache,
            Set columnNames, PropertyType pt) throws SQLException {
        for (Iterator iter = columnNames.iterator(); iter.hasNext();) {
            String columnName = (String) iter.next();
            String columnName2 = StringUtil.replace(columnName, "_", "");
            if (columnName2.equalsIgnoreCase(pt.getColumnName())) {
                proprertyCache.put(columnName, pt);
                break;
            }
        }
    }

    // -----------------------------------------------------
    //                                                   Dto
    //                                                   ---
    /**
     * @param columnNames The set of column name. (NotNull)
     * @param dtoMetaData Dto meta data. (NotNull)
     * @return The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @throws SQLException
     */
    public Map createPropertyCache(Set columnNames, DtoMetaData dtoMetaData)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Map proprertyCache = newPropertyCache();
        setupPropertyCache(proprertyCache, columnNames, dtoMetaData);
        return proprertyCache;
    }

    protected void setupPropertyCache(Map proprertyCache, Set columnNames,
            DtoMetaData dtoMetaData) throws SQLException {
        for (int i = 0; i < dtoMetaData.getPropertyTypeSize(); ++i) {
            PropertyType pt = dtoMetaData.getPropertyType(i);
            if (!isTargetProperty(pt)) {
                return;
            }
            if (columnNames.contains(pt.getColumnName())) {
                proprertyCache.put(pt.getColumnName(), pt);
            } else if (columnNames.contains(pt.getPropertyName())) {
                proprertyCache.put(pt.getPropertyName(), pt);
            } else {
                String possibleName = DaoNamingConventionUtil
                        .fromPropertyNameToColumnName(pt.getPropertyName());
                if (columnNames.contains(possibleName)) {
                    proprertyCache.put(possibleName, pt);
                }
            }
        }
    }

    // -----------------------------------------------------
    //                                                Common
    //                                                ------
    protected Map newPropertyCache() {
        return new HashMap();
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    protected boolean isTargetProperty(PropertyType pt) throws SQLException {
        // - - - - - - - - - - - - - - - - - - - - - - - -
        // Extension Point!
        //  --> 該当のPropertyを処理対象とするか否か。
        // - - - - - - - - - - - - - - - - - - - - - - - -
        // If the property is not writable, the property is out of target!
        return pt.getPropertyDesc().hasWriteMethod();
    }
}
