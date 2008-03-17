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
package org.seasar.dao.unit;

import java.sql.DatabaseMetaData;
import java.util.List;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.BeanEnhancer;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.ColumnNaming;
import org.seasar.dao.DaoAnnotationReader;
import org.seasar.dao.DaoNamingConvention;
import org.seasar.dao.Dbms;
import org.seasar.dao.DtoMetaDataFactory;
import org.seasar.dao.ProcedureMetaDataFactory;
import org.seasar.dao.PropertyTypeFactory;
import org.seasar.dao.PropertyTypeFactoryBuilder;
import org.seasar.dao.RelationPropertyTypeFactoryBuilder;
import org.seasar.dao.ResultSetHandlerFactory;
import org.seasar.dao.TableNaming;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.dao.impl.BeanEnhancerImpl;
import org.seasar.dao.impl.BeanMetaDataFactoryImpl;
import org.seasar.dao.impl.DaoMetaDataImpl;
import org.seasar.dao.impl.DaoNamingConventionImpl;
import org.seasar.dao.impl.DefaultColumnNaming;
import org.seasar.dao.impl.DefaultTableNaming;
import org.seasar.dao.impl.DtoMetaDataFactoryImpl;
import org.seasar.dao.impl.DtoMetaDataImpl;
import org.seasar.dao.impl.FieldAnnotationReaderFactory;
import org.seasar.dao.impl.ProcedureMetaDataFactoryImpl;
import org.seasar.dao.impl.PropertyTypeFactoryBuilderImpl;
import org.seasar.dao.impl.RelationPropertyTypeFactoryBuilderImpl;
import org.seasar.dao.impl.ResultSetHandlerFactorySelector;
import org.seasar.dao.impl.ValueTypeFactoryImpl;
import org.seasar.dao.pager.PagerContext;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.jdbc.impl.BasicResultSetFactory;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author higa
 * @author manhole
 * @author jflute
 */
public abstract class S2DaoTestCase extends S2TestCase {

    private ValueTypeFactory valueTypeFactory;

    private AnnotationReaderFactory annotationReaderFactory;

    private BeanMetaDataFactory beanMetaDataFactory;

    private DaoNamingConvention daoNamingConvention;

    private Dbms dbms;

    private BeanEnhancer beanEnhancer;

    private ResultSetHandlerFactory resultSetHandlerFactory;

    private DtoMetaDataFactory dtoMetaDataFactory;

    private PropertyTypeFactoryBuilder propertyTypeFactoryBuilder;

    private RelationPropertyTypeFactoryBuilder relationPropertyTypeFactoryBuilder;

    private TableNaming tableNaming;

    private ColumnNaming columnNaming;

    private ProcedureMetaDataFactory procedureMetaDataFactory;

    public S2DaoTestCase() {
    }

    public S2DaoTestCase(final String name) {
        super(name);
    }

    protected void tearDown() throws Exception {
        valueTypeFactory = null;
        annotationReaderFactory = null;
        beanMetaDataFactory = null;
        dbms = null;
        beanEnhancer = null;
        resultSetHandlerFactory = null;
        dtoMetaDataFactory = null;
        propertyTypeFactoryBuilder = null;
        relationPropertyTypeFactoryBuilder = null;
        tableNaming = null;
        columnNaming = null;
        procedureMetaDataFactory = null;
        PagerContext.end();
        super.tearDown();
    }

    protected void assertBeanEquals(final String message,
            final DataSet expected, final Object bean) {

        final S2DaoBeanReader reader = new S2DaoBeanReader(bean,
                createBeanMetaData(bean.getClass()));
        assertEquals(message, expected, reader.read());
    }

    protected void assertBeanListEquals(final String message,
            final DataSet expected, final List list) {

        final S2DaoBeanListReader reader = new S2DaoBeanListReader(list,
                createBeanMetaData(list.get(0).getClass()));
        assertEquals(message, expected, reader.read());
    }

    protected BeanMetaData createBeanMetaData(final Class beanClass) {
        final BeanMetaDataFactory factory = getBeanMetaDataFactory();
        return factory.createBeanMetaData(beanClass);
    }

