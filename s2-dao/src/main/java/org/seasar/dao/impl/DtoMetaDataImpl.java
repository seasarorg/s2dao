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
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.PropertyTypeImpl;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.CaseInsensitiveMap;
import org.seasar.framework.util.ClassUtil;

/**
 * @author higa
 *  
 */
public class DtoMetaDataImpl implements DtoMetaData {

    private Class beanClass_;

    private CaseInsensitiveMap propertyTypes_ = new CaseInsensitiveMap();

    protected BeanAnnotationReader beanAnnotationReader_;

    protected DtoMetaDataImpl() {
    }

    public DtoMetaDataImpl(Class beanClass,
            BeanAnnotationReader beanAnnotationReader) {
        beanClass_ = beanClass;
        beanAnnotationReader_ = beanAnnotationReader;
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(beanClass);
        setupPropertyType(beanDesc);
    }

    /**
     * @see org.seasar.dao.DtoMetaData#getBeanClass()
     */
    public Class getBeanClass() {
        return beanClass_;
    }

    protected void setBeanClass(Class beanClass) {
        beanClass_ = beanClass;
    }

    /**
     * @see org.seasar.dao.DtoMetaData#getPropertyTypeSize()
     */
    public int getPropertyTypeSize() {
        return propertyTypes_.size();
    }

    /**
     * @see org.seasar.dao.DtoMetaData#getPropertyType(int)
     */
    public PropertyType getPropertyType(int index) {
        return (PropertyType) propertyTypes_.get(index);
    }

    /**
     * @see org.seasar.dao.DtoMetaData#getPropertyType(java.lang.String)
     */
    public PropertyType getPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException {

        PropertyType propertyType = (PropertyType) propertyTypes_
                .get(propertyName);
        if (propertyType == null) {
            throw new PropertyNotFoundRuntimeException(beanClass_, propertyName);
        }
        return propertyType;
    }

    /**
     * @see org.seasar.dao.DtoMetaData#hasPropertyType(java.lang.String)
     */
    public boolean hasPropertyType(String propertyName) {
        return propertyTypes_.get(propertyName) != null;
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
        String ca = beanAnnotationReader_.getColumnAnnotation(propertyDesc);
        if (ca != null) {
            columnName = ca;
        }
        ValueType valueType = getValueType(propertyDesc);
        PropertyType pt = new PropertyTypeImpl(propertyDesc, valueType,
                columnName);
        return pt;
    }

    protected ValueType getValueType(PropertyDesc propertyDesc) {
        final Class valueTypeClass = beanAnnotationReader_
                .getValueType(propertyDesc);
        if (valueTypeClass != null) {
            return (ValueType) ClassUtil.newInstance(valueTypeClass);
        } else {
            return ValueTypes.getValueType(propertyDesc.getPropertyType());
        }
    }

    protected void addPropertyType(PropertyType propertyType) {
        propertyTypes_.put(propertyType.getPropertyName(), propertyType);
    }
}