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
package org.seasar.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.ArgumentDtoAnnotationReader;
import org.seasar.dao.IllegalParameterTypeRuntimeException;
import org.seasar.dao.IllegalSignatureRuntimeException;
import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureMetaDataFactory;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.util.TypeUtil;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * {@link ProcedureMetaDataFactory}の実装クラスです。
 * 
 * @author taedium
 */
public class ProcedureMetaDataFactoryImpl implements ProcedureMetaDataFactory {

    public static final String INIT_METHOD = "initialize";

    public static final String valueTypeFactory_BINDING = "bindingType=must";

    public static final String annotationReaderFactory_BINDING = "bindingType=must";

    protected static String procedureParameterInType = "in";

    protected static String procedureParameterOutType = "out";

    protected static String procedureParameterInOutType = "inOut";

    protected static String procedureParameterReturnType = "return";

    /** {@link ValueType}のファクトリ */
    protected ValueTypeFactory valueTypeFactory;

    /** アノテーションリーダのファクトリ */
    protected AnnotationReaderFactory annotationReaderFactory;

    /** メソッドの引数のDTOに対するアノテーションリーダ */
    protected ArgumentDtoAnnotationReader annotationReader;

    public void initialize() {
        annotationReader = annotationReaderFactory
                .createArgumentDtoAnnotationReader();
    }

    public void setAnnotationReaderFactory(
            final AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    public void setValueTypeFactory(final ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    public ProcedureMetaData createProcedureMetaData(
            final String procedureName, final Method method) {
        final ProcedureMetaDataImpl metaData = new ProcedureMetaDataImpl(
                procedureName);
        final Class dtoClass = getParameterType(method);
        if (dtoClass == null) {
            return metaData;
        } else {
            if (!isDtoType(dtoClass)) {
                throw new IllegalSignatureRuntimeException("EDAO0031", method
                        .toString());
            }
        }
        final BeanDesc dtoDesc = BeanDescFactory.getBeanDesc(dtoClass);
        final Field[] fields = TypeUtil.getDeclaredFields(dtoClass);
        for (int i = 0; i < fields.length; i++) {
            final Field field = fields[i];
            if (!isInstanceField(field)) {
                continue;
            }
            final ProcedureParameterType ppt = getProcedureParameterType(
                    dtoDesc, field);
            if (ppt == null) {
                continue;
            }
            metaData.addParameterType(ppt);
        }
        return metaData;
    }

    /**
     * パラメータの型を返します。
     * 
     * @param method
     *            メソッド
     * @return パラメータが1つのみ存在する場合はそのパラメータの型、存在しない場合は<code>null</code>
     */
    protected Class getParameterType(final Method method) {
        final Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            return null;
        } else if (parameterTypes.length == 1) {
            return parameterTypes[0];
        }
        throw new IllegalSignatureRuntimeException("EDAO0030", method
                .toString());
    }

    /**
     * プロシージャのパラメータのタイプを返します。
     * 
     * @param Bean記述
     * @param field
     *            フィールド
     * @return プロシージャのパラメータのタイプ、存在しない場合<code>null</code>
     */
    protected ProcedureParameterType getProcedureParameterType(
            final BeanDesc dtoDesc, final Field field) {
        final String type = annotationReader.getProcedureParameter(dtoDesc,
                field);
        if (type == null) {
            return null;
        }
        field.setAccessible(true);
        final ProcedureParameterType ppt = new ProcedureParameterTypeImpl(field);
        if (type.equalsIgnoreCase(procedureParameterInType)) {
            ppt.setInType(true);
        } else if (type.equalsIgnoreCase(procedureParameterOutType)) {
            ppt.setOutType(true);
        } else if (type.equalsIgnoreCase(procedureParameterInOutType)) {
            ppt.setInType(true);
            ppt.setOutType(true);
        } else if (type.equalsIgnoreCase(procedureParameterReturnType)) {
            ppt.setOutType(true);
            ppt.setReturnType(true);
        } else {
            throw new IllegalParameterTypeRuntimeException(type);
        }
        final ValueType valueType = getValueType(dtoDesc, field);
        ppt.setValueType(valueType);
        return ppt;
    }

    /**
     * {@link ValueType}を返します。
     * 
     * @param Bean記述
     * @param field
     *            フィールド
     * @return {@link ValueType}
     */
    protected ValueType getValueType(final BeanDesc dtoDesc, final Field field) {
        final String name = annotationReader.getValueType(dtoDesc, field);
        if (name != null) {
            return valueTypeFactory.getValueTypeByName(name);
        }
        final Class type = field.getType();
        return valueTypeFactory.getValueTypeByClass(type);
    }

    /**
     * <code>field</code>がインスタンスフィールドの場合<code>true</code>
     * 
     * @param field
     *            フィールド
     * @return <code>field</code>がインスタンスフィールドの場合<code>true</code>、そうでない場合
     *         <code>false</code>
     */
    protected boolean isInstanceField(final Field field) {
        final int mod = field.getModifiers();
        return !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
    }

    /**
     * DTOとみなすことができる型の場合<code>true</code>を返します。
     * 
     * @param clazz
     *            クラス
     * @return DTOとみなすことができる型の場合<code>true</code>、そうでない場合<code>false</code>
     */
    protected boolean isDtoType(final Class clazz) {
        return !TypeUtil.isSimpleType(clazz) && !isContainerType(clazz);
    }

    /**
     * コンテナを表すクラスの場合に<code>true</code>を返します。
     * 
     * @param clazz
     *            クラス
     * @return コンテナを表すクラスの場合<code>true</code>、そうでない場合<code>false</code>
     */
    protected boolean isContainerType(final Class clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        return Collection.class.isAssignableFrom(clazz)
                || Map.class.isAssignableFrom(clazz) || clazz.isArray();
    }

}
