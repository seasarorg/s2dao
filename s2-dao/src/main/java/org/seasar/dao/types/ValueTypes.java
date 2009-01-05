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
package org.seasar.dao.types;

import org.seasar.dao.dbms.PostgreSQL;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.types.StringClobType;

/**
 * 値タイプの定数です。
 * 
 * @author taedium
 */
public class ValueTypes {

    /**
     * CLOB用の値タイプです。
     */
    public final static ValueType CLOB = new StringClobType();

    /**
     * バイト配列用の値タイプです。
     */
    public final static ValueType BYTE_ARRAY = new BytesType(
            BytesType.BYTES_TRAIT);

    /**
     * BLOB用の値タイプです。
     */
    public final static ValueType BLOB = new BytesType(BytesType.BLOB_TRAIT);

    /**
     * PostgreSQLのBLOB用の値タイプです。
     */
    public final static ValueType POSTGRE_BLOB = new BytesType(
            PostgreSQL.POSTGRE_TRAIT);

    /**
     * オブジェクトをシリアライズしたバイト配列用の値タイプです。
     */
    public final static ValueType SERIALIZABLE_BYTE_ARRAY = new SerializableType(
            BytesType.BYTES_TRAIT);

    /**
     * オブジェクトをシリアライズしたBLOB用の値タイプです。
     */
    public final static ValueType SERIALIZABLE_BLOB = new SerializableType(
            BytesType.BLOB_TRAIT);

    /**
     * オブジェクトをシリアライズしたPostgreSQLのBLOB用の値タイプです。
     */
    public final static ValueType POSTGRE_SERIALIZABLE_BLOB = new SerializableType(
            PostgreSQL.POSTGRE_TRAIT);
}
