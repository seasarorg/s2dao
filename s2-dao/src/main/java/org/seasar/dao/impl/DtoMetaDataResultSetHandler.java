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
import java.util.Set;

import org.seasar.dao.DtoMetaData;

public class DtoMetaDataResultSetHandler extends
        AbstractDtoMetaDataResultSetHandler {

    public DtoMetaDataResultSetHandler(DtoMetaData dtoMetaData) {
        super(dtoMetaData);
    }

    public Object handle(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            Set columnNames = createColumnNames(resultSet.getMetaData());
            Object row = createRow(resultSet, columnNames);
            return row;
        } else {
            return null;
        }
    }

}
