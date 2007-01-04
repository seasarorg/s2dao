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

    public Object createRelationRow(ResultSet rs, RelationPropertyType rpt,
            Set columnNames, Map relKeyValues) throws SQLException {
        final Object row = setupRelationKeyValue(rpt, columnNames, relKeyValues);
        return setupRelationAllValue(row, rs, rpt, columnNames, relKeyValues);
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
            RelationPropertyType rpt, Set columnNames, Map relKeyValues)
            throws SQLException {
        final String relationNoSuffix = "_" + rpt.getRelationNo();
        final BeanMetaData bmd = rpt.getBeanMetaData();
        int existColumn = 0;
        for (int i = 0; i < bmd.getPropertyTypeSize(); ++i) {
            final PropertyType pt = bmd.getPropertyType(i);
            final String columnName = pt.getColumnName() + relationNoSuffix;
            if (!columnNames.contains(columnName)) {
                continue;
            }
            existColumn++;
            if (row == null) {
                row = createRelationRow(rpt);
            }
            registerRelationValue(row, rs, rpt, pt, columnName, relKeyValues);
        }
        if (existColumn == 0) {
            return null;
        }
        return row;
    }

    protected void registerRelationValue(Object row, ResultSet rs,
            RelationPropertyType rpt, PropertyType pt, String columnName,
            Map relKeyValues) throws SQLException {
        Object value = null;
        if (relKeyValues != null && relKeyValues.containsKey(columnName)) {
            value = relKeyValues.get(columnName);
        } else {
            final ValueType valueType = pt.getValueType();
            value = valueType.getValue(rs, columnName);
        }
        final PropertyDesc pd = pt.getPropertyDesc();
        if (value != null) {
            pd.setValue(row, value);
        }
    }

    protected Object createRelationRow(RelationPropertyType rpt) {
        return ClassUtil.newInstance(rpt.getPropertyDesc().getPropertyType());
    }
}
