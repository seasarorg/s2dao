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
import java.util.Map;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.RelationRowCreator;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.ClassUtil;

/**
 * @author jflute
 */
public class RelationRowCreatorImpl implements RelationRowCreator {

    /**
     * @param rs Result set. (NotNull)
     * @param rpt The type of relation property. (NotNull)
     * @param columnNames The set of column name. (NotNull)
     * @param relKeyValues The map of rel key values. (Nullable)
     * @param relationPropertyCache The map of relation property cache. The key is String(relationNoSuffix) and the value is Set(PropertyType). (NotNull)
     * @return Created relation row. (Nullable)
     * @throws SQLException
     */
    public Object createRelationRow(ResultSet rs, RelationPropertyType rpt,
            Set columnNames, Map relKeyValues, Map relationPropertyCache)
            throws SQLException {
        final Object row = setupRelationKeyValue(rpt, columnNames, relKeyValues);
        return setupRelationAllValue(row, rs, rpt, columnNames, relKeyValues,
                relationPropertyCache);
    }

    protected Object setupRelationKeyValue(RelationPropertyType rpt,
            Set columnNames, Map relKeyValues) {
        Object row = null;
        final BeanMetaData bmd = rpt.getBeanMetaData();
        for (int i = 0; i < rpt.getKeySize(); ++i) {
            String columnName = rpt.getMyKey(i);
            if (columnNames.contains(columnName)) {
                if (row == null) {
                    row = createRelationRow(rpt);
                }
                if (relKeyValues != null
                        && relKeyValues.containsKey(columnName)) {
                    Object value = relKeyValues.get(columnName);
                    PropertyType pt = bmd.getPropertyTypeByColumnName(rpt
                            .getYourKey(i));
                    PropertyDesc pd = pt.getPropertyDesc();
                    if (value != null) {
                        pd.setValue(row, value);
                    }
                }
            }
            continue;
        }
        return row;
    }

    protected Object setupRelationAllValue(Object row, ResultSet rs,
            RelationPropertyType rpt, Set columnNames, Map relKeyValues,
            Map relationPropertyCache) throws SQLException {
        final String relationNoSuffix = buildRelationNoSuffix(rpt);
        final RelationRowCreationResource resource = createRelationRowCreationResource(
                row, rs, rpt, columnNames, relKeyValues, relationPropertyCache,
                relationNoSuffix);
        return setupRelationAllValue(resource);
    }

    protected String buildRelationNoSuffix(RelationPropertyType rpt) {
        return "_" + rpt.getRelationNo();
    }

    protected RelationRowCreationResource createRelationRowCreationResource(
            Object row, ResultSet rs, RelationPropertyType rpt,
            Set columnNames, Map relKeyValues, Map relationPropertyCache,
            String relationNoSuffix) throws SQLException {
        final RelationRowCreationResource resource = new RelationRowCreationResource();
        resource.setResultSet(rs);
        resource.setRow(row);
        resource.setRelationPropertyType(rpt);
        resource.setColumnNames(columnNames);
        resource.setRelKeyValues(relKeyValues);
        resource.setRelationPropertyCache(relationPropertyCache);
        resource.setRelationNoSuffix(relationNoSuffix);
        return resource;
    }

    protected Object setupRelationAllValue(RelationRowCreationResource resource)
            throws SQLException {
        if (resource.hasRelationPropertyCache()) {
            // - - - - - - - - - - - - - -
            // Using relationPropertyCache
            // - - - - - - - - - - - - - -
            final Set propertyCache = resource
                    .extractRelationTargetPropertyListFromCache();
            for (final Iterator ite = propertyCache.iterator(); ite.hasNext();) {
                final PropertyType pt = (PropertyType) ite.next();
                resource.setCurrentPropertyType(pt);
                if (!isTargetProperty(resource)) {
                    continue;
                }
                if (!isValidRelation(resource)) {
                    return null;
                }

                // 既にCacheを利用しているので、Cacheの保存はしないでSetupする。
                final boolean saveCache = false;
                setupRelationProperty(resource, saveCache);
            }
        } else {
            // - - - - - - - - - - - - - -
            // Using relationBeanMetaData
            // - - - - - - - - - - - - - -
            final BeanMetaData bmd = resource.getRelationBeanMetaData();

            // Cacheの初期化をする。この時点では空っぽである。
            // Cacheするべきものが一つも無い場合でも、
            // 「一つも無い」という状態がCacheされることになる。。
            resource.initializeRelationPropertyCache();

            for (int i = 0; i < bmd.getPropertyTypeSize(); ++i) {
                final PropertyType pt = bmd.getPropertyType(i);
                resource.setCurrentPropertyType(pt);
                if (!isTargetProperty(resource)) {
                    continue;
                }
                if (!isValidRelation(resource)) {
                    // Cacheするのに有効なRecordではない(全てのPropertyを処理しない)ため、
                    // 作りかけのCacheはClearして、次回Request時に再度Cacheを作る。
                    resource.clearRelationPropertyCache();
                    return null;
                }

                // Cacheの保存はしながらSetupする。
                final boolean saveCache = true;
                setupRelationProperty(resource, saveCache);
            }
        }
        return getRowIfExistsColumn(resource);
    }

    protected boolean isTargetProperty(RelationRowCreationResource resource)
            throws SQLException {
        final PropertyType pt = resource.getCurrentPropertyType();
        return pt.getPropertyDesc().hasWriteMethod();
    }

    protected boolean isValidRelation(RelationRowCreationResource resource)
            throws SQLException {
        return true;// Always true as default. This method is for extension(for override).
    }

    protected Object getRowIfExistsColumn(RelationRowCreationResource resource)
            throws SQLException {
        return resource.getRowIfExistsColumn();
    }

    protected void setupRelationProperty(RelationRowCreationResource resource,
            boolean saveCache) throws SQLException {
        final String columnName = resource.buildRelationColumnName();
        if (!resource.containsColumnName(columnName)) {
            return;
        }
        resource.incrementExistColumn();
        if (saveCache) {
            resource.saveRelationPropertyCache();
        }
        if (!resource.hasRowInstance()) {
            resource.setRow(createRelationRow(resource));
        }
        registerRelationValue(resource, columnName);
    }

    protected void registerRelationValue(RelationRowCreationResource resource,
            String columnName) throws SQLException {
        final PropertyType pt = resource.getCurrentPropertyType();
        Object value = null;
        if (resource.existsRelKeyValues()
                && resource.containsRelKeyValue(columnName)) {
            value = resource.extractRelKeyValue(columnName);
        } else {
            final ValueType valueType = pt.getValueType();
            value = valueType.getValue(resource.getResultSet(), columnName);
        }
        if (value != null) {
            final PropertyDesc pd = pt.getPropertyDesc();
            pd.setValue(resource.getRow(), value);
        }
    }

    protected Object createRelationRow(RelationRowCreationResource resource) {
        final RelationPropertyType rpt = resource.getRelationPropertyType();
        return ClassUtil.newInstance(rpt.getPropertyDesc().getPropertyType());
    }

    protected Object createRelationRow(RelationPropertyType rpt) {
        return ClassUtil.newInstance(rpt.getPropertyDesc().getPropertyType());
    }
}
