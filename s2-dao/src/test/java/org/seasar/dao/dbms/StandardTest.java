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
package org.seasar.dao.dbms;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.framework.exception.SRuntimeException;
import org.seasar.framework.util.DisposableUtil;

/**
 * @author higa
 * @author manhole
 */
public class StandardTest extends S2DaoTestCase {

    protected void setUp() throws Exception {
        include("j2ee.dicon");
        Dbms dbms = new Standard();
        setDbms(dbms);
    }

    public void testCreateAutoSelectList() throws Exception {
        BeanMetaData bmd = createBeanMetaData(Employee.class);
        String sql = getDbms().getAutoSelectSql(bmd);
        System.out.println(sql);
    }

    public void testDispose() throws Exception {
        final Standard standard = new Standard();
        setDbms(standard);
        assertEquals(0, standard.autoSelectFromClauseCache.size());

        final BeanMetaData bmd = createBeanMetaData(Employee.class);
        {
            final String sql = standard.getAutoSelectSql(bmd);
            assertNotNull(sql);
        }

        assertEquals(1, standard.autoSelectFromClauseCache.size());
        DisposableUtil.dispose();
        assertEquals(0, standard.autoSelectFromClauseCache.size());
        {
            final String sql = standard.getAutoSelectSql(bmd);
            assertNotNull(sql);
        }
        assertEquals(1, standard.autoSelectFromClauseCache.size());
        DisposableUtil.dispose();
        assertEquals(0, standard.autoSelectFromClauseCache.size());
    }

    public void testGetIdentitySelectString() throws Exception {
        try {
            getDbms().getIdentitySelectString();
            fail();
        } catch (SRuntimeException e) {
            assertEquals("EDAO0022", e.getMessageCode());
            System.out.println(e);
        }
    }

    public void testGetSequenceNextValString() throws Exception {
        try {
            getDbms().getSequenceNextValString(null);
            fail();
        } catch (SRuntimeException e) {
            assertEquals("EDAO0022", e.getMessageCode());
            System.out.println(e);
        }
    }
}