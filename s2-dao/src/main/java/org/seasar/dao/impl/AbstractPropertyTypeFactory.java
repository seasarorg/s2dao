/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

import java.util.ArrayList;
import java.util.List;

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.ColumnNaming;
import org.seasar.dao.Dbms;
import org.seasar.dao.PropertyTypeFactory;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.PropertyTypeImpl;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.exception.EmptyRuntimeException;

/**
 * {@link PropertyType}の抽象クラスです。
 * 
 * @author taedium
 */
public abstract class AbstractPropertyTypeFactory implements
        PropertyTypeFactory {

    protected Class beanClass;

    protected BeanAnnotationReader beanAnnotationReader;

    protected ValueTypeFactory valueTypeFactory;

    protected ColumnNaming columnNaming;

    private Dbms dbms;

    /**
     * インスタンスを構築します。
     * 
     * @param beanClass Beanのクラス
     * @param beanAnnotationReader Beanのアノテーションリーダ
     * @param valueTypeFactory {@link ValueType}のファクトリ
     * @param columnNaming カラムのネーミング
     */
    public AbstractPropertyTypeFactory(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            ValueTypeFactory valueTypeFactory, ColumnNaming columnNaming) {
        this.beanClass = beanClass;
        this.beanAnnotationReader = beanAnnotationReader;
        this.valueTypeFactory = valueTypeFactory;
        this.columnNaming = columnNaming;
    }

    /**
     * インスタンスを構築します。
     * 
     * @param beanClass Beanのクラス
     * @param beanAnnotationReader Beanのアノテーションリーダ
     * @param valueTypeFactory {@link ValueType}のファクトリ
     * @param columnNaming カラムのネーミング
     * @param dbms DBMS
     */
    public AbstractPropertyTypeFactory(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            ValueTypeFactory valueTypeFactory, ColumnNaming columnNaming,
            Dbms dbms) {
        this(beanClass, beanAnnotationReader, valueTypeFactory, columnNaming);
        this.dbms = dbms;
    }

    public PropertyType[] createDtoPropertyTypes() {
        List list = new ArrayList();
        BeanDesc beanDesc = getBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            PropertyType pt = createPropertyType(pd);
            list.add(pt);
        }
        return (PropertyType[]) list.toArray(new PropertyType[list.size()]);
    }

    /**
     * {@link BeanDesc}を返します。
     * 
     * @return {@link BeanDesc}
     */
    protected BeanDesc getBeanDesc() {
        return BeanDescFactory.getBeanDesc(beanClass);
    }

    /**
     * 関連を表すのプロパティである場合<code>true</code>を返します。
     * 
     * @param propertyDesc {@link PropertyDesc}
     * @return 関連を表すプロパティである場合<code>true</code>、そうでない場合<code>false</code>
     */
    protected boolean isRelation(PropertyDesc propertyDesc) {
        return beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    /**
     * 主キーを表すプロパティである場合<code>true</code>を返します。
     * 
     * @param propertyDesc {@link PropertyDesc}
     * @return　主キーを表すプロパティである場合<code>true</code>、そうでない場合<code>false</code>
     */
    protected boolean isPrimaryKey(PropertyDesc propertyDesc) {
        Dbms dbms = getDbms();
        return beanAnnotationReader.getId(propertyDesc, dbms) != null;
    }

    /**
     * 永続化されるプロパティである場合<code>true</code>を返します。
     * 
     * @param propertyType {@link PropertyType}
     * @return 永続化されるプロパティである場合<code>true</code>、そうでない場合<code>false</code>
     */
    protected boolean isPersistent(PropertyType propertyType) {
        String[] props = beanAnnotationReader.getNoPersisteneProps();
        if (props != null) {
            String propertyName = propertyType.getPropertyName();
            for (int i = 0; i < props.length; ++i) {
                if (props[i].equals(propertyName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * {@link PropertyType}を作成します。
     * 
     * @param propertyDesc　{@link PropertyDesc}
     * @return {@link PropertyType}
     */
    protected PropertyType createPropertyType(PropertyDesc propertyDesc) {
        final String columnName = getColumnName(propertyDesc);
        final ValueType valueType = getValueType(propertyDesc);
        return new PropertyTypeImpl(propertyDesc, valueType, columnName);
    }

    /**
     * カラム名を返します。
     * 
     * @param propertyDesc {@link PropertyDesc}
     * @return カラム名
     */
    protected String getColumnName(PropertyDesc propertyDesc) {
        String propertyName = propertyDesc.getPropertyName();
        String defaultName = fromPropertyNameToColumnName(propertyName);
        String name = beanAnnotationReader.getColumnAnnotation(propertyDesc);
        return name != null ? name : defaultName;
    }

    /**
     * プロパティ名をカラム名に変換します。
     * 
     * @param propertyName プロパティ名
     * @return カラム名
     */
    protected String fromPropertyNameToColumnName(String propertyName) {
        return columnNaming.fromPropertyNameToColumnName(propertyName);
    }

    /**
     * {@link ValueType}を返します。
     * 
     * @param propertyDesc {@link PropertyDesc}
     * @return　{@link ValueType}
     */
    protected ValueType getValueType(PropertyDesc propertyDesc) {
        final String name = beanAnnotationReader.getValueType(propertyDesc);
        if (name != null) {
            return valueTypeFactory.getValueTypeByName(name);
        }
        Class type = propertyDesc.getPropertyType();
        return valueTypeFactory.getValueTypeByClass(type);
    }

    /**
     * DBMSを返します。
     * 
     * @return DBMS
     */
    public Dbms getDbms() {
        if (dbms == null) {
            throw new EmptyRuntimeException("dbms");
        }
        return dbms;
    }

}
