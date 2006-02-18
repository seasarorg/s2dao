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

import org.seasar.dao.Dbms;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public abstract class S2DaoTestCase extends S2TestCase {

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
            getDatabaseMetaData());
        assertEquals(message, expected, reader.read());
    }

    protected void assertBeanListEquals(String message, DataSet expected,
        List list) {

        S2DaoBeanListReader reader = new S2DaoBeanListReader(list,
            getDatabaseMetaData());
        assertEquals(message, expected, reader.read());
    }

    protected Dbms getDbms() {
        DatabaseMetaData dbMetaData = getDatabaseMetaData();
        return DbmsManager.getDbms(dbMetaData);
    }

}