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
package org.seasar.dao.unit;

import java.sql.DatabaseMetaData;
import java.util.List;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.Dbms;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.dao.impl.BeanMetaDataFactoryImpl;
import org.seasar.dao.impl.DaoMetaDataImpl;
import org.seasar.dao.impl.FieldAnnotationReaderFactory;
import org.seasar.dao.impl.ValueTypeFactoryImpl;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.jdbc.impl.BasicResultSetFactory;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * @author manhole
 */
public abstract class S2DaoTestCase extends S2TestCase {

    private ValueTypeFactory valueTypeFactory;

    private AnnotationReaderFactory annotationReaderFactory;

    private BeanMetaDataFactory beanMetaDataFactory;

    private Dbms dbms;

    public S2DaoTestCase() {
    }

    /**
     * @param name
     */
    public S2DaoTestCase(final String name) {
        super(name);
    }

    protected void tearDown() throws Exception {
        valueTypeFactory = null;
        annotationReaderFactory = null;
        beanMetaDataFactory = null;
        dbms = null;
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

    protected Dbms getDbms() {
        if (dbms == null) {
            final DatabaseMetaData dbMetaData = getDatabaseMetaData();
            dbms = DbmsManager.getDbms(dbMetaData);
        }
        return dbms;
    }

    protected BeanMetaData createBeanMetaData(final Class beanClass) {
        final BeanMetaDataFactory factory = getBeanMetaDataFactory();
        return factory.createBeanMetaData(beanClass);
    }

    protected ValueTypeFactory getValueTypeFactory() {
        if (valueTypeFactory == null) {
            final ValueTypeFactoryImpl v = new ValueTypeFactoryImpl();
            v.setContainer(getContainer());
            valueTypeFactory = v;
        }
        return valueTypeFactory;
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

    protected void setValueTypeFactory(final ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
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
        dmd.initialize();
        return dmd;
    }

    protected BeanMetaDataFactory getBeanMetaDataFactory() {
        if (beanMetaDataFactory == null) {
            final BeanMetaDataFactoryImpl factory = new BeanMetaDataFactoryImpl() {
                protected Dbms getDbms() {
                    return S2DaoTestCase.this.getDbms();
                }
            };
            factory.setAnnotationReaderFactory(getAnnotationReaderFactory());
            factory.setValueTypeFactory(getValueTypeFactory());
            factory.setDataSource(getDataSource());
            beanMetaDataFactory = factory;
        }
        return beanMetaDataFactory;
    }

    protected void setDbms(Dbms dbms) {
        this.dbms = dbms;
    }

}
