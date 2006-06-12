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
package org.seasar.dao.interceptors;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class S2DaoInterceptor3Test extends S2TestCase {

    private DepartmentAutoDao dao;

    public S2DaoInterceptor3Test(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(S2DaoInterceptor3Test.class);
    }

    public void setUp() {
        include("DepartmentAutoDao.dicon");
    }

    public void testUpdateTx() throws Exception {
        Department dept = new Department();
        dept.setDeptno(10);
        assertEquals("1", 1, dao.update(dept));
    }

    public void testDeleteTx() throws Exception {
        Department dept = new Department();
        dept.setDeptno(10);
        assertEquals("1", 1, dao.delete(dept));
    }

}