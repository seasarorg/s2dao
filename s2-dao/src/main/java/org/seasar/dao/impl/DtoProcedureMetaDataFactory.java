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

import org.seasar.dao.ArgumentDtoAnnotationReader;
import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureMetaDataFactory;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * DTOからプロシージャのメタデータを取得する{@link ProcedureMetaDataFactory}の実装クラスです。
 * 
 * @author taedium
 */
public class DtoProcedureMetaDataFactory implements ProcedureMetaDataFactory {

    /** プロシージャ名 */
    protected String procedureName;

    /** DTOのクラス記述 */
    protected BeanDesc dtoDesc;

    /** {@link ValueType}のファクトリ */
    protected ValueTypeFactory valueTypeFactory;

    /** 引数のDTOのアノテーションリーダ */
    protected ArgumentDtoAnnotationReader annotationReader;

    /**
     * インスタンスを構築します。
     * 
     * @param procedureName プロシージャ名
     * @param dtoDesc DTOのクラス記述
     * @param valueTypeFactory {@link ValueType}のファクトリ
     * @param annotationReader DTOのアノテーションリーダ
     */
    public DtoProcedureMetaDataFactory(final String procedureName, final Class dtoClass,
            final ValueTypeFactory valueTypeFactory,
            final ArgumentDtoAnnotationReader annotationReader) {
        this.procedureName = procedureName;
        this.dtoDesc = BeanDescFactory.getBeanDesc(dtoClass);
        this.valueTypeFactory = valueTypeFactory;
        this.annotationReader = annotationReader;
    }

    public ProcedureMetaData createProcedureMetaData() {
        final ProcedureMetaDataImpl metaData = new ProcedureMetaDataImpl(
                procedureName);
        for (int i = 0; i < dtoDesc.getPropertyDescSize(); i++) {
            final PropertyDesc pd = dtoDesc.getPropertyDesc(i);
            final ProcedureParameterType ppt = annotationReader
                    .getProcedureParameter(dtoDesc, pd);
            final ValueType valueType = getValueType(dtoDesc, pd);
            ppt.setValueType(valueType);
            metaData.addParameterType(ppt);
        }
        return metaData;
    }

    protected ValueType getValueType(final BeanDesc dtoDesc, final PropertyDesc propertyDesc) {
        final String name = annotationReader
                .getValueType(dtoDesc, propertyDesc);
        if (name != null) {
            return valueTypeFactory.getValueTypeByName(name);
        }
        final Class type = propertyDesc.getPropertyType();
        return valueTypeFactory.getValueTypeByClass(type);
    }

}
