/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
import org.seasar.dao.SqlCommand;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author higa
 * 
 */
public class BooleanPropertyTest extends S2DaoTestCase {

    public void setUp() {
        include("j2ee.dicon");
    }

    public void testInsertAndSelectTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Department2AutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand("insert");
        Department2 dept = new Department2();
        dept.setDeptno(99);
        dept.setDname("hoge");
        dept.setActive(true);
        cmd.execute(new Object[] { dept });
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand("getDepartment");
        Department2 dept2 = (Department2) cmd2
                .execute(new Object[] { new Integer(99) });
        assertEquals("1", true, dept2.isActive());
    }

}
