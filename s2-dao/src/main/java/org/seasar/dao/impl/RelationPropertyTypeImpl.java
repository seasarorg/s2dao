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

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;
import org.seasar.extension.jdbc.impl.PropertyTypeImpl;
import org.seasar.framework.beans.PropertyDesc;

/**
 * @author higa
 * 
 */
public class RelationPropertyTypeImpl extends PropertyTypeImpl implements
        RelationPropertyType {

    protected int relationNo;

    protected String[] myKeys;

    protected String[] yourKeys;

    protected BeanMetaData beanMetaData;

    public RelationPropertyTypeImpl(PropertyDesc propertyDesc) {
        super(propertyDesc);
    }

    public RelationPropertyTypeImpl(PropertyDesc propertyDesc, int relationNo,
            String[] myKeys, String[] yourKeys, BeanMetaData beanMetaData) {

        super(propertyDesc);
        this.relationNo = relationNo;
        this.myKeys = myKeys;
        this.yourKeys = yourKeys;
        this.beanMetaData = beanMetaData;
    }

    public int getRelationNo() {
        return relationNo;
    }

    /**
     * @see org.seasar.dao.RelationPropertyType#getKeySize()
     */
    public int getKeySize() {
        if (myKeys.length > 0) {
            return myKeys.length;
        } else {
            return beanMetaData.getPrimaryKeySize();
        }

    }

    /**
     * @see org.seasar.dao.RelationPropertyType#getMyKey(int)
     */
    public String getMyKey(int index) {
        if (myKeys.length > 0) {
            return myKeys[index];
        } else {
            return beanMetaData.getPrimaryKey(index);
        }
    }

    /**
     * @see org.seasar.dao.RelationPropertyType#getYourKey(int)
     */
    public String getYourKey(int index) {
        if (yourKeys.length > 0) {
            return yourKeys[index];
        } else {
            return beanMetaData.getPrimaryKey(index);
        }
    }

    /**
     * @see org.seasar.dao.RelationPropertyType#isYourKey(java.lang.String)
     */
    public boolean isYourKey(String columnName) {
        for (int i = 0; i < getKeySize(); ++i) {
            if (columnName.equalsIgnoreCase(getYourKey(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.seasar.extension.jdbc.RelationPropertyType#getBeanMetaData()
     */
    public BeanMetaData getBeanMetaData() {
        return beanMetaData;
    }
}