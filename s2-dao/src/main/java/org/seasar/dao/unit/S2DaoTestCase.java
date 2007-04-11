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
package org.seasar.dao.unit;

import java.sql.DatabaseMetaData;
import java.util.List;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanEnhancer;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.DaoNamingConvention;
import org.seasar.dao.Dbms;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.dao.impl.BeanEnhancerImpl;
import org.seasar.dao.impl.BeanMetaDataFactoryImpl;
import org.seasar.dao.impl.DaoMetaDataImpl;
import org.seasar.dao.impl.DaoNamingConventionImpl;
import org.seasar.dao.impl.FieldAnnotationReaderFactory;
import org.seasar.dao.impl.ValueTypeFactoryImpl;
import org.seasar.dao.pager.PagerContext;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.jdbc.impl.BasicResultSetFactory;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.unit.S2TestCase;

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

    protected DaoMetaDataImpl createDaoMetaData(final Class daoClass) {
        final DaoMetaDataImpl dmd = new DaoMetaDataImpl();
        dmd.setDaoClass(daoClass);
        dmd.setDataSource(getDataSource());
        dmd.setStatementFactory(BasicStatementFactory.INSTANCE);
        dmd.setResultSetFactory(BasicResultSetFactory.INSTANCE);
        dmd.setAnnotationReaderFactory(getAnnotationReaderFactory());
        dmd.setValueTypeFactory(getValueTypeFactory());
        dmd.setBeanMetaDataFactory(getBeanMetaDataFactory());
        dmd.setDaoNamingConvention(getDaoNamingConvention());
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
            impl.setValueTypeFactory(getValueTypeFactory());
            impl.setDataSource(getDataSource());
            impl.setDaoNamingConvention(getDaoNamingConvention());
            impl.setBeanEnhancer(getBeanEnhancer());
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

}
