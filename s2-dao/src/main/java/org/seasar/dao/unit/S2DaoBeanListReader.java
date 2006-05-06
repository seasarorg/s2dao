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

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.dbms.DbmsManager;
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
        beanMetaData
                .setAnnotationReaderFactory(new FieldAnnotationReaderFactory());
        beanMetaData.setValueTypeFactory(new ValueTypeFactoryImpl());
        beanMetaData.initialize();
        initialize(list, beanMetaData);
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