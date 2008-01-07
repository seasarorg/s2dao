/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.seasar.extension.jdbc.ResultSetHandler;

/**
 * オブジェクトの配列を返す{@link ResultSetHandler}です。
 * 
 * @author taedium
 */
public class ObjectArrayResultSetHandler extends ObjectListResultSetHandler {

    /**
     * {@link ObjectArrayResultSetHandler}を作成します。
     * 
     * @param clazz
     *            配列の要素の型
     */
    public ObjectArrayResultSetHandler(Class clazz) {
        super(clazz);
    }

    public Object handle(ResultSet rs) throws SQLException {
        List list = (List) super.handle(rs);
        Object array = Array.newInstance(clazz, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }
}
