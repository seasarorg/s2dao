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

import java.sql.DatabaseMetaData;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.dbms.DbmsManager;

/**
 * @author jflute
 */
public class BeanMetaDataFactoryImpl implements BeanMetaDataFactory {

    protected AnnotationReaderFactory annotationReaderFactory;

    protected ValueTypeFactory valueTypeFactory;

    public void setAnnotationReaderFactory(
            AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    public void setValueTypeFactory(ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    public BeanMetaData createBeanMetaData(final DatabaseMetaData dbMetaData,
            final Class beanClass) {
        return createBeanMetaData(dbMetaData, beanClass, 0);
    }

    public BeanMetaData createBeanMetaData(final DatabaseMetaData dbMetaData,
            final Class beanClass, final int relationNestLevel) {
        BeanMetaDataImpl beanMetaDataImpl = newBeanMetaDataImpl();
        beanMetaDataImpl.setBeanClass(beanClass);
        beanMetaDataImpl.setDatabaseMetaData(dbMetaData);
        beanMetaDataImpl.setDbms(DbmsManager.getDbms(dbMetaData));
        beanMetaDataImpl.setAnnotationReaderFactory(annotationReaderFactory);
        beanMetaDataImpl.setValueTypeFactory(valueTypeFactory);
        beanMetaDataImpl.setStopRelationCreation(isLimitRelationNestLevel(relationNestLevel));
        beanMetaDataImpl.setBeanMetaDataFactory(this);
        beanMetaDataImpl.setRelationNestLevel(relationNestLevel);
        beanMetaDataImpl.initialize();
        
        return beanMetaDataImpl;
    }

    protected BeanMetaDataImpl newBeanMetaDataImpl() {
        return new BeanMetaDataImpl();
    }
    
    protected boolean isLimitRelationNestLevel(final int relationNestLevel) {
        return relationNestLevel == getLimitRelationNestLevel();
    }
    
    protected int getLimitRelationNestLevel() {
        // You can change relation creation range by changing this.
        return 1;
    }
}
