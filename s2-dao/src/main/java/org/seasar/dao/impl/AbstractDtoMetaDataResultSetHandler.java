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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import org.seasar.dao.DtoMetaData;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.CaseInsensitiveSet;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.StringUtil;

public abstract class AbstractDtoMetaDataResultSetHandler implements
        ResultSetHandler {

    private DtoMetaData dtoMetaData;

    public AbstractDtoMetaDataResultSetHandler(DtoMetaData dtoMetaData) {
        this.dtoMetaData = dtoMetaData;
    }

    public DtoMetaData getDtoMetaData() {
        return dtoMetaData;
    }

    protected Object createRow(ResultSet rs, Set columnNames)
            throws SQLException {

        Object row = ClassUtil.newInstance(dtoMetaData.getBeanClass());
        for (int i = 0; i < dtoMetaData.getPropertyTypeSize(); ++i) {
            PropertyType pt = dtoMetaData.getPropertyType(i);
            if (columnNames.contains(pt.getColumnName())) {
                ValueType valueType = pt.getValueType();
                Object value = valueType.getValue(rs, pt.getColumnName());
                PropertyDesc pd = pt.getPropertyDesc();
                pd.setValue(row, value);
            } else if (columnNames.contains(pt.getPropertyName())) {
                ValueType valueType = pt.getValueType();
                Object value = valueType.getValue(rs, pt.getPropertyName());
                PropertyDesc pd = pt.getPropertyDesc();
                pd.setValue(row, value);
            } else if (!pt.isPersistent()) {
                for (Iterator iter = columnNames.iterator(); iter.hasNext();) {
                    String columnName = (String) iter.next();
                    String columnName2 = StringUtil
                            .replace(columnName, "_", "");
                    if (columnName2.equalsIgnoreCase(pt.getColumnName())) {
                        ValueType valueType = pt.getValueType();
                        Object value = valueType.getValue(rs, columnName);
                        PropertyDesc pd = pt.getPropertyDesc();
                        pd.setValue(row, value);
                        break;
                    }
                }
            }
        }
        return row;
    }

    /*
     * https://www.seasar.org/issues/browse/DAO-41
     * SQLiteでは[TABLE名.COLUMN名]がcolumnNamesに入るため、[COLUMN名]だけにしておく。
     */
    protected Set createColumnNames(final ResultSetMetaData rsmd)
            throws SQLException {
        final int count = rsmd.getColumnCount();
        final Set columnNames = new CaseInsensitiveSet();
        for (int i = 0; i < count; ++i) {
            final String columnName = rsmd.getColumnLabel(i + 1);
            final int pos = columnName.lastIndexOf('.');
            if (-1 < pos) {
                columnNames.add(columnName.substring(pos + 1));
            } else {
                columnNames.add(columnName);
            }
        }
        return columnNames;
    }
}