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
import org.seasar.dao.impl.BeanMetaDataImpl;
import org.seasar.dao.impl.DaoMetaDataImpl;
import org.seasar.dao.impl.FieldAnnotationReaderFactory;
import org.seasar.dao.impl.ValueTypeFactoryImpl;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.jdbc.impl.BasicResultSetFactory;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public abstract class S2DaoTestCase extends S2TestCase {

    private ValueTypeFactory valueTypeFactory;

    private AnnotationReaderFactory annotationReaderFactory;

    public S2DaoTestCase() {
    }

    /**
     * @param name
     */
    public S2DaoTestCase(String name) {
        super(name);
    }

    protected void assertBeanEquals(String message, DataSet expected,
            Object bean) {

        S2DaoBeanReader reader = new S2DaoBeanReader(bean,
                createBeanMetaData(bean.getClass()));
        assertEquals(message, expected, reader.read());
    }

    protected void assertBeanListEquals(String message, DataSet expected,
            List list) {

        S2DaoBeanListReader reader = new S2DaoBeanListReader(list,
                createBeanMetaData(list.get(0).getClass()));
        assertEquals(message, expected, reader.read());
    }

    protected Dbms getDbms() {
        DatabaseMetaData dbMetaData = getDatabaseMetaData();
        return DbmsManager.getDbms(dbMetaData);
    }

    protected BeanMetaData createBeanMetaData(Class beanClass) {
        BeanMetaDataImpl beanMetaData = new BeanMetaDataImpl();
        beanMetaData.setBeanClass(beanClass);
        beanMetaData.setDatabaseMetaData(getDatabaseMetaData());
        beanMetaData.setDbms(getDbms());
        beanMetaData.setAnnotationReaderFactory(getAnnotationReaderFactory());
        beanMetaData.setValueTypeFactory(getValueTypeFactory());
        beanMetaData.setBeanMetaDataFactory(createBeanMetaDataFactory());
        beanMetaData.setRelationNestLevel(0);
        beanMetaData.initialize();
        return beanMetaData;
    }

    protected ValueTypeFactory getValueTypeFactory() {
        if (valueTypeFactory == null) {
            ValueTypeFactoryImpl v = new ValueTypeFactoryImpl();
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
            AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    protected void setValueTypeFactory(ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    protected DaoMetaDataImpl createDaoMetaData(Class daoClass) {
        DaoMetaDataImpl daoMetaData = new DaoMetaDataImpl();
        daoMetaData.setDaoClass(daoClass);
        daoMetaData.setDataSource(getDataSource());
        daoMetaData.setStatementFactory(BasicStatementFactory.INSTANCE);
        daoMetaData.setResultSetFactory(BasicResultSetFactory.INSTANCE);
        daoMetaData.setAnnotationReaderFactory(getAnnotationReaderFactory());
        daoMetaData.setValueTypeFactory(getValueTypeFactory());
        daoMetaData.setBeanMetaDataFactory(createBeanMetaDataFactory());
        daoMetaData.initialize();
        return daoMetaData;
    }

    protected BeanMetaDataFactory createBeanMetaDataFactory() {
        final BeanMetaDataFactoryImpl beanMetaDataFactoryImpl = new BeanMetaDataFactoryImpl();
        beanMetaDataFactoryImpl
                .setAnnotationReaderFactory(annotationReaderFactory);
        beanMetaDataFactoryImpl.setValueTypeFactory(valueTypeFactory);
        return beanMetaDataFactoryImpl;
    }
}
