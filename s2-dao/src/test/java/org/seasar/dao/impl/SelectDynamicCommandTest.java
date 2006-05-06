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

import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.impl.BasicResultSetFactory;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;

public class SelectDynamicCommandTest extends S2DaoTestCase {

    public SelectDynamicCommandTest(String arg0) {
        super(arg0);
    }

    public void testExecute() throws Exception {
        SelectDynamicCommand cmd = new SelectDynamicCommand(getDataSource(),
                BasicStatementFactory.INSTANCE,
                new BeanMetaDataResultSetHandler(
                        createBeanMetaData(Employee.class)),
                BasicResultSetFactory.INSTANCE);
        cmd.setSql("SELECT * FROM emp WHERE empno = /*empno*/1234");
        Employee emp = (Employee) cmd
                .execute(new Object[] { new Integer(7788) });
        System.out.println(emp);
        assertNotNull("1", emp);
    }

    public void setUp() {
        include("j2ee.dicon");
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SelectDynamicCommandTest.class);
    }

}