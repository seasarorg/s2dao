/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.RelationRowCreator;
import org.seasar.dao.RowCreator;
import org.seasar.framework.beans.PropertyDesc;

/**
 * @author jflute
 */
public class BeanListMetaDataResultSetHandler extends
        AbstractBeanMetaDataResultSetHandler {

    /**
     * @param dtoMetaData
     *            Dto meta data. (NotNull)
     * @param rowCreator
     *            Row creator. (NotNull)
     * @param relationRowCreator
     *            Relation row creator. (NotNul)
     */
    public BeanListMetaDataResultSetHandler(BeanMetaData beanMetaData,
            RowCreator rowCreator, RelationRowCreator relationRowCreator) {
        super(beanMetaData, rowCreator, relationRowCreator);
    }

    /**
     * @see org.seasar.extension.jdbc.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet rs) throws SQLException {
        // Set<String(columnName)>
        final Set columnNames = createColumnNames(rs.getMetaData());

        // Map<String(columnName), PropertyType>
        Map propertyCache = null;// [DAO-118] (2007/08/26)

        // Map<String(relationNoSuffix), Map<String(columnName), PropertyType>>
        Map relationPropertyCache = null;// [DAO-118] (2007/08/25)

        final List list = new ArrayList();
        final int relSize = getBeanMetaData().getRelationPropertyTypeSize();
        final RelationRowCache relRowCache = new RelationRowCache(relSize);

        while (rs.next()) {
            // Lazy initialization because if the result is zero, the cache is
            // unused.
            if (propertyCache == null) {
                propertyCache = createPropertyCache(columnNames);
            }
            if (relationPropertyCache == null) {
                relationPropertyCache = createRelationPropertyCache(columnNames);
            }

            // Create row instance of base table by row property cache.
            final Object row = createRow(rs, propertyCache);

            for (int i = 0; i < relSize; ++i) {
                RelationPropertyType rpt = getBeanMetaData()
                        .getRelationPropertyType(i);
                if (rpt == null) {
                    continue;
                }
                Object relationRow = null;
                Map relKeyValues = new HashMap();
                // TODO 1レコード目でnullが返るなら、2レコード目以降は不要では?
                RelationKey relKey = createRelationKey(rs, rpt, columnNames,
                        relKeyValues);
                if (relKey != null) {
                    relationRow = relRowCache.getRelationRow(i, relKey);
                    if (relationRow == null) {
                        relationRow = createRelationRow(rs, rpt, columnNames,
                                relKeyValues, relationPropertyCache);
                        relRowCache.addRelationRow(i, relKey, relationRow);
                    }
                }
                if (relationRow != null) {
                    PropertyDesc pd = rpt.getPropertyDesc();
                    pd.setValue(row, relationRow);
                    postCreateRow(relationRow);
                }
            }
            postCreateRow(row);
            list.add(row);
        }
        return list;
    }

}