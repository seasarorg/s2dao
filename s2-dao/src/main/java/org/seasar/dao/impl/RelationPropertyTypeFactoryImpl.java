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
import java.util.List;
import java.util.StringTokenizer;

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.BeanEnhancer;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.RelationPropertyTypeFactory;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.beans.impl.PropertyDescImpl;

/**
 * {@link RelationPropertyTypeFactory}の実装クラスです。
 * 
 * @author taedium
 */
public class RelationPropertyTypeFactoryImpl implements
        RelationPropertyTypeFactory {

    protected Class beanClass;

    protected BeanAnnotationReader beanAnnotationReader;

    protected BeanMetaDataFactory beanMetaDataFactory;

    protected DatabaseMetaData databaseMetaData;

    protected int relationNestLevel;

    protected boolean isStopRelationCreation;

    protected BeanEnhancer beanEnhancer;

    public RelationPropertyTypeFactoryImpl(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            BeanMetaDataFactory beanMetaDataFactory,
            DatabaseMetaData databaseMetaData, int relationNestLevel,
            boolean isStopRelationCreation, BeanEnhancer beanEnhancer) {
        this.beanClass = beanClass;
        this.beanAnnotationReader = beanAnnotationReader;
        this.beanMetaDataFactory = beanMetaDataFactory;
        this.databaseMetaData = databaseMetaData;
        this.relationNestLevel = relationNestLevel;
        this.isStopRelationCreation = isStopRelationCreation;
        this.beanEnhancer = beanEnhancer;
    }

    public RelationPropertyType[] createRelationPropertyTypes() {
        List list = new ArrayList();
        BeanDesc beanDesc = getBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            if (isStopRelationCreation || !isRelationProperty(pd)) {
                continue;
            }
            RelationPropertyType rpt = createRelationPropertyType(pd);
            list.add(rpt);
        }
        return (RelationPropertyType[]) list
                .toArray(new RelationPropertyType[list.size()]);
    }

    protected RelationPropertyType createRelationPropertyType(
            PropertyDesc propertyDesc) {

        String[] myKeys = new String[0];
        String[] yourKeys = new String[0];
        int relno = beanAnnotationReader.getRelationNo(propertyDesc);
        String relkeys = beanAnnotationReader.getRelationKey(propertyDesc);
        if (relkeys != null) {
            StringTokenizer st = new StringTokenizer(relkeys, " \t\n\r\f,");
            List myKeyList = new ArrayList();
            List yourKeyList = new ArrayList();
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                int index = token.indexOf(':');
                if (index > 0) {
                    myKeyList.add(token.substring(0, index));
                    yourKeyList.add(token.substring(index + 1));
                } else {
                    myKeyList.add(token);
                    yourKeyList.add(token);
                }
            }
            myKeys = (String[]) myKeyList.toArray(new String[myKeyList.size()]);
            yourKeys = (String[]) yourKeyList.toArray(new String[yourKeyList
                    .size()]);
        }
        final BeanMetaData beanMetaData = createRelationBeanMetaData(propertyDesc
                .getPropertyType());
        PropertyDesc enhancedPd = null;
        if (beanEnhancer.isEnhancedClass(beanMetaData.getBeanClass())) {
            enhancedPd = new PropertyDescImpl(propertyDesc.getPropertyName(),
                    beanMetaData.getBeanClass(), propertyDesc.getReadMethod(),
                    propertyDesc.getWriteMethod(), getBeanDesc());
        } else {
            enhancedPd = propertyDesc;
        }
        final RelationPropertyType rpt = new RelationPropertyTypeImpl(
                enhancedPd, relno, myKeys, yourKeys, beanMetaData);
        return rpt;
    }

    protected BeanMetaData createRelationBeanMetaData(
            final Class relationBeanClass) {
        return beanMetaDataFactory.createBeanMetaData(databaseMetaData,
                relationBeanClass, relationNestLevel + 1);
    }

    protected boolean isRelationProperty(PropertyDesc propertyDesc) {
        return beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    protected BeanDesc getBeanDesc() {
        return BeanDescFactory.getBeanDesc(beanClass);
    }

}
