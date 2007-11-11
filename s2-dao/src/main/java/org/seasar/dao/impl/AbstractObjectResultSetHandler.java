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

import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.types.ValueTypes;

/**
 * オブジェクト用の{@link ResultSetHandler}の抽象クラスです。
 * 
 * @author taedium
 */
public abstract class AbstractObjectResultSetHandler implements
        ResultSetHandler {

    protected Class clazz;

    /**
     * {@link AbstractObjectResultSetHandler}を生成します。
     * 
     * @param clazz
     *            オブジェクトの型
     */
    public AbstractObjectResultSetHandler(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * <code>clazz</code>もしくは結果セットのメタデータに対応する{@link ValueType}を返します。
     * 
     * @param rs
     *            結果セット
     * @return {@link ValueType}
     * @throws SQLException
     */
    protected ValueType getValueType(ResultSet rs) throws SQLException {
        if (clazz != null) {
            return ValueTypes.getValueType(clazz);
        }
        ResultSetMetaData rsmd = rs.getMetaData();
        return ValueTypes.getValueType(rsmd.getColumnTypeName(1));
    }
}
