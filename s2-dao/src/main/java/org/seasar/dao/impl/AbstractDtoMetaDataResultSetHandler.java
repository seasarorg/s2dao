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
import java.util.Map;
import java.util.Set;

import org.seasar.dao.DtoMetaData;
import org.seasar.dao.RowCreator;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.framework.util.CaseInsensitiveSet;

/**
 * @author jflute
 */
public abstract class AbstractDtoMetaDataResultSetHandler implements
        ResultSetHandler {

    private DtoMetaData dtoMetaData;

    protected RowCreator rowCreator;// [DAO-118] (2007/08/25)

    /**
     * @param dtoMetaData Dto meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     */
    public AbstractDtoMetaDataResultSetHandler(DtoMetaData dtoMetaData,
            RowCreator rowCreator) {
        this.dtoMetaData = dtoMetaData;
        this.rowCreator = rowCreator;
    }

    /**
     * @param columnNames The set of column name. (NotNull)
     * @return The map of row property cache. Map{String(columnName), PropertyType} (NotNull)
     * @throws SQLException
     */
    protected Map createPropertyCache(Set columnNames) throws SQLException {
        return rowCreator.createPropertyCache(columnNames, dtoMetaData);
    }

    /**
     * @param rs Result set. (NotNull)
     * @param propertyCache The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    protected Object createRow(ResultSet rs, Map propertyCache)
            throws SQLException {
        final Class beanClass = dtoMetaData.getBeanClass();
        return rowCreator.createRow(rs, propertyCache, beanClass);
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

    public DtoMetaData getDtoMetaData() {
        return dtoMetaData;
    }
}