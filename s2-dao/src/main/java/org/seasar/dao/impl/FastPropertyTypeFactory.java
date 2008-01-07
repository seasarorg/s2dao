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
import org.seasar.dao.DaoNamingConvention;
import org.seasar.dao.Dbms;
import org.seasar.dao.PropertyTypeFactory;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.exception.EmptyRuntimeException;

/**
 * クラスのメタ情報のみを使用し高速に{@link PropertyType}を生成する{@link PropertyTypeFactory}の実装クラスです。
 * <p>
 * データベースのメタ情報を使用しないため{@link PropertyTypeFactoryImpl}よりも高速です。
 * </p>
 * 
 * @author taedium
 */
public class FastPropertyTypeFactory extends AbstractPropertyTypeFactory {

    private DaoNamingConvention daoNamingConvention;

    /**
     * インスタンスを構築します。
     * 
     * @param beanClass Beanのクラス
     * @param beanAnnotationReader Beanのアノテーションリーダ
     * @param valueTypeFactory {@link ValueType}のファクトリ
     * @param columnNaming カラムのネーミング
     */
    public FastPropertyTypeFactory(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            ValueTypeFactory valueTypeFactory, ColumnNaming columnNaming) {
        super(beanClass, beanAnnotationReader, valueTypeFactory, columnNaming);
    }

    /**
     * インスタンスを構築します。
     * 
     * @param beanClass Beanのクラス
     * @param beanAnnotationReader Beanのアノテーションリーダ
     * @param valueTypeFactory {@link ValueType}のファクトリ
     * @param columnNaming カラムのネーミング
     * @param daoNamingConvention Daoのネーミング規約
     * @param dbms DBMS
     */
    public FastPropertyTypeFactory(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            ValueTypeFactory valueTypeFactory, ColumnNaming columnNaming,
            DaoNamingConvention daoNamingConvention, Dbms dbms) {
        super(beanClass, beanAnnotationReader, valueTypeFactory, columnNaming,
                dbms);
        this.daoNamingConvention = daoNamingConvention;
    }

    public PropertyType[] createBeanPropertyTypes(String tableName) {
        List list = new ArrayList();
        BeanDesc beanDesc = getBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            if (isRelation(pd)) {
                continue;
            }
            PropertyType pt = createPropertyType(pd);
            pt.setPrimaryKey(isPrimaryKey(pd));
            pt.setPersistent(isPersistent(pt));
            list.add(pt);
        }
        return (PropertyType[]) list.toArray(new PropertyType[list.size()]);
    }

    protected boolean isPersistent(PropertyType propertyType) {
        DaoNamingConvention convention = getDaoNamingConvention();
        String propertyName = propertyType.getPropertyName();
        if (propertyName.equals(convention
                .getModifiedPropertyNamesPropertyName())) {
            return false;
        }
        return super.isPersistent(propertyType);
    }

    /**
     * Daoのネーミング規約を返します。
     * 
     * @return Daoのネーミング規約
     */
    public DaoNamingConvention getDaoNamingConvention() {
        if (daoNamingConvention == null) {
            throw new EmptyRuntimeException("daoNamingConvention");
        }
        return daoNamingConvention;
    }

}