    protected DtoMetaDataImpl createDtoMetaData(final Class dtoClass) {
        final DtoMetaDataImpl dmd = new DtoMetaDataImpl();
        final BeanAnnotationReader reader = getAnnotationReaderFactory()
                .createBeanAnnotationReader(dtoClass);
        final PropertyTypeFactoryBuilder builder = getPropertyTypeFactoryBuilder();
        final PropertyTypeFactory propertyTypeFactory = builder.build(dtoClass,
                reader);
        dmd.setBeanClass(dtoClass);
        dmd.setBeanAnnotationReader(getAnnotationReaderFactory()
                .createBeanAnnotationReader(dtoClass));
        dmd.setPropertyTypeFactory(propertyTypeFactory);
        dmd.initialize();
        return dmd;
    }

    protected DaoMetaDataImpl createDaoMetaData(final Class daoClass) {
        final DaoMetaDataImpl dmd = new DaoMetaDataImpl();
        final BeanDesc daoBeanDesc = BeanDescFactory.getBeanDesc(daoClass);
        final DaoAnnotationReader daoAnnotationReader = getAnnotationReaderFactory()
                .createDaoAnnotationReader(daoBeanDesc);
        final BeanMetaDataFactory bmdf = getBeanMetaDataFactory();
        final DtoMetaDataFactory dmdf = getDtoMetaDataFactory();

        dmd.setDaoClass(daoClass);
        dmd.setDataSource(getDataSource());
        dmd.setStatementFactory(BasicStatementFactory.INSTANCE);
        dmd.setResultSetFactory(BasicResultSetFactory.INSTANCE);
        dmd.setValueTypeFactory(getValueTypeFactory());
        dmd.setBeanMetaDataFactory(bmdf);
        dmd.setDaoNamingConvention(getDaoNamingConvention());
        dmd.setDaoAnnotationReader(daoAnnotationReader);
        dmd.setProcedureMetaDataFactory(getProcedureMetaDataFactory());
        dmd.setDtoMetaDataFactory(dmdf);
        dmd.setResultSetHandlerFactory(getResultSetHandlerFactory());
        dmd.initialize();
        return dmd;
    }

    protected BeanMetaDataFactory getBeanMetaDataFactory() {
        if (beanMetaDataFactory == null) {
            final BeanMetaDataFactoryImpl impl = new BeanMetaDataFactoryImpl() {
                protected Dbms getDbms() {
                    return S2DaoTestCase.this.getDbms();
                }
            };
            impl.setAnnotationReaderFactory(getAnnotationReaderFactory());
            impl.setDataSource(getDataSource());
            impl.setDaoNamingConvention(getDaoNamingConvention());
            impl.setBeanEnhancer(getBeanEnhancer());
            impl.setPropertyTypeFactoryBuilder(getPropertyTypeFactoryBuilder());
            impl
                    .setRelationPropertyTypeFactoryBuilder(getRelationPropertyTypeFactoryBuilder(impl));
            impl.setTableNaming(getTableNaming());
            beanMetaDataFactory = impl;
        }
        return beanMetaDataFactory;
    }

    protected Dbms getDbms() {
        if (dbms == null) {
            final DatabaseMetaData dbMetaData = getDatabaseMetaData();
            dbms = DbmsManager.getDbms(dbMetaData);
        }
        return dbms;
    }

    protected void setDbms(final Dbms dbms) {
        this.dbms = dbms;
    }

    protected AnnotationReaderFactory getAnnotationReaderFactory() {
        if (annotationReaderFactory == null) {
            annotationReaderFactory = new FieldAnnotationReaderFactory();
        }
        return annotationReaderFactory;
    }

