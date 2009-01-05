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

import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanNotFoundRuntimeException;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.dao.NullBean;
import org.seasar.dao.RelationPropertyType;
import org.seasar.extension.jdbc.ColumnNotFoundRuntimeException;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;

/**
 * @author taedium
 *
 */
public class NullBeanMetaData implements BeanMetaData {

    private Class daoInterface;

    public NullBeanMetaData(Class daoInterface) {
        this.daoInterface = daoInterface;
    }

    public String convertFullColumnName(String alias) {
        throwException();
        return null;
    }

    public String getAutoSelectList() {
        throwException();
        return null;
    }

    public Set getModifiedPropertyNames(Object bean) {
        throwException();
        return null;
    }

    public String getPrimaryKey(int index) {
        throwException();
        return null;
    }

    public int getPrimaryKeySize() {
        throwException();
        return 0;
    }

    public PropertyType getPropertyTypeByAliasName(String aliasName)
            throws ColumnNotFoundRuntimeException {
        throwException();
        return null;
    }

    public PropertyType getPropertyTypeByColumnName(String columnName)
            throws ColumnNotFoundRuntimeException {
        throwException();
        return null;
    }

    public RelationPropertyType getRelationPropertyType(int index) {
        throwException();
        return null;
    }

    public RelationPropertyType getRelationPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException {
        throwException();
        return null;
    }

    public int getRelationPropertyTypeSize() {
        throwException();
        return 0;
    }

    public String getTableName() {
        throwException();
        return null;
    }

    public String getTimestampPropertyName() {
        throwException();
        return null;
    }

    public PropertyType getTimestampPropertyType()
            throws PropertyNotFoundRuntimeException {
        throwException();
        return null;
    }

    public String getVersionNoPropertyName() {
        throwException();
        return null;
    }

    public PropertyType getVersionNoPropertyType()
            throws PropertyNotFoundRuntimeException {
        throwException();
        return null;
    }

    public boolean hasPropertyTypeByAliasName(String aliasName) {
        throwException();
        return false;
    }

    public boolean hasPropertyTypeByColumnName(String columnName) {
        throwException();
        return false;
    }

    public boolean hasTimestampPropertyType() {
        throwException();
        return false;
    }

    public boolean hasVersionNoPropertyType() {
        throwException();
        return false;
    }

    public Class getBeanClass() {
        return NullBean.class;
    }

    public PropertyType getPropertyType(int index) {
        throwException();
        return null;
    }

    public PropertyType getPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException {
        throwException();
        return null;
    }

    public int getPropertyTypeSize() {
        throwException();
        return 0;
    }

    public IdentifierGenerator getIdentifierGenerator(int index) {
        throwException();
        return null;
    }

    public IdentifierGenerator getIdentifierGenerator(String propertyName) {
        throwException();
        return null;
    }

    public int getIdentifierGeneratorSize() {
        throwException();
        return 0;
    }

    public boolean hasPropertyType(String propertyName) {
        throwException();
        return false;
    }

    protected void throwException() {
        throw new BeanNotFoundRuntimeException(daoInterface);
    }

}
