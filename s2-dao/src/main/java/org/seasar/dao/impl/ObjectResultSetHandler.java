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

import org.seasar.extension.jdbc.ResultSetHandler;

/**
 * オブジェクトを返す{@link ResultSetHandler}です。
 * 
 * @author taedium
 */
public class ObjectResultSetHandler extends AbstractObjectResultSetHandler {

    /**
     * {@link ObjectResultSetHandler}を生成します。
     * 
     * @param clazz
     *            オブジェクトの型
     */
    public ObjectResultSetHandler(Class clazz) {
        super(clazz);
    }

    public Object handle(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return getValueType(rs).getValue(rs, 1);
        }
        return null;
    }

}
