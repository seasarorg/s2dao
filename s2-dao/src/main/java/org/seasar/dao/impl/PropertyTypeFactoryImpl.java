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

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.Dbms;
import org.seasar.dao.PropertyTypeFactory;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.PropertyTypeImpl;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
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
public class PropertyTypeFactoryImpl implements PropertyTypeFactory {

    private static Logger logger = Logger
            .getLogger(PropertyTypeFactoryImpl.class);

    protected Class beanClass;

    protected BeanAnnotationReader beanAnnotationReader;

    protected ValueTypeFactory valueTypeFactory;

    protected DatabaseMetaData databaseMetaData;

    protected Dbms dbms;

    public PropertyTypeFactoryImpl(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            ValueTypeFactory valueTypeFactory) {
        this.beanClass = beanClass;
        this.beanAnnotationReader = beanAnnotationReader;
        this.valueTypeFactory = valueTypeFactory;
    }

    public PropertyTypeFactoryImpl(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            ValueTypeFactory valueTypeFactory,
            DatabaseMetaData databaseMetaData, Dbms dbms) {
        this(beanClass, beanAnnotationReader, valueTypeFactory);
        this.databaseMetaData = databaseMetaData;
        this.dbms = dbms;
    }

    public PropertyType[] createPropertyTypes() {
        List list = new ArrayList();
        BeanDesc beanDesc = getBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            PropertyType pt = createPropertyType(pd);
            list.add(pt);
        }
        return (PropertyType[]) list.toArray(new PropertyType[list.size()]);
    }

    public PropertyType[] createPropertyTypes(String tableName) {
        List list = new ArrayList();
        BeanDesc beanDesc = getBeanDesc();
        boolean found = false;
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            if (isRelationProperty(pd)) {
                continue;
            }
            PropertyType pt = createPropertyType(pd);
            if (isPrimaryKey(pd)) {
                pt.setPrimaryKey(true);
                found = true;
            }
            list.add(pt);
        }
        PropertyType[] types = (PropertyType[]) list
                .toArray(new PropertyType[list.size()]);
        setupPropertyPersistentAndColumnName(tableName, types);
        if (!found) {
            setupPrimaryKey(tableName, types);
        }
        return types;
    }

    protected PropertyType createPropertyType(PropertyDesc propertyDesc) {
        final String columnName = getColumnName(propertyDesc);
        final ValueType valueType = getValueType(propertyDesc);
        return new PropertyTypeImpl(propertyDesc, valueType, columnName);
    }

    protected String getColumnName(PropertyDesc propertyDesc) {
        String name = beanAnnotationReader.getColumnAnnotation(propertyDesc);
        return name != null ? name : propertyDesc.getPropertyName();
    }

    protected ValueType getValueType(PropertyDesc propertyDesc) {
        final String valueTypeName = beanAnnotationReader
                .getValueType(propertyDesc);
        if (valueTypeName != null) {
            return valueTypeFactory.getValueTypeByName(valueTypeName);
        } else {
            return valueTypeFactory.getValueTypeByClass(propertyDesc
                    .getPropertyType());
        }
    }

    protected boolean isPrimaryKey(PropertyDesc propertyDesc) {
        return beanAnnotationReader.getId(propertyDesc, dbms) != null;
    }

    protected void setupPropertyPersistentAndColumnName(String tableName,
            PropertyType[] propertyTypes) {
        Set columnSet = DatabaseMetaDataUtil.getColumnMap(databaseMetaData,
                tableName).keySet();
        if (columnSet.isEmpty()) {
            logger.log("WDAO0002", new Object[] { tableName });
        }

        for (Iterator i = columnSet.iterator(); i.hasNext();) {
            String columnName = (String) i.next();
            String columnName2 = StringUtil.replace(columnName, "_", "");
            for (int j = 0; j < propertyTypes.length; ++j) {
                PropertyType pt = propertyTypes[j];
                if (pt.getColumnName().equalsIgnoreCase(columnName2)) {
                    final PropertyDesc pd = pt.getPropertyDesc();
                    if (beanAnnotationReader.getColumnAnnotation(pd) == null) {
                        pt.setColumnName(columnName);
                    }
                    break;
                }
            }
        }

        String[] props = beanAnnotationReader.getNoPersisteneProps();
        if (props != null) {
            for (int i = 0; i < props.length; ++i) {
                for (int j = 0; j < propertyTypes.length; j++) {
                    PropertyType pt = propertyTypes[j];
                    if (pt.getPropertyName().equals(props[i])) {
                        pt.setPersistent(false);
                    }
                }
            }
        }

        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            if (!columnSet.contains(pt.getColumnName())) {
                pt.setPersistent(false);
            }
        }
    }

    protected void setupPrimaryKey(String tableName,
            PropertyType[] propertyTypes) {
        Set primaryKeySet = DatabaseMetaDataUtil.getPrimaryKeySet(
                databaseMetaData, tableName);
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            if (primaryKeySet.contains(pt.getColumnName())) {
                pt.setPrimaryKey(true);
            }
        }
    }

    protected boolean isRelationProperty(PropertyDesc propertyDesc) {
        return beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    protected BeanDesc getBeanDesc() {
        return BeanDescFactory.getBeanDesc(beanClass);
    }

}
