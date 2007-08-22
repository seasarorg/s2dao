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
 * @author taedium
 *
 */
public class ProcedureParameterTypeImpl implements ProcedureParameterType {

    private String parameterName;

    private ValueType valueType;

    private int sqlType;

    private boolean bindable;

    private boolean registerable;

    public ProcedureParameterTypeImpl() {
    }

    public String getParameterName() {
        return parameterName;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public int getSqlType() {
        return sqlType;
    }

    public boolean isRegisterable() {
        return registerable;
    }

    public boolean isBindable() {
        return bindable;
    }

    public void setBindable(boolean bindable) {
        this.bindable = bindable;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setRegisterable(boolean registerable) {
        this.registerable = registerable;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

}
