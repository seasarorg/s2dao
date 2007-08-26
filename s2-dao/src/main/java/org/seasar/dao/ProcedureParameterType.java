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
package org.seasar.dao;

import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.PropertyDesc;

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
     * パラメータ名を設定します。
     */
    void setParameterName(String parameterName);

    /**
     * プロパティ記述を返します。
     * 
     * @return プロパティ記述
     */
    PropertyDesc getPropertyDesc();

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
     * @return　<code>IN</code>パラメータもしくは<code>INOUT</code>パラメータである場合<code>true</code>、そうでない場合<code>false</code>
     */
    boolean isInType();

    /**
     * <code>IN</code>パラメータもしくは<code>INOUT</code>パラメータである場合に<code>true</code>を設定します。
     * 
     * @param inType　<code>IN</code>パラメータもしくは<code>INOUT</code>パラメータである場合<code>true</code>
     */
    void setInType(boolean inType);

    /**
     * <code>OUT</code>パラメータもしくは<code>INOUT</code>パラメータである場合に<code>true</code>を返します。
     * 
     * @return　<code>OUT</code>パラメータもしくは<code>INOUT</code>パラメータである場合<code>true</code>、そうでない場合<code>false</code>
     */
    boolean isOutType();

    /**
     * <code>OUT</code>パラメータもしくは<code>INOUT</code>パラメータである場合に<code>true</code>を設定します。
     * 
     * @param outType　<code>OUT</code>パラメータもしくは<code>INOUT</code>パラメータである場合<code>true</code>
     */
    void setOutType(boolean outType);

    /**
     * <code>RETURN</code>パラメータである場合に<code>true</code>を返します。
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
     * 位置を持っている場合<code>true</code>を返します。
     * 
     * @return　位置を持っている場合<code>true</code>、そうでない場合<code>false</code>
     */
    boolean hasIndex();

    /**
     * 位置を返します。
     * 
     * @return　位置
     */
    Integer getIndex();

    /**
     * 位置を設定します。
     * 
     * @param index 位置
     */
    void setIndex(Integer index);

}
