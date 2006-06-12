/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.DtoMetaData;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.PropertyTypeImpl;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.CaseInsensitiveMap;

/**
 * @author higa
 * 
 */
public class DtoMetaDataImpl implements DtoMetaData {

    private Class beanClass;

    private CaseInsensitiveMap propertyTypes = new CaseInsensitiveMap();

    protected BeanAnnotationReader beanAnnotationReader;

    private ValueTypeFactory valueTypeFactory;

    public DtoMetaDataImpl() {
    }

    /**
     * @deprecated
     */
    public DtoMetaDataImpl(Class beanClass,
            BeanAnnotationReader beanAnnotationReader) {
        setBeanClass(beanClass);
        setBeanAnnotationReader(beanAnnotationReader);
        initialize();
    }

    public void initialize() {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(getBeanClass());
        setupPropertyType(beanDesc);
    }

    /**
     * @see org.seasar.dao.DtoMetaData#getBeanClass()
     */
    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * @see org.seasar.dao.DtoMetaData#getPropertyTypeSize()
     */
    public int getPropertyTypeSize() {
        return propertyTypes.size();
    }

    /**
     * @see org.seasar.dao.DtoMetaData#getPropertyType(int)
     */
    public PropertyType getPropertyType(int index) {
        return (PropertyType) propertyTypes.get(index);
    }

    /**
     * @see org.seasar.dao.DtoMetaData#getPropertyType(java.lang.String)
     */
    public PropertyType getPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException {

        PropertyType propertyType = (PropertyType) propertyTypes
                .get(propertyName);
        if (propertyType == null) {
            throw new PropertyNotFoundRuntimeException(beanClass, propertyName);
        }
        return propertyType;
    }

    /**
     * @see org.seasar.dao.DtoMetaData#hasPropertyType(java.lang.String)
     */
    public boolean hasPropertyType(String propertyName) {
        return propertyTypes.get(propertyName) != null;
    }

    protected void setupPropertyType(BeanDesc beanDesc) {
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            PropertyType pt = createPropertyType(beanDesc, pd);
            addPropertyType(pt);
        }
    }

    protected PropertyType createPropertyType(BeanDesc beanDesc,
            PropertyDesc propertyDesc) {

        String columnName = propertyDesc.getPropertyName();
        String ca = beanAnnotationReader.getColumnAnnotation(propertyDesc);
        if (ca != null) {
            columnName = ca;
        }
        ValueType valueType = getValueType(propertyDesc);
        PropertyType pt = new PropertyTypeImpl(propertyDesc, valueType,
                columnName);
        return pt;
    }

    protected ValueType getValueType(PropertyDesc propertyDesc) {
        final String valueTypeName = beanAnnotationReader
                .getValueType(propertyDesc);
        if (valueTypeName != null) {
            return getValueTypeFactory().getValueTypeByName(valueTypeName);
        } else {
            return getValueTypeFactory().getValueTypeByClass(
                    propertyDesc.getPropertyType());
        }
    }

    protected void addPropertyType(PropertyType propertyType) {
        propertyTypes.put(propertyType.getPropertyName(), propertyType);
    }

    public void setBeanAnnotationReader(
            BeanAnnotationReader beanAnnotationReader) {
        this.beanAnnotationReader = beanAnnotationReader;
    }

    protected ValueTypeFactory getValueTypeFactory() {
        return valueTypeFactory;
    }

    public void setValueTypeFactory(ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

}
