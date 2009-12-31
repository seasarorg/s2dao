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
import java.util.ArrayList;
import java.util.List;

import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.ValueType;

/**
 * オブジェクトのリストを返す {@link ResultSetHandler}です。
 * 
 * @author taedium
 */
public class ObjectListResultSetHandler extends AbstractObjectResultSetHandler {

    /**
     * {@link ObjectListResultSetHandler}を作成します。
     * 
     * @param clazz
     *            リストの要素の型
     */
    public ObjectListResultSetHandler(Class clazz) {
        super(clazz);
    }

    public Object handle(ResultSet rs) throws SQLException {
        List ret = new ArrayList(100);
        ValueType valueType = getValueType(rs);
        while (rs.next()) {
            ret.add(valueType.getValue(rs, 1));
        }
        return ret;
    }
}
