/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.MethodSetupFailureRuntimeException;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.framework.exception.SRuntimeException;

/**
 * @author taichi
 * @author azusa
 */
public class PkOnlyTableTest extends S2DaoTestCase {

    public PkOnlyTableTest() {
        super();
    }

    public PkOnlyTableTest(String name) {
        super(name);
    }

    public void setUp() {
        include("j2ee.dicon");
    }

    /*
     * https://www.seasar.org/issues/browse/DAO-16
     */
    public void testInsertTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(PkOnlyTableDao.class);
        SqlCommand cmd = dmd.getSqlCommand("insert");
        PkOnlyTable data = new PkOnlyTable();
        data.setAaa("value");
        Integer i = (Integer) cmd.execute(new Object[] { data });
        assertEquals(1, i.intValue());
    }

    public void setUpUpdateUnlessNullTx() {
        include("PkOnlyTableTest.dicon");
    }

    public void testUpdateUnlessNullTx() throws Exception {
        PkOnlyTableDao2 dao = (PkOnlyTableDao2) getComponent(PkOnlyTableDao2.class);
        try {
            dao.updateUnlessNull(new PkOnlyTable());
            fail();
        } catch (MethodSetupFailureRuntimeException e) {
            assertEquals("EDAO0020", ((SRuntimeException) e.getCause())
                    .getMessageCode());
            e.printStackTrace();
        }
    }

    public void setUpUpdateTx() {
        include("PkOnlyTableTest.dicon");
    }

    /*
     * https://www.seasar.org/issues/browse/DAO-52
     */
    public void testUpdateTx() throws Exception {
        PkOnlyTableDao2 dao = (PkOnlyTableDao2) getComponent(PkOnlyTableDao2.class);
        try {
            dao.update(new PkOnlyTable());
            fail();
        } catch (MethodSetupFailureRuntimeException e) {
            assertEquals("EDAO0020", ((SRuntimeException) e.getCause())
                    .getMessageCode());
            e.printStackTrace();
        }

    }

    public class PkOnlyTable {
        public static final String TABLE = "PKONLYTABLE";

        private String aaa;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }
    }

    public interface PkOnlyTableDao {
        Class BEAN = PkOnlyTable.class;

        int insert(PkOnlyTable data);

    }

    public interface PkOnlyTableDao2 {
        Class BEAN = PkOnlyTable.class;

        int update(PkOnlyTable data);

        int updateUnlessNull(PkOnlyTable data);
    }
}
