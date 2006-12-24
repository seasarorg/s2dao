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
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.RelationRowCreator;
import org.seasar.framework.beans.PropertyDesc;

public class BeanMetaDataResultSetHandler extends
        AbstractBeanMetaDataResultSetHandler {

    public BeanMetaDataResultSetHandler(BeanMetaData beanMetaData,
            RelationRowCreator relationRowCreator) {
        super(beanMetaData, relationRowCreator);
    }

    /**
     * @see org.seasar.extension.jdbc.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            Set columnNames = createColumnNames(resultSet.getMetaData());
            Object row = createRow(resultSet, columnNames);
            for (int i = 0; i < getBeanMetaData().getRelationPropertyTypeSize(); ++i) {
                RelationPropertyType rpt = getBeanMetaData()
                        .getRelationPropertyType(i);
                if (rpt == null) {
                    continue;
                }
                Object relationRow = createRelationRow(resultSet, rpt,
                        columnNames, null);
                if (relationRow != null) {
                    PropertyDesc pd = rpt.getPropertyDesc();
                    pd.setValue(row, relationRow);
                    postCreateRow(relationRow);
                }
            }
            postCreateRow(row);
            return row;
        } else {
            return null;
        }
    }

}
