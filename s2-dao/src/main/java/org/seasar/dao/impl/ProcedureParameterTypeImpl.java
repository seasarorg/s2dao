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

import org.seasar.dao.ProcedureParameterType;
import org.seasar.extension.jdbc.ValueType;

/**
 * {@link ProcedureParameterType}の実装クラスです。
 * 
 * @author taedium
 */
public class ProcedureParameterTypeImpl implements ProcedureParameterType {

    private String parameterName;

    private ValueType valueType;

    private boolean inType;

    private boolean outType;

    private boolean returnType;

    /**
     * インスタンスを構築します。
     * 
     * @param parameterName パラメータ名
     */
    public ProcedureParameterTypeImpl(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public boolean isInType() {
        return inType;
    }

    public boolean isOutType() {
        return outType;
    }

    public boolean isReturnType() {
        return returnType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    /**
     * <code>IN</code>パラメータもしくは<code>INOUT</code>パラメータである場合に<code>true</code>を設定します。
     * 
     * @param inType　<code>IN</code>パラメータもしくは<code>INOUT</code>パラメータである場合<code>true</code>
     */
    public void setInType(boolean inType) {
        this.inType = inType;
    }

    /**
     * <code>OUT</code>パラメータもしくは<code>INOUT</code>パラメータである場合に<code>true</code>を設定します。
     * 
     * @param outType　<code>OUT</code>パラメータもしくは<code>INOUT</code>パラメータである場合<code>true</code>
     */
    public void setOutType(boolean outType) {
        this.outType = outType;
    }

    /**
     * code>RETURN</code>パラメータである場合に<code>true</code>を設定します。
     * 
     * @param returnType <code>RETURN</code>パラメータである場合<code>true</code>
     */
    public void setReturnType(boolean returnType) {
        this.returnType = returnType;
    }

}
