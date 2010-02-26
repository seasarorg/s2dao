/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
import java.util.Map;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.NotSingleResultRuntimeException;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.RelationRowCreator;
import org.seasar.dao.RowCreator;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.log.Logger;

/**
 * @author jflute
 */
public class BeanMetaDataResultSetHandler extends
        AbstractBeanMetaDataResultSetHandler {

    private static final Logger logger = Logger
            .getLogger(BeanMetaDataResultSetHandler.class);

    /**
     * @param dtoMetaData
     *            Dto meta data. (NotNull)
     * @param rowCreator
     *            Row creator. (NotNull)
     * @param relationRowCreator
     *            Relation row creator. (NotNul)
     */
    public BeanMetaDataResultSetHandler(BeanMetaData beanMetaData,
            RowCreator rowCreator, RelationRowCreator relationRowCreator) {
        super(beanMetaData, rowCreator, relationRowCreator);
    }

    /**
     * @see org.seasar.extension.jdbc.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            // Set<String(columnName)>
            final Set columnNames = createColumnNames(resultSet.getMetaData());// [DAO-118]
            // (2007/08/26)

            // Map<String(columnName), PropertyType>
            Map propertyCache = createPropertyCache(columnNames);// [DAO-118]
            // (2007/08/25)

            // Map<String(relationNoSuffix), Set<PropertyType>>
            final Map relationPropertyCache = createRelationPropertyCache(columnNames);// [DAO-118]
            // (2007/08/26)

            final Object row = createRow(resultSet, propertyCache);
            for (int i = 0; i < getBeanMetaData().getRelationPropertyTypeSize(); ++i) {
                RelationPropertyType rpt = getBeanMetaData()
                        .getRelationPropertyType(i);
                if (rpt == null) {
                    continue;
                }
                Map relKeyValues = new HashMap();
                RelationKey relKey = createRelationKey(resultSet, rpt,
                        columnNames, relKeyValues);
                Object relationRow = null;
                if (relKey != null) {
                    relationRow = createRelationRow(resultSet, rpt,
                            columnNames, relKeyValues, relationPropertyCache);
                }
                if (relationRow != null) {
                    PropertyDesc pd = rpt.getPropertyDesc();
                    pd.setValue(row, relationRow);
                    postCreateRow(relationRow);
                }
            }
            postCreateRow(row);
            if (resultSet.next()) {
                handleNotSingleResult();
            }
            return row;
        } else {
            return null;
        }
    }

    /**
     * 結果が1件でない場合の処理を行います。
     * デフォルトでは警告ログを出力します。
     */
    protected void handleNotSingleResult() {
        logger.log("WDAO0003", null);
    }

    /**
     * 結果が2件以上のときに例外をスローする{@link BeanMetaDataResultSetHandler}です
     * @author azusa
     *
     */
    public static class RestrictBeanMetaDataResultSetHandler extends BeanMetaDataResultSetHandler{

        /**
         * @param beanMetaData  BeanMetaData
         * @param rowCreator RowCreator
         * @param relationRowCreator RelationRowCreator
         */
        public RestrictBeanMetaDataResultSetHandler(BeanMetaData beanMetaData,
                RowCreator rowCreator, RelationRowCreator relationRowCreator) {
            super(beanMetaData, rowCreator, relationRowCreator);
        }

        protected void handleNotSingleResult() {
            throw new NotSingleResultRuntimeException();
        }
        
    }
}
