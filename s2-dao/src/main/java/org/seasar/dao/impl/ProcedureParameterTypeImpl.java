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

import java.lang.reflect.Field;

import org.seasar.dao.ProcedureParameterType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.util.FieldUtil;

/**
 * {@link ProcedureParameterType}の実装クラスです。
 * 
 * @author taedium
 */
public class ProcedureParameterTypeImpl implements ProcedureParameterType {

    private String parameterName;

    private Field field;

    private ValueType valueType;

    private boolean inType;

    private boolean outType;

    private boolean returnType;

    /**
     * インスタンスを構築します。
     * 
     * @param field
     */
    public ProcedureParameterTypeImpl(Field field) {
        this.field = field;
        this.parameterName = field.getName();
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

    public Object getValue(Object target) {
        return FieldUtil.get(field, target);
    }

    public void setValue(Object target, Object value) {
        FieldUtil.set(field, target, value);
    }

}
