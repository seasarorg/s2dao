/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;
import org.seasar.extension.jdbc.PropertyType;

/**
 * @author jflute
 */
public class RelationRowCreationResource {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Result set. */
    private ResultSet resultSet;

    /** Relation row. Initialized at first or initialied after. */
    private Object row;

    /** Relation property type. */
    private RelationPropertyType relationPropertyType;

    /** The set of column name. */
    private Set columnNames;

    /** The map of rel key values. */
    private Map relKeyValues;

    /** The map of relation property cache. */
    private Map relationPropertyCache;// Map<String(relationNoSuffix), Map<String(columnName), PropertyType>>

    /** The suffix of base object. */
    private String baseSuffix;

    /** The suffix of relation no. */
    private String relationNoSuffix;

    /** The limit of relation nest leve. */
    private int limitRelationNestLevel;

    /** The current relation nest level. Default is one. */
    private int currentRelationNestLevel;

    /** Current property type. This variable is temporary. */
    private PropertyType currentPropertyType;

    /** The temporary variable for relation property type. */
    private RelationPropertyType tmpRelationPropertyType;

    /** The temporary variable for base suffix. */
    private String tmpBaseSuffix;

    /** The temporary variable for relation no suffix. */
    private String tmpRelationNoSuffix;

    /** The count of valid value. */
    private int validValueCount;

    /** Does it create dead link? */
    private boolean createDeadLink;

    // ===================================================================================
    //                                                                            Behavior
    //                                                                            ========
    // -----------------------------------------------------
    //                                                   row
    //                                                   ---
    public boolean hasRowInstance() {
        return row != null;
    }

    public void clearRowInstance() {
        row = null;
    }

    // -----------------------------------------------------
    //                                  relationPropertyType
    //                                  --------------------
    public BeanMetaData getRelationBeanMetaData() {
        return relationPropertyType.getBeanMetaData();
    }

    public boolean hasNextRelationProperty() {
        return getRelationBeanMetaData().getRelationPropertyTypeSize() > 0;
    }

    public void backupRelationPropertyType() {
        tmpRelationPropertyType = relationPropertyType;
    }

    public void restoreRelationPropertyType() {
        relationPropertyType = tmpRelationPropertyType;
    }

    // -----------------------------------------------------
    //                                           columnNames
    //                                           -----------
    public boolean containsColumnName(String columnName) {
        return columnNames.contains(columnName);
    }

    // -----------------------------------------------------
    //                                          relKeyValues
    //                                          ------------
    public boolean existsRelKeyValues() {
        return relKeyValues != null;
    }

    public boolean containsRelKeyValue(String key) {
        return relKeyValues.containsKey(key);
    }

    public boolean containsRelKeyValueIfExists(String key) {
        return existsRelKeyValues() && relKeyValues.containsKey(key);
    }

    public Object extractRelKeyValue(String key) {
        return relKeyValues.get(key);
    }

    // -----------------------------------------------------
    //                                 relationPropertyCache
    //                                 ---------------------
    // The type of relationPropertyCache is Map<String(relationNoSuffix), Map<String(columnName), PropertyType>>.
    public void initializePropertyCacheElement() {
        relationPropertyCache.put(relationNoSuffix, new HashMap());
    }

    public boolean hasPropertyCacheElement() {
        final Map propertyCacheElement = extractPropertyCacheElement();
        return propertyCacheElement != null && !propertyCacheElement.isEmpty();
    }

    public Map extractPropertyCacheElement() {
        return (Map) relationPropertyCache.get(relationNoSuffix);
    }

    public void savePropertyCacheElement() {
        if (!hasPropertyCacheElement()) {
            initializePropertyCacheElement();
        }
        final Map propertyCacheElement = extractPropertyCacheElement();
        final String columnName = buildRelationColumnName();
        if (propertyCacheElement.containsKey(columnName)) {
            return;
        }
        propertyCacheElement.put(columnName, currentPropertyType);
    }

    // -----------------------------------------------------
    //                                                suffix
    //                                                ------
    public String buildRelationColumnName() {
        return currentPropertyType.getColumnName() + relationNoSuffix;
    }

    public void addRelationNoSuffix(String additionalRelationNoSuffix) {
        relationNoSuffix = relationNoSuffix + additionalRelationNoSuffix;
    }

    public void backupSuffixAndPrepare(String baseSuffix,
            String additionalRelationNoSuffix) {
        backupBaseSuffix();
        backupRelationNoSuffix();
        this.baseSuffix = baseSuffix;
        addRelationNoSuffix(additionalRelationNoSuffix);
    }

    public void restoreSuffix() {
        restoreBaseSuffix();
        restoreRelationNoSuffix();
    }

    private void backupBaseSuffix() {
        tmpBaseSuffix = baseSuffix;
    }

    private void restoreBaseSuffix() {
        baseSuffix = tmpBaseSuffix;
    }

    private void backupRelationNoSuffix() {
        tmpRelationNoSuffix = relationNoSuffix;
    }

    private void restoreRelationNoSuffix() {
        relationNoSuffix = tmpRelationNoSuffix;
    }

    // -----------------------------------------------------
    //                                     relationNestLevel
    //                                     -----------------
    public boolean hasNextRelationLevel() {
        return currentRelationNestLevel < limitRelationNestLevel;
    }

    public void incrementCurrentRelationNestLevel() {
        ++currentRelationNestLevel;
    }

    public void decrementCurrentRelationNestLevel() {
        --currentRelationNestLevel;
    }

    // -----------------------------------------------------
    //                                       validValueCount
    //                                       ---------------
    public void incrementValidValueCount() {
        ++validValueCount;
    }

    public void clearValidValueCount() {
        validValueCount = 0;
    }

    public boolean hasValidValueCount() {
        return validValueCount > 0;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
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

    public Map getRelKeyValues() {
        return this.relKeyValues;
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

    public Map getRelationPropertyCache() {
        return relationPropertyCache;
    }

    public void setRelationPropertyCache(Map relationPropertyCache) {
        this.relationPropertyCache = relationPropertyCache;
    }

    public String getBaseSuffix() {
        return baseSuffix;
    }

    public void setBaseSuffix(String baseSuffix) {
        this.baseSuffix = baseSuffix;
    }

    public String getRelationNoSuffix() {
        return relationNoSuffix;
    }

    public void setRelationNoSuffix(String relationNoSuffix) {
        this.relationNoSuffix = relationNoSuffix;
    }

    public int getLimitRelationNestLevel() {
        return limitRelationNestLevel;
    }

    public void setLimitRelationNestLevel(int limitRelationNestLevel) {
        this.limitRelationNestLevel = limitRelationNestLevel;
    }

    public int getCurrentRelationNestLevel() {
        return currentRelationNestLevel;
    }

    public void setCurrentRelationNestLevel(int currentRelationNestLevel) {
        this.currentRelationNestLevel = currentRelationNestLevel;
    }

    public PropertyType getCurrentPropertyType() {
        return currentPropertyType;
    }

    public void setCurrentPropertyType(PropertyType propertyType) {
        this.currentPropertyType = propertyType;
    }

    public boolean isCreateDeadLink() {
        return createDeadLink;
    }

    public void setCreateDeadLink(boolean createDeadLink) {
        this.createDeadLink = createDeadLink;
    }
}
