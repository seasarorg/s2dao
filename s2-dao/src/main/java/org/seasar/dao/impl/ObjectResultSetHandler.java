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

import org.seasar.dao.NotSingleResultRuntimeException;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.framework.log.Logger;

/**
 * オブジェクトを返す{@link ResultSetHandler}です。
 * 
 * @author taedium
 */
public class ObjectResultSetHandler extends AbstractObjectResultSetHandler {

    private static final Logger logger = Logger
            .getLogger(ObjectResultSetHandler.class);

    /**
     * {@link ObjectResultSetHandlear}を生成します。
     * 
     * @param clazz
     *            オブジェクトの型
     */
    public ObjectResultSetHandler(Class clazz) {
        super(clazz);
    }

    public Object handle(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Object value = getValueType(rs).getValue(rs, 1);
            if (rs.next()) {
                handleNotSingleResult();
            }
            return value;
        }
        return null;
    }

    /**
     * 結果が1件でない場合の処理を行います。 デフォルトでは警告ログを出力します。
     */
    protected void handleNotSingleResult() {
        logger.log("WDAO0003", null);
    }

    /**
     * 結果が2件以上のときに例外をスローする{@link ObjectResultSetHandler}です。
     * 
     * @author azusa
     * 
     */
    public static class RestrictObjectResultSetHandler extends
            ObjectResultSetHandler {

        /**
         * @param clazz
         *            返り値のクラス
         */
        public RestrictObjectResultSetHandler(Class clazz) {
            super(clazz);
        }

        protected void handleNotSingleResult() {
            throw new NotSingleResultRuntimeException();
        }

    }
}
