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

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.BeanEnhancer;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.ColumnNaming;
import org.seasar.dao.DaoNamingConvention;
import org.seasar.dao.Dbms;
import org.seasar.dao.NullBean;
import org.seasar.dao.PropertyTypeFactory;
import org.seasar.dao.PropertyTypeFactoryBuilder;
import org.seasar.dao.RelationPropertyTypeFactory;
import org.seasar.dao.RelationPropertyTypeFactoryBuilder;
import org.seasar.dao.TableNaming;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;

/**
 * @author jflute
 * @author manhole
 */
public class BeanMetaDataFactoryImpl implements BeanMetaDataFactory {

    public static final String annotationReaderFactory_BINDING = "bindingType=must";

    public static final String valueTypeFactory_BINDING = "bindingType=must";

    public static final String dataSource_BINDING = "bindingType=must";

    public static final String daoNamingConvention_BINDING = "bindingType=must";

    public static final String beanEnhancer_BINDING = "bindingType=must";

    protected AnnotationReaderFactory annotationReaderFactory;

    protected ValueTypeFactory valueTypeFactory;

    protected DataSource dataSource;

    protected DaoNamingConvention daoNamingConvention;

    protected BeanEnhancer beanEnhancer;

    public static final String tableNaming_BINDING = "bindingType=may";

    protected TableNaming tableNaming = new DefaultTableNaming();

    public static final String propertyTypeFactoryBuilder_BINDING = "bindingType=may";

    protected PropertyTypeFactoryBuilder propertyTypeFactoryBuilder = new PropertyTypeFactoryBuilderImpl();

    public static final String relationPropertyTypeFactoryBuilder_BINDING = "bindingType=may";

    protected RelationPropertyTypeFactoryBuilder relationPropertyTypeFactoryBuilder = new RelationPropertyTypeFactoryBuilderImpl();

    public static final String columnNaming_BINDING = "bindingType=may";

    protected ColumnNaming columnNaming = new DefaultColumnNaming();

    public BeanMetaData createBeanMetaData(final Class daoInterface,
            final Class beanClass) {
        if (NullBean.class == beanClass) {
            return new NullBeanMetaData(daoInterface);
        }
        return createBeanMetaData(beanClass);
    }

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

    public BeanMetaData createBeanMetaData(final DatabaseMetaData dbMetaData,
            final Class beanClass, final int relationNestLevel) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        final BeanEnhancer enhancer = getBeanEnhancer();
        final Class originalBeanClass = enhancer.getOriginalClass(beanClass);
        final Dbms dbms = getDbms(dbMetaData);
        final boolean stopRelationCreation = isLimitRelationNestLevel(relationNestLevel);
        final BeanAnnotationReader bar = annotationReaderFactory
                .createBeanAnnotationReader(originalBeanClass);
        final String versionNoPropertyName = getVersionNoPropertyName(bar);
        final String timestampPropertyName = getTimestampPropertyName(bar);
        final PropertyTypeFactory ptf = createPropertyTypeFactory(
                originalBeanClass, bar, dbMetaData, dbms);
        final RelationPropertyTypeFactory rptf = createRelationPropertyTypeFactory(
                originalBeanClass, bar, this, dbMetaData, relationNestLevel,
                stopRelationCreation);
        final BeanMetaDataImpl bmd = createBeanMetaDataImpl();

        bmd.setDbms(dbms);
        bmd.setBeanAnnotationReader(bar);
        bmd.setVersionNoPropertyName(versionNoPropertyName);
        bmd.setTimestampPropertyName(timestampPropertyName);
        bmd.setBeanClass(originalBeanClass);
        bmd.setTableNaming(tableNaming);
        bmd.setPropertyTypeFactory(ptf);
        bmd.setRelationPropertyTypeFactory(rptf);
        bmd.initialize();

        final Class enhancedBeanClass = enhancer.enhanceBeanClass(beanClass,
                versionNoPropertyName, timestampPropertyName);
        bmd.setModifiedPropertySupport(enhancer.getSupporter());
        bmd.setBeanClass(enhancedBeanClass);

        return bmd;
    }

    protected String getVersionNoPropertyName(
            BeanAnnotationReader beanAnnotationReader) {
        final String defaultName = getDaoNamingConvention()
                .getVersionNoPropertyName();
        final String name = beanAnnotationReader.getVersionNoPropertyName();
        return name != null ? name : defaultName;
    }

    protected String getTimestampPropertyName(
            BeanAnnotationReader beanAnnotationReader) {
        final String defaultName = getDaoNamingConvention()
                .getTimestampPropertyName();
        final String name = beanAnnotationReader.getTimestampPropertyName();
        return name != null ? name : defaultName;
    }

    protected PropertyTypeFactory createPropertyTypeFactory(
            Class originalBeanClass, BeanAnnotationReader beanAnnotationReader,
            DatabaseMetaData databaseMetaData, Dbms dbms) {
        return propertyTypeFactoryBuilder.build(originalBeanClass,
                beanAnnotationReader, valueTypeFactory, columnNaming, dbms,
                databaseMetaData);
    }

    protected RelationPropertyTypeFactory createRelationPropertyTypeFactory(
            Class originalBeanClass, BeanAnnotationReader beanAnnotationReader,
            BeanMetaDataFactory beanMetaDataFactory,
            DatabaseMetaData databaseMetaData, int relationNestLevel,
            boolean isStopRelationCreation) {
        return relationPropertyTypeFactoryBuilder.build(originalBeanClass,
                beanAnnotationReader, beanMetaDataFactory, databaseMetaData,
                relationNestLevel, isStopRelationCreation);
    }

    protected Dbms getDbms() {
        return DbmsManager.getDbms(dataSource);
    }

    protected Dbms getDbms(DatabaseMetaData dbMetaData) {
        return DbmsManager.getDbms(dbMetaData);
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

    public DaoNamingConvention getDaoNamingConvention() {
        return daoNamingConvention;
    }

    public void setDaoNamingConvention(
            final DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
    }

    public BeanEnhancer getBeanEnhancer() {
        return beanEnhancer;
    }

    public void setBeanEnhancer(final BeanEnhancer beanEnhancer) {
        this.beanEnhancer = beanEnhancer;
    }

    public TableNaming getTableNaming() {
        return tableNaming;
    }

    public void setTableNaming(TableNaming tableNameConverter) {
        this.tableNaming = tableNameConverter;
    }

    public void setPropertyTypeFactoryBuilder(
            PropertyTypeFactoryBuilder propertyTypeFactoryBuilder) {
        this.propertyTypeFactoryBuilder = propertyTypeFactoryBuilder;
    }

    public void setRelationPropertyTypeFactoryBuilder(
            RelationPropertyTypeFactoryBuilder relationPropertyTypeFactoryBuilder) {
        this.relationPropertyTypeFactoryBuilder = relationPropertyTypeFactoryBuilder;
    }

}
