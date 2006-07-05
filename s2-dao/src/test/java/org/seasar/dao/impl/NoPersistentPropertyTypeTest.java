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

import java.util.Iterator;
import java.util.List;

import org.seasar.dao.NoPersistentPropertyTypeRuntimeException;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * https://www.seasar.org/issues/browse/DAO-20
 * 
 * @author manhole
 */
public class NoPersistentPropertyTypeTest extends S2DaoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee.dicon");
    }

    public void testNoPersistentPropertyTypeException1() throws Exception {
        try {
            final DaoMetaDataImpl dmd = createDaoMetaData(Foo1Dao.class);
            final SqlCommand command = dmd.getSqlCommand("findAll");
            command.execute(null);
            fail();
        } catch (NoPersistentPropertyTypeRuntimeException e) {
            e.printStackTrace();
            final String message = e.getMessage();
            assertEquals(true, -1 < message.indexOf("EDAO0017"));
        }
    }

    public void testNoPersistentPropertyTypeException2() throws Exception {
        // ## Arrange ##
        final DaoMetaDataImpl dmd = createDaoMetaData(Foo2Dao.class);

        // ## Act ##
        final SqlCommand command = dmd.getSqlCommand("findAll");

        // ## Assert ##
        final List result = (List) command.execute(null);
        assertEquals(false, result.isEmpty());
        for (Iterator it = result.iterator(); it.hasNext();) {
            FooDto a = (FooDto) it.next();
            assertNotNull(a.getEname());
        }
    }

    public static interface Foo1Dao {
        Class BEAN = FooDto.class;

        List findAll();
    }

    public static interface Foo2Dao {
        Class BEAN = FooDto.class;

        String findAll_SQL = "SELECT * FROM EMP";

        List findAll();
    }

    public static class FooDto {
        public static final String TABLE = "WRONG_TABLE_NAME";

        private String ename;

        public String getEname() {
            return ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }
    }

}
