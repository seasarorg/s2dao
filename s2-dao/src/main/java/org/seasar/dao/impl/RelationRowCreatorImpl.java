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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.RelationRowCreator;
import org.seasar.dao.util.PropertyDescUtil;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.ClassUtil;

/**
 * @author jflute
 */
public class RelationRowCreatorImpl implements RelationRowCreator {

    // ===================================================================================
    //                                                                        Row Creation
    //                                                                        ============
    /**
     * @param rs Result set. (NotNull)
     * @param rpt The type of relation property. (NotNull)
     * @param columnNames The set of column name. (NotNull)
     * @param relKeyValues The map of relation key values. (Nullable)
     * @param relationPropertyCache The map of relation property cache. Map{String(relationNoSuffix), Map{String(columnName), PropertyType}} (NotNull)
     * @return Created relation row. (Nullable)
     * @throws SQLException
     */
    public Object createRelationRow(ResultSet rs, RelationPropertyType rpt,
            Set columnNames, Map relKeyValues, Map relationPropertyCache)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final RelationRowCreationResource res = createResourceForRow(rs, rpt,
                columnNames, relKeyValues, relationPropertyCache);
        return createRelationRow(res);
    }

    protected RelationRowCreationResource createResourceForRow(ResultSet rs,
            RelationPropertyType rpt, Set columnNames, Map relKeyValues,
            Map relationPropertyCache) throws SQLException {
        final RelationRowCreationResource res = new RelationRowCreationResource();
        res.setResultSet(rs);
        res.setRelationPropertyType(rpt);
        res.setColumnNames(columnNames);
        res.setRelKeyValues(relKeyValues);
        res.setRelationPropertyCache(relationPropertyCache);
        res.setBaseSuffix("");// as Default
        res.setRelationNoSuffix(buildRelationNoSuffix(rpt));
        res.setLimitRelationNestLevel(getLimitRelationNestLevel());
        res.setCurrentRelationNestLevel(1);// as Default
        res.setCreateDeadLink(isCreateDeadLink());
        return res;
    }

    /**
     * @param res The resource of relation row creation. (NotNull)
     * @return Created relation row. (Nullable)
     * @throws SQLException
     */
    protected Object createRelationRow(RelationRowCreationResource res)
            throws SQLException {
        // - - - - - - - - - - - 
        // Recursive Call Point!
        // - - - - - - - - - - -

        // Select句に該当RelationのPropertyが一つも指定されていない場合は、
        // この時点ですぐにreturn null;とする。以前のS2Daoの仕様通りである。[DAO-7]
        if (!res.hasPropertyCacheElement()) {
            return null;
        }

        setupRelationKeyValue(res);
        setupRelationAllValue(res);
        return res.getRow();
    }

    protected void setupRelationKeyValue(RelationRowCreationResource res) {
        final RelationPropertyType rpt = res.getRelationPropertyType();
        final BeanMetaData bmd = rpt.getBeanMetaData();
        for (int i = 0; i < rpt.getKeySize(); ++i) {
            final String columnName = rpt.getMyKey(i) + res.getBaseSuffix();

            if (!res.containsColumnName(columnName)) {
                continue;
            }
            if (!res.hasRowInstance()) {
                res.setRow(newRelationRow(rpt));
            }
            if (!res.containsRelKeyValueIfExists(columnName)) {
                continue;
            }
            final Object value = res.extractRelKeyValue(columnName);
            if (value == null) {
                continue;
            }

            final String yourKey = rpt.getYourKey(i);
            final PropertyType pt = bmd.getPropertyTypeByColumnName(yourKey);
            final PropertyDesc pd = pt.getPropertyDesc();
            pd.setValue(res.getRow(), value);
            continue;
        }
    }

    protected void setupRelationAllValue(RelationRowCreationResource res)
            throws SQLException {
        final Map propertyCacheElement = res.extractPropertyCacheElement();
        final Set columnNameCacheElementKeySet = propertyCacheElement.keySet();
        for (final Iterator ite = columnNameCacheElementKeySet.iterator(); ite
                .hasNext();) {
            final String columnName = (String) ite.next();
            final PropertyType pt = (PropertyType) propertyCacheElement
                    .get(columnName);
            res.setCurrentPropertyType(pt);
            if (!isValidRelationPerPropertyLoop(res)) {
                res.clearRowInstance();
                return;
            }
            setupRelationProperty(res);
        }
        if (!isValidRelationAfterPropertyLoop(res)) {
            res.clearRowInstance();
            return;
        }
        res.clearValidValueCount();
        if (res.hasNextRelationProperty() && res.hasNextRelationLevel()) {
            setupNextRelationRow(res);
        }
    }

    protected boolean isValidRelationPerPropertyLoop(
            RelationRowCreationResource res) throws SQLException {
        return true;// Always true as default. This method is for extension(for override).
    }

    protected boolean isValidRelationAfterPropertyLoop(
            RelationRowCreationResource res) throws SQLException {
        if (res.isCreateDeadLink()) {
            return true;
        }
        return res.hasValidValueCount();
    }

    protected void setupRelationProperty(RelationRowCreationResource res)
            throws SQLException {
        final String columnName = res.buildRelationColumnName();
        if (!res.hasRowInstance()) {
            res.setRow(newRelationRow(res));
        }
        registerRelationValue(res, columnName);
    }

    protected void registerRelationValue(RelationRowCreationResource res,
            String columnName) throws SQLException {
        final PropertyType pt = res.getCurrentPropertyType();
        Object value = null;
        if (res.containsRelKeyValueIfExists(columnName)) {
            value = res.extractRelKeyValue(columnName);
        } else {
            final ValueType valueType = pt.getValueType();
            value = valueType.getValue(res.getResultSet(), columnName);
        }
        if (value != null) {
            registerRelationValidValue(res, pt, value);
        }
    }
    
    protected void registerRelationValidValue(RelationRowCreationResource res,
            PropertyType pt, Object value) throws SQLException {
        res.incrementValidValueCount();
        final PropertyDesc pd = pt.getPropertyDesc();
        pd.setValue(res.getRow(), value);
    }

    // -----------------------------------------------------
    //                                         Next Relation
    //                                         -------------
    protected void setupNextRelationRow(RelationRowCreationResource res)
            throws SQLException {
        final BeanMetaData nextBmd = res.getRelationBeanMetaData();
        final Object row = res.getRow();
        res.backupRelationPropertyType();
        res.incrementCurrentRelationNestLevel();
        try {
            for (int i = 0; i < nextBmd.getRelationPropertyTypeSize(); ++i) {
                final RelationPropertyType nextRpt = nextBmd
                        .getRelationPropertyType(i);
                setupNextRelationRowElement(res, row, nextRpt);
            }
        } finally {
            res.setRow(row);
            res.restoreRelationPropertyType();
            res.decrementCurrentRelationNestLevel();
        }
    }

    protected void setupNextRelationRowElement(RelationRowCreationResource res,
            Object row, RelationPropertyType nextRpt) throws SQLException {
        if (nextRpt == null) {
            return;
        }
        res.clearRowInstance();
        res.setRelationPropertyType(nextRpt);

        final String baseSuffix = res.getRelationNoSuffix();
        final String additionalRelationNoSuffix = buildRelationNoSuffix(nextRpt);
        res.backupSuffixAndPrepare(baseSuffix, additionalRelationNoSuffix);
        try {
            final Object relationRow = createRelationRow(res);
            if (relationRow != null) {
                nextRpt.getPropertyDesc().setValue(row, relationRow);
            }
        } finally {
            res.restoreSuffix();
        }
    }

    // ===================================================================================
    //                                                             Property Cache Creation
    //                                                             =======================
    /**
     * @param columnNames The set of column name. (NotNull)
     * @param bmd Bean meta data of base object. (NotNull)
     * @return The map of relation property cache. Map{String(relationNoSuffix), Map{String(columnName), PropertyType}} (NotNull)
     * @throws SQLException
     */
    public Map createPropertyCache(Set columnNames, BeanMetaData bmd)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Map relationPropertyCache = newRelationPropertyCache();
        for (int i = 0; i < bmd.getRelationPropertyTypeSize(); ++i) {
            final RelationPropertyType rpt = bmd.getRelationPropertyType(i);
            final String baseSuffix = "";
            final String relationNoSuffix = buildRelationNoSuffix(rpt);
            final RelationRowCreationResource res = createResourceForPropertyCache(
                    rpt, columnNames, relationPropertyCache, baseSuffix,
                    relationNoSuffix, getLimitRelationNestLevel());
            if (rpt == null) {
                continue;
            }
            setupPropertyCache(res);
        }
        return relationPropertyCache;
    }

    protected RelationRowCreationResource createResourceForPropertyCache(
            RelationPropertyType rpt, Set columnNames,
            Map relationPropertyCache, String baseSuffix,
            String relationNoSuffix, int limitRelationNestLevel)
            throws SQLException {
        final RelationRowCreationResource res = new RelationRowCreationResource();
        res.setRelationPropertyType(rpt);
        res.setColumnNames(columnNames);
        res.setRelationPropertyCache(relationPropertyCache);
        res.setBaseSuffix(baseSuffix);
        res.setRelationNoSuffix(relationNoSuffix);
        res.setLimitRelationNestLevel(limitRelationNestLevel);
        res.setCurrentRelationNestLevel(1);// as Default
        return res;
    }

    protected void setupPropertyCache(RelationRowCreationResource res)
            throws SQLException {
        // - - - - - - - - - - - 
        // Recursive Call Point!
        // - - - - - - - - - - -
        // Cacheの初期化をする。この時点では空っぽである。
        // Cacheするべきものが一つも無い場合でも、
        // 「一つも無い」という状態がCacheされることになる。。
        res.initializePropertyCacheElement();

        // Check whether the relation is target or not.
        if (!isTargetRelation(res)) {
            return;
        }
        
        // Set up property cache about current beanMetaData.
        final BeanMetaData nextBmd = res.getRelationBeanMetaData();
        for (int i = 0; i < nextBmd.getPropertyTypeSize(); ++i) {
            final PropertyType pt = nextBmd.getPropertyType(i);
            res.setCurrentPropertyType(pt);
            if (!isTargetProperty(res)) {
                continue;
            }
            setupPropertyCacheElement(res);
        }
        
        // Set up next relation.
        if (res.hasNextRelationProperty() && res.hasNextRelationLevel()) {
            res.backupRelationPropertyType();
            res.incrementCurrentRelationNestLevel();
            try {
                setupNextPropertyCache(res, nextBmd);
            } finally {
                res.restoreRelationPropertyType();
                res.decrementCurrentRelationNestLevel();
            }
        }
    }

    protected void setupPropertyCacheElement(RelationRowCreationResource res)
            throws SQLException {
        final String columnName = res.buildRelationColumnName();
        if (!res.containsColumnName(columnName)) {
            return;
        }
        res.savePropertyCacheElement();
    }

    // -----------------------------------------------------
    //                                         Next Relation
    //                                         -------------
    protected void setupNextPropertyCache(RelationRowCreationResource res,
            BeanMetaData nextBmd) throws SQLException {
        for (int i = 0; i < nextBmd.getRelationPropertyTypeSize(); ++i) {
            final RelationPropertyType nextNextRpt = nextBmd
                    .getRelationPropertyType(i);
            res.setRelationPropertyType(nextNextRpt);
            setupNextPropertyCacheElement(res, nextNextRpt);
        }
    }

    protected void setupNextPropertyCacheElement(
            RelationRowCreationResource res, RelationPropertyType nextNextRpt)
            throws SQLException {
        final String baseSuffix = res.getRelationNoSuffix();
        final String additionalRelationNoSuffix = buildRelationNoSuffix(nextNextRpt);
        res.backupSuffixAndPrepare(baseSuffix, additionalRelationNoSuffix);
        try {
            setupPropertyCache(res);// Recursive call!
        } finally {
            res.restoreSuffix();
        }
    }

    // -----------------------------------------------------
    //                                                Common
    //                                                ------
    protected Map newRelationPropertyCache() {
        return new HashMap();
    }

    // ===================================================================================
    //                                                                        Common Logic
    //                                                                        ============
    protected String buildRelationNoSuffix(RelationPropertyType rpt) {
        return "_" + rpt.getRelationNo();
    }

    protected Object newRelationRow(RelationRowCreationResource res) {
        return newRelationRow(res.getRelationPropertyType());
    }

    protected Object newRelationRow(RelationPropertyType rpt) {
        return ClassUtil.newInstance(rpt.getPropertyDesc().getPropertyType());
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    protected boolean isTargetRelation(RelationRowCreationResource res)
            throws SQLException {
        // - - - - - - - - - - - - - - - - - - - - - - - -
        // Extension Point!
        //  --> 該当のRelationを処理対象とするか否か。
        // - - - - - - - - - - - - - - - - - - - - - - - -
        return true;// 基本はtrue固定。拡張クラスで何かの情報をもとに判定してもらうことを想定。
    }
    
    protected boolean isTargetProperty(RelationRowCreationResource res)
            throws SQLException {
        // - - - - - - - - - - - - - - - - - - - - - - - -
        // Extension Point!
        //  --> 該当のPropertyを処理対象とするか否か。
        // - - - - - - - - - - - - - - - - - - - - - - - -
        final PropertyType pt = res.getCurrentPropertyType();
        return PropertyDescUtil.isWritable(pt.getPropertyDesc());
    }

    protected boolean isCreateDeadLink() {
        // - - - - - - - - - - - - - - - - - - - - - - - -
        // Extension Point!
        //  --> 参照切れの場合にInstanceを作成するか否か。
        // - - - - - - - - - - - - - - - - - - - - - - - -
        return true;// 以前の仕様のまま(空Entityを作成する)とする。
    }

    protected int getLimitRelationNestLevel() {
        // - - - - - - - - - - - - - - - - - - - - - - - - -
        // Extension Point!
        //  --> RelationのNestを何レベルまで許可するか否か。
        // - - - - - - - - - - - - - - - - - - - - - - - - -
        return 1;// 以前の仕様のまま(1階層まで)とする。
    }
}
