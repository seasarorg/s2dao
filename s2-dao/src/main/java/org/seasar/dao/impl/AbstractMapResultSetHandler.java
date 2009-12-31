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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.PropertyTypeImpl;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.framework.util.CaseInsensitiveMap;
import org.seasar.framework.util.StringUtil;

public abstract class AbstractMapResultSetHandler implements ResultSetHandler {

    public AbstractMapResultSetHandler() {
    }

    protected Map createRow(ResultSet rs, PropertyType[] propertyTypes)
            throws SQLException {

        Map row = new CaseInsensitiveMap();
        for (int i = 0; i < propertyTypes.length; ++i) {
            Object value = propertyTypes[i].getValueType().getValue(rs, i + 1);
            row.put(propertyTypes[i].getPropertyName(), value);
        }
        return row;
    }

    protected PropertyType[] createPropertyTypes(ResultSetMetaData rsmd)
            throws SQLException {

        int count = rsmd.getColumnCount();
        PropertyType[] propertyTypes = new PropertyType[count];
        for (int i = 0; i < count; ++i) {
            String propertyName = StringUtil.replace(
                    rsmd.getColumnLabel(i + 1), "_", "");
            ValueType valueType = ValueTypes.getValueType(rsmd
                    .getColumnType(i + 1));
            propertyTypes[i] = new PropertyTypeImpl(propertyName, valueType);
        }
        return propertyTypes;
    }
}