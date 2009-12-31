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

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.framework.util.CaseInsensitiveMap;

/**
 * {@link ProcedureMetaData}の実装クラスです。
 * 
 * @author taedium
 */
public class ProcedureMetaDataImpl implements ProcedureMetaData {

    private String procedureName;

    private CaseInsensitiveMap parameterTypes = new CaseInsensitiveMap();

    private boolean returnType;

    public ProcedureMetaDataImpl(final String procedureName) {
        this.procedureName = procedureName;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public ProcedureParameterType getParameterType(final int index) {
        return (ProcedureParameterType) parameterTypes.get(index);
    }

    public ProcedureParameterType getParameterType(final String parameterName) {
        return (ProcedureParameterType) parameterTypes.get(parameterName);
    }

    public int getParameterTypeSize() {
        return parameterTypes.size();
    }

    public boolean hasReturnParameterType() {
        return returnType;
    }

    /**
     * パラメータのタイプを追加します。
     * 
     * @param parameterType パラメータのタイプ
     */
    public void addParameterType(final ProcedureParameterType parameterType) {
        final String name = parameterType.getParameterName();
        parameterTypes.put(name, parameterType);
        if (parameterType.isReturnType()) {
            returnType = true;
        }
    }

}
