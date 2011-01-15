/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.ColumnNaming;
import org.seasar.dao.Dbms;
import org.seasar.dao.PropertyTypeFactory;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.exception.EmptyRuntimeException;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.StringUtil;

/**
 * {@link PropertyTypeFactory}の実装クラスです。
 * <p>
 * データベースのメタデータ情報を利用して{@link PropertyType}を作成します。
 * </p>
 * 
 * @author taedium
 */
public class PropertyTypeFactoryImpl extends AbstractPropertyTypeFactory {

    private static Logger logger = Logger
            .getLogger(PropertyTypeFactoryImpl.class);

    private DatabaseMetaData databaseMetaData;

    /**
     * インスタンスを構築します。
     * 
     * @param beanClass Beanのクラス
     * @param beanAnnotationReader Beanのアノテーションリーダ
     * @param valueTypeFactory {@link ValueType}のファクトリ
     * @param columnNaming カラムのネーミング
     */
    public PropertyTypeFactoryImpl(Class beanClass,
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
     * @param dbms DBMS
     * @param databaseMetaData データベースのメタ情報
     */
    public PropertyTypeFactoryImpl(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            ValueTypeFactory valueTypeFactory, ColumnNaming columnNaming,
            Dbms dbms, DatabaseMetaData databaseMetaData) {
        super(beanClass, beanAnnotationReader, valueTypeFactory, columnNaming,
                dbms);
        this.databaseMetaData = databaseMetaData;
    }

    public PropertyType[] createBeanPropertyTypes(String tableName) {
        List list = new ArrayList();
        BeanDesc beanDesc = getBeanDesc();
        Set columns = getColumns(tableName);
        boolean found = false;
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            if (isRelation(pd)) {
                continue;
            }
            PropertyType pt = createPropertyType(pd);
            if (isPrimaryKey(pd)) {
                pt.setPrimaryKey(true);
                found = true;
            }
            setupColumnName(pt, columns);
            setupPersistent(pt, columns);
            list.add(pt);
        }
        PropertyType[] propertyTypes = (PropertyType[]) list
                .toArray(new PropertyType[list.size()]);
        if (!found) {
            setupPrimaryKey(propertyTypes, tableName);
        }
        return propertyTypes;
    }

    /**
     * カラム名のセットを返します。
     * 
     * @param tableName
     * @return カラム名のセット
     */
    protected Set getColumns(String tableName) {
        Set columnSet = DatabaseMetaDataUtil.getColumnMap(
                getDatabaseMetaData(), tableName).keySet();
        if (columnSet.isEmpty()) {
            logger.log("WDAO0002", new Object[] { tableName });
        }
        return columnSet;
    }

    /**
     * <code>propertyType</code>にカラム名を設定します。
     * 
     * @param propertyType {@link PropertyType}
     * @param columns カラム名のセット
     */
    protected void setupColumnName(PropertyType propertyType, Set columns) {
        final PropertyDesc pd = propertyType.getPropertyDesc();
        if (beanAnnotationReader.getColumnAnnotation(pd) != null) {
            return;
        }
        for (Iterator i = columns.iterator(); i.hasNext();) {
            String columnName = (String) i.next();
            String columnName2 = StringUtil.replace(columnName, "_", "");
            if (propertyType.getColumnName().equalsIgnoreCase(columnName2)) {
                propertyType.setColumnName(columnName);
                break;
            }
        }
    }

    /**
     * <code>propertyType</code>が永続化されるかどうかを設定します。
     * 
     * @param propertyType {@link PropertyType}
     * @param columns カラム名のセット
     */
    protected void setupPersistent(PropertyType propertyType, Set columns) {
        propertyType.setPersistent(isPersistent(propertyType));
        if (!columns.contains(propertyType.getColumnName())) {
            propertyType.setPersistent(false);
        }
    }

    /**
     * <code>propertyTypes</code>の各要素に主キーであるかどうかを設定します。
     * 
     * @param propertyTypes {@link PropertyType}の配列
     * @param tableName テーブル名
     */
    protected void setupPrimaryKey(PropertyType[] propertyTypes,
            String tableName) {
        Set primaryKeySet = DatabaseMetaDataUtil.getPrimaryKeySet(
                getDatabaseMetaData(), tableName);
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            if (primaryKeySet.contains(pt.getColumnName())) {
                pt.setPrimaryKey(true);
            }
        }
    }

    /**
     * データベースのメタ情報を返します。
     * 
     * @return データベースのメタ情報
     */
    protected DatabaseMetaData getDatabaseMetaData() {
        if (databaseMetaData == null) {
            throw new EmptyRuntimeException("databaseMetaData");
        }
        return databaseMetaData;
    }
}
