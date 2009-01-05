/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import java.util.Date;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author manhole
 */
public class ProcedureHsqldbTest extends S2DaoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee.dicon");
    }

    public void test1() throws Exception {
        DaoMetaData dmd = createDaoMetaData(ADao.class);
        SqlCommand cmd = dmd.getSqlCommand("currentDate");
        Object result = cmd.execute(new Object[] {});
        System.out.println(result);
        assertNotNull(result);
        assertEquals(true, result instanceof Date);
    }

    public static interface ADao {

        public Class BEAN = ABean.class;

        public String currentDate_PROCEDURE = "CURDATE";

        public Date currentDate();

    }

    public static class ABean {
    }

}