    protected void setAnnotationReaderFactory(
            final AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    protected ValueTypeFactory getValueTypeFactory() {
        if (valueTypeFactory == null) {
            final ValueTypeFactoryImpl impl = new ValueTypeFactoryImpl();
            impl.setContainer(getContainer());
            valueTypeFactory = impl;
        }
        return valueTypeFactory;
    }

    protected void setValueTypeFactory(final ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    protected DaoNamingConvention getDaoNamingConvention() {
        if (daoNamingConvention == null) {
            daoNamingConvention = new DaoNamingConventionImpl();
        }
        return daoNamingConvention;
    }

    protected void setDaoNamingConvention(
            final DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
    }

    protected BeanEnhancer getBeanEnhancer() {
        if (beanEnhancer == null) {
            final BeanEnhancerImpl impl = new BeanEnhancerImpl();
            impl.setDaoNamingConvention(getDaoNamingConvention());
            beanEnhancer = impl;
        }
        return beanEnhancer;
    }

    protected void setBeanEnhancer(final BeanEnhancer beanEnhancer) {
        this.beanEnhancer = beanEnhancer;
    }

    protected ResultSetHandlerFactory getResultSetHandlerFactory() {
        if (resultSetHandlerFactory == null) {
            final ResultSetHandlerFactorySelector factory = new ResultSetHandlerFactorySelector();
            factory.setDtoMetaDataFactory(getDtoMetaDataFactory());
            factory.init();
            resultSetHandlerFactory = factory;
        }
        return resultSetHandlerFactory;
    }

    protected void setResultSetHandlerFactory(
            final ResultSetHandlerFactory resultSetHandlerFactory) {
        this.resultSetHandlerFactory = resultSetHandlerFactory;
    }

    protected DtoMetaDataFactory getDtoMetaDataFactory() {
        if (dtoMetaDataFactory == null) {
            final DtoMetaDataFactoryImpl factory = new DtoMetaDataFactoryImpl();
            factory.setAnnotationReaderFactory(getAnnotationReaderFactory());
            factory
                    .setPropertyTypeFactoryBuilder(getPropertyTypeFactoryBuilder());
            dtoMetaDataFactory = factory;
        }
        return dtoMetaDataFactory;
    }

    protected void setDtoMetaDataFactory(
            final DtoMetaDataFactory dtoMetaDataFactory) {
        this.dtoMetaDataFactory = dtoMetaDataFactory;
    }

    protected ColumnNaming getColumnNaming() {
        if (columnNaming == null) {
            columnNaming = new DefaultColumnNaming();
        }
        return columnNaming;
    }

    protected void setColumnNaming(final ColumnNaming columnNaming) {
        this.columnNaming = columnNaming;
    }

    protected PropertyTypeFactoryBuilder getPropertyTypeFactoryBuilder() {
        if (propertyTypeFactoryBuilder == null) {
            final PropertyTypeFactoryBuilderImpl builder = new PropertyTypeFactoryBuilderImpl();
            builder.setColumnNaming(getColumnNaming());
            builder.setDaoNamingConvention(getDaoNamingConvention());
            builder.setValueTypeFactory(getValueTypeFactory());
            propertyTypeFactoryBuilder = builder;
        }
        return propertyTypeFactoryBuilder;
    }

    protected void setPropertyTypeFactoryBuilder(
            final PropertyTypeFactoryBuilder propertyTypeFactoryBuilder) {
        this.propertyTypeFactoryBuilder = propertyTypeFactoryBuilder;
    }

    protected RelationPropertyTypeFactoryBuilder getRelationPropertyTypeFactoryBuilder(
            final BeanMetaDataFactory beanMetaDataFactory) {
        if (relationPropertyTypeFactoryBuilder == null) {
            final RelationPropertyTypeFactoryBuilderImpl builder = new RelationPropertyTypeFactoryBuilderImpl();
            builder.setBeanEnhancer(beanEnhancer);
            builder.setBeanMetaDataFactory(beanMetaDataFactory);
            relationPropertyTypeFactoryBuilder = builder;
        }
        return relationPropertyTypeFactoryBuilder;
    }

    protected void setRelationPropertyTypeFactoryBuilder(
            final RelationPropertyTypeFactoryBuilder relationPropertyTypeFactoryBuilder) {
        this.relationPropertyTypeFactoryBuilder = relationPropertyTypeFactoryBuilder;
    }

    protected TableNaming getTableNaming() {
        if (tableNaming == null) {
            tableNaming = new DefaultTableNaming();
        }
        return tableNaming;
    }

    protected void setTableNaming(final TableNaming tableNaming) {
        this.tableNaming = tableNaming;
    }

    protected ProcedureMetaDataFactory getProcedureMetaDataFactory() {
        if (procedureMetaDataFactory == null) {
            final ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
            factory.setValueTypeFactory(valueTypeFactory);
            factory.setAnnotationReaderFactory(annotationReaderFactory);
            factory.initialize();
            procedureMetaDataFactory = factory;
        }
        return procedureMetaDataFactory;
    }

    protected void setProcedureMetaDataFactory(
            final ProcedureMetaDataFactory procedureMetaDataFactory) {
        this.procedureMetaDataFactory = procedureMetaDataFactory;
    }

}
