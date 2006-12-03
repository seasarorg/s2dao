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

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.Dbms;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;

/**
 * @author jflute
 * @author manhole
 */
public class BeanMetaDataFactoryImpl implements BeanMetaDataFactory {

    protected AnnotationReaderFactory annotationReaderFactory;

    protected ValueTypeFactory valueTypeFactory;

    protected DataSource dataSource;

    public BeanMetaData createBeanMetaData(final Class beanClass) {
        return createBeanMetaData(beanClass, 0);
    }

    public BeanMetaData createBeanMetaData(final Class beanClass,
            final int relationNestLevel) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        final Connection con = DataSourceUtil.getConnection(dataSource);
        try {
            final DatabaseMetaData metaData = ConnectionUtil.getMetaData(con);
            return createBeanMetaData(metaData, beanClass, relationNestLevel);
        } finally {
            ConnectionUtil.close(con);
        }
    }

    protected BeanMetaData createBeanMetaData(
            final DatabaseMetaData dbMetaData, final Class beanClass,
            final int relationNestLevel) {

        final BeanMetaDataImpl bmd = createBeanMetaDataImpl();
        bmd.setBeanClass(beanClass);
        bmd.setDatabaseMetaData(dbMetaData);
        final Dbms dbms = getDbms();
        bmd.setDbms(dbms);
        final BeanAnnotationReader beanAnnotationReader = annotationReaderFactory
                .createBeanAnnotationReader(beanClass);
        bmd.setBeanAnnotationReader(beanAnnotationReader);
        bmd.setValueTypeFactory(valueTypeFactory);
        bmd
                .setStopRelationCreation(isLimitRelationNestLevel(relationNestLevel));
        bmd.setBeanMetaDataFactory(this);
        bmd.setRelationNestLevel(relationNestLevel);
        bmd.initialize();
        return bmd;
    }

    protected Dbms getDbms() {
        return DbmsManager.getDbms(dataSource);
    }

    protected BeanMetaDataImpl createBeanMetaDataImpl() {
        return new BeanMetaDataImpl();
    }

    protected boolean isLimitRelationNestLevel(final int relationNestLevel) {
        return relationNestLevel == getLimitRelationNestLevel();
    }

    protected int getLimitRelationNestLevel() {
        // You can change relation creation range by changing this.
        return 1;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setAnnotationReaderFactory(
            final AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    public void setValueTypeFactory(final ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

}
