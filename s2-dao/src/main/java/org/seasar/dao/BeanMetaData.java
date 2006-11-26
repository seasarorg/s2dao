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
package org.seasar.dao;

import org.seasar.extension.jdbc.ColumnNotFoundRuntimeException;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;

/**
 * @author higa
 * 
 */
public interface BeanMetaData extends DtoMetaData {

    public String getTableName();

    public PropertyType getVersionNoPropertyType()
            throws PropertyNotFoundRuntimeException;

    public String getVersionNoPropertyName();

    public boolean hasVersionNoPropertyType();

    public PropertyType getTimestampPropertyType()
            throws PropertyNotFoundRuntimeException;

    public String getTimestampPropertyName();

    public boolean hasTimestampPropertyType();

    public String convertFullColumnName(String alias);

    public PropertyType getPropertyTypeByAliasName(String aliasName)
            throws ColumnNotFoundRuntimeException;

    public PropertyType getPropertyTypeByColumnName(String columnName)
            throws ColumnNotFoundRuntimeException;

    public boolean hasPropertyTypeByColumnName(String columnName);

    public boolean hasPropertyTypeByAliasName(String aliasName);

    public int getRelationPropertyTypeSize();

    public RelationPropertyType getRelationPropertyType(int index);

    public RelationPropertyType getRelationPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException;

    public int getPrimaryKeySize();

    public String getPrimaryKey(int index);

    public IdentifierGenerator getIdentifierGenerator();

    public String getAutoSelectList();

    public boolean isStopRelationCreation();
}