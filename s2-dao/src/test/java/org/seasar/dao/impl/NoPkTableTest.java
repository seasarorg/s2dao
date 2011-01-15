/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import org.seasar.dao.unit.S2DaoTestCase;

/**
 * [Seasar-user:4036]
 * 
 * @author manhole
 */
public class NoPkTableTest extends S2DaoTestCase {

    private NoPkTableDao noPkTableDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("NoPkTableTest.dicon");
    }

    public void testCRUDTx() throws Exception {
        {
            final NoPkTable[] beans = noPkTableDao.findAll();
            assertEquals(0, beans.length);
        }
        {
            // insert
            NoPkTable bean = new NoPkTable();
            bean.setAaa("a");
            bean.setBbb(new Integer(1));
            noPkTableDao.insert(bean);
        }
        {
            // select
            final NoPkTable[] beans = noPkTableDao.findAll();
            assertEquals(1, beans.length);
            assertEquals("a", beans[0].getAaa());
            assertEquals(1, beans[0].getBbb().intValue());

            // update
            beans[0].setAaa("a2");
            noPkTableDao.update(beans[0]);
        }
        {
            // select
            final NoPkTable[] beans = noPkTableDao.findAll();
            assertEquals(1, beans.length);
            assertEquals("a2", beans[0].getAaa());
            assertEquals(1, beans[0].getBbb().intValue());
        }
        // delete
        {
            noPkTableDao.delete("hoge");
            final NoPkTable[] beans = noPkTableDao.findAll();
            assertEquals(1, beans.length);
        }
        {
            noPkTableDao.delete("a2");
            final NoPkTable[] beans = noPkTableDao.findAll();
            assertEquals(0, beans.length);
        }
    }

    public static interface NoPkTableDao {

        Class BEAN = NoPkTable.class;

        NoPkTable[] findAll();

        // String insert_SQL = "INSERT INTO NO_PK_TABLE VALUES (/*dto.aaa*/'Z',
        // /*dto.bbb*/99)";

        int insert(NoPkTable noPkTable);

        // String update_SQL = "UPDATE NO_PK_TABLE SET AAA = /*dto.aaa*/";

        int update(NoPkTable noPkTable);

        String delete_QUERY = "AAA = ?";

        int delete(String aaa);

    }

    public static class NoPkTable {

        public static final String TABLE = "NO_PK_TABLE";

        private String aaa;

        private Integer bbb;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }

        public Integer getBbb() {
            return bbb;
        }

        public void setBbb(Integer bbb) {
            this.bbb = bbb;
        }

    }
}
