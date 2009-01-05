/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import org.seasar.dao.DtoMetaData;
import org.seasar.dao.RowCreator;
import org.seasar.framework.log.Logger;

/**
 * @author jflute
 */
public class DtoMetaDataResultSetHandler extends
        AbstractDtoMetaDataResultSetHandler {

    private static final Logger logger = Logger
            .getLogger(DtoMetaDataResultSetHandler.class);

    /**
     * @param dtoMetaData Dto meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     */
    public DtoMetaDataResultSetHandler(DtoMetaData dtoMetaData,
            RowCreator rowCreator) {
        super(dtoMetaData, rowCreator);
    }

    public Object handle(ResultSet resultSet) throws SQLException {
        // Map<String(columnName), PropertyType>
        Map propertyCache = null;// [DAO-118] (2007/08/26)

        if (resultSet.next()) {
            final Set columnNames = createColumnNames(resultSet.getMetaData());

            // Lazy initialization because if the result is zero, the cache is unused.
            if (propertyCache == null) {
                propertyCache = createPropertyCache(columnNames);
            }

            final Object row = createRow(resultSet, propertyCache);
            if (resultSet.next()) {
                logger.log("WDAO0003", null);
            }
            return row;
        } else {
            return null;
        }
    }

}
