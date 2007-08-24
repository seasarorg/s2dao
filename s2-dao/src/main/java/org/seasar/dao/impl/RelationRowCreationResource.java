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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;
import org.seasar.extension.jdbc.PropertyType;

/**
 * @author jflute
 */
public class RelationRowCreationResource {

    // - - - - -
    // Attribute
    // - - - - -
    /** Result set. Initialized at first. */
    private ResultSet resultSet;

    /** Relation row. Initialized at first or initialied after. */
    private Object row;

    /** Relation property type. Initialized at first. */
    private RelationPropertyType relationPropertyType;

    /** The set of column name. Initialized at first. */
    private Set columnNames;

    /** The map of rel key values. Initialized at first. */
    private Map relKeyValues;

    /** The map of relation property cache. Initialized at first. */
    private Map relationPropertyCache;// Map<String(relationNoSuffix), Set<PropertyType>>

    /** The suffix of relation no. Initialized at first. */
    private String relationNoSuffix;

    /** Current property type. This variable is temporary. */
    private PropertyType currentPropertyType;

    /** The count of exist column. This variable is counter. */
    private int existColumn;

    // - - - - -
    // About row
    // - - - - -
    public boolean hasRowInstance() {
        return row != null;
    }

    public Object getRowIfExistsColumn() {
        if (!existsColumn()) {
            return null;
        }
        return row;
    }

    // - - - - - - - - - - - - - -
    // About relationPropertyType
    // - - - - - - - - - - - - - -
    public BeanMetaData getRelationBeanMetaData() {
        return relationPropertyType.getBeanMetaData();
    }

    // - - - - - - - - -
    // About columnNames
    // - - - - - - - - -
    public boolean containsColumnName(String columnName) {
        return columnNames.contains(columnName);
    }

    // - - - - - - - - - -
    // About relKeyValues
    // - - - - - - - - - -
    public boolean existsRelKeyValues() {
        return relKeyValues != null;
    }

    public boolean containsRelKeyValue(String key) {
        return relKeyValues.containsKey(key);
    }

    public Object extractRelKeyValue(String key) {
        return relKeyValues.get(key);
    }

    // - - - - - - - - - - - - - -
    // About relationPropertyCache
    // - - - - - - - - - - - - - -
    // The type of relationPropertyCache is Map<String(relationNoSuffix), Set<PropertyType>>.
    public boolean hasRelationPropertyCache() {
        return relationPropertyCache.containsKey(relationNoSuffix);
    }

    public Set extractRelationTargetPropertyListFromCache() {
        return (Set) relationPropertyCache.get(relationNoSuffix);
    }

    public void initializeRelationPropertyCache() {
        if (!hasRelationPropertyCache()) {
            relationPropertyCache.put(relationNoSuffix, new HashSet());
        }
    }

    public void clearRelationPropertyCache() {
        if (hasRelationPropertyCache()) {
            relationPropertyCache.remove(relationNoSuffix);
        }
    }

    public void saveRelationPropertyCache() {
        if (!hasRelationPropertyCache()) {
            initializeRelationPropertyCache();
        }
        final Set propertyCache = (Set) relationPropertyCache
                .get(relationNoSuffix);
        if (propertyCache.contains(currentPropertyType)) {
            return;
        }
        propertyCache.add(currentPropertyType);
    }

    // - - - - - - - - - - - -
    // About relationNoSuffix
    // - - - - - - - - - - - -
    public String buildRelationColumnName() {
        return currentPropertyType.getColumnName() + relationNoSuffix;
    }

    // - - - - - - - - -
    // About existColumn
    // - - - - - - - - -
    public void incrementExistColumn() {
        ++existColumn;
    }

    public boolean existsColumn() {
        return existColumn > 0;
    }

    // - - - - -
    // Accessor
    // - - - - -
    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public Set getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(Set columnNames) {
        this.columnNames = columnNames;
    }

    public void setRelKeyValues(Map relKeyValues) {
        this.relKeyValues = relKeyValues;
    }

    public Object getRow() {
        return row;
    }

    public void setRow(Object row) {
        this.row = row;
    }

    public RelationPropertyType getRelationPropertyType() {
        return relationPropertyType;
    }

    public void setRelationPropertyType(RelationPropertyType rpt) {
        this.relationPropertyType = rpt;
    }

    public String getRelationNoSuffix() {
        return relationNoSuffix;
    }

    public void setRelationNoSuffix(String relationNoSuffix) {
        this.relationNoSuffix = relationNoSuffix;
    }

    public PropertyType getCurrentPropertyType() {
        return currentPropertyType;
    }

    public void setCurrentPropertyType(PropertyType propertyType) {
        this.currentPropertyType = propertyType;
    }

    public Map getRelationPropertyCache() {
        return relationPropertyCache;
    }

    public void setRelationPropertyCache(Map relationPropertyCache) {
        this.relationPropertyCache = relationPropertyCache;
    }
}
