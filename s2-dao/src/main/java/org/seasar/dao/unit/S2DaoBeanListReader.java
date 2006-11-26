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
import org.seasar.dao.impl.FieldAnnotationReaderFactory;
import org.seasar.dao.impl.ValueTypeFactoryImpl;

/**
 * @author higa
 * 
 */
public class S2DaoBeanListReader extends S2DaoBeanReader {

    /**
     * @deprecated
     */
    public S2DaoBeanListReader(List list, DatabaseMetaData dbMetaData) {
        Dbms dbms = DbmsManager.getDbms(dbMetaData);
        BeanMetaDataImpl beanMetaData = new BeanMetaDataImpl();
        beanMetaData.setBeanClass(list.get(0).getClass());
        beanMetaData.setDatabaseMetaData(dbMetaData);
        beanMetaData.setDbms(dbms);

        final FieldAnnotationReaderFactory fieldAnnotationReaderFactory = new FieldAnnotationReaderFactory();
        final ValueTypeFactoryImpl valueTypeFactoryImpl = new ValueTypeFactoryImpl();
        beanMetaData.setAnnotationReaderFactory(fieldAnnotationReaderFactory);
        beanMetaData.setValueTypeFactory(valueTypeFactoryImpl);
        beanMetaData.setBeanMetaDataFactory(createBeanMetaDataFactory(
                fieldAnnotationReaderFactory, valueTypeFactoryImpl));
        beanMetaData.setRelationNestLevel(0);
        beanMetaData.initialize();
        initialize(list, beanMetaData);
    }

    private BeanMetaDataFactory createBeanMetaDataFactory(
            AnnotationReaderFactory annotationReaderFactory,
            ValueTypeFactory valueTypeFactory) {
        final BeanMetaDataFactoryImpl beanMetaDataFactoryImpl = new BeanMetaDataFactoryImpl();
        beanMetaDataFactoryImpl
                .setAnnotationReaderFactory(annotationReaderFactory);
        beanMetaDataFactoryImpl.setValueTypeFactory(valueTypeFactory);
        return beanMetaDataFactoryImpl;
    }

    public S2DaoBeanListReader(List list, BeanMetaData beanMetaData) {
        initialize(list, beanMetaData);
    }

    private void initialize(List list, BeanMetaData beanMetaData) {
        setupColumns(beanMetaData);
        for (int i = 0; i < list.size(); ++i) {
            setupRow(beanMetaData, list.get(i));
        }
    }

}