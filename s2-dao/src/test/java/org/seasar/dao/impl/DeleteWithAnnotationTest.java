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

import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author azusa
 *
 */
public class DeleteWithAnnotationTest extends S2TestCase {

    private Employee12Dao dao;

    protected void setUp() throws Exception {
        include("DeleteWithAnnotationTest.dicon");
    }

    public void testDeleteTx() throws Exception {
        DataTable before = readDbByTable("EMP");
        dao.delete(7369);
        assertEquals(before.getRowSize() - 1, readDbByTable("EMP").getRowSize());
        dao.deleteNoWhere(7499);
        assertEquals(before.getRowSize() - 2, readDbByTable("EMP").getRowSize());
    }
}
