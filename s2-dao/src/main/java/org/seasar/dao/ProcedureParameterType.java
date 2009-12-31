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
package org.seasar.dao;

import org.seasar.extension.jdbc.ValueType;

/**
 * プロシージャのパラメータのタイプです。
 * 
 * @author taedium
 */
public interface ProcedureParameterType {

    /**
     * パラメータ名を返します。
     * 
     * @return パラメータ名
     */
    String getParameterName();

    /**
     * {@link ValueType}を返します。
     * 
     * @return {@link ValueType}
     */
    ValueType getValueType();

    /**
     * {@link ValueType}を設定します。
     * 
     * @param valueType　{@link ValueType}
     */
    void setValueType(ValueType valueType);

    /**
     * <code>IN</code>パラメータもしくは<code>INOUT</code>パラメータである場合に<code>true</code>を返します。
     * 
     * @return　<code>IN</code>パラメータ
     * もしくは<code>INOUT</code>パラメータである場合<code>true</code>、そうでない場合<code>false</code>
     */
    boolean isInType();

    /**
     * <code>IN</code>パラメータもしくは<code>INOUT</code>パラメータである場合に<code>true</code>を設定します。
     * 
     * @param inType　<code>IN</code>パラメータもしくは<code>INOUT</code>パラメータである場合<code>true</code>
     */
    void setInType(boolean inType);

    /**
     * <code>OUT</code>パラメータ、<code>INOUT</code>パラメータ
     * もしくは<code>RETURN</code>パラメータである場合に<code>true</code>を返します。
     * 
     * @return　<code>OUT</code>パラメータ、<code>INOUT</code>パラメータ
     * もしくは<code>RETURN</code>パラメータである場合<code>true</code>、そうでない場合<code>false</code>
     */
    boolean isOutType();

    /**
     * <code>OUT</code>パラメータ、<code>INOUT</code>パラメータ
     * もしくは<code>RETURN</code>パラメータである場合に<code>true</code>を設定します。
     * 
     * @param outType　<code>OUT</code>パラメータ、<code>INOUT</code>パラメータ
     * もしくは<code>RETURN</code>パラメータである場合<code>true</code>
     */
    void setOutType(boolean outType);

    /**
     * ppt.setOutType(true);ある場合に<code>true</code>を返します。
     * 
     * @return　<code>RETURN</code>パラメータである場合<code>true</code>、そうでない場合<code>false</code>
     */
    boolean isReturnType();

    /**
     * <code>RETURN</code>パラメータである場合に<code>true</code>を設定します。
     * 
     * @param returnType <code>RETURN</code>パラメータである場合<code>true</code>
     */
    void setReturnType(boolean returnType);

    /**
     * 値を返します。
     * 
     * @param target 対象のオブジェクト
     * @return 値
     */
    Object getValue(Object target);

    /**
     * 値を設定します。
     * 
     * @param target 対象のオブジェクト
     * @param value 値
     */
    void setValue(Object target, Object value);

}
