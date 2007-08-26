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
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.exception.EmptyRuntimeException;

/**
 * {@link ProcedureParameterType}の実装クラスです。
 * 
 * @author taedium
 */
public class ProcedureParameterTypeImpl implements ProcedureParameterType {

    private String parameterName;

    private PropertyDesc propertyDesc;

    private ValueType valueType;

    private boolean inType;

    private boolean outType;

    private boolean returnType;

    private Integer index;

    /**
     * インスタンスを構築します。
     * 
     */
    public ProcedureParameterTypeImpl() {
    }

    /**
     * インスタンスを構築します。
     * 
     * @param propertyDesc プロパティ記述
     */
    public ProcedureParameterTypeImpl(final PropertyDesc propertyDesc) {
        this.propertyDesc = propertyDesc;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(final String parameterName) {
        this.parameterName = parameterName;
    }

    public PropertyDesc getPropertyDesc() {
        if (propertyDesc == null) {
            throw new EmptyRuntimeException("propertyDesc");
        }
        return propertyDesc;
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

    public void setValueType(final ValueType valueType) {
        this.valueType = valueType;
    }

    public void setInType(final boolean inType) {
        this.inType = inType;
    }

    public void setOutType(final boolean outType) {
        this.outType = outType;
    }

    public void setReturnType(final boolean returnType) {
        this.returnType = returnType;
    }

    public boolean hasIndex() {
        return index != null;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(final Integer index) {
        this.index = index;
    }

}
