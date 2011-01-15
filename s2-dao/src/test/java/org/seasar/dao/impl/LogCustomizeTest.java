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

import org.apache.log4j.PropertyConfigurator;
import org.seasar.extension.jdbc.SqlLogRegistryLocator;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.ResourceUtil;

/**
 * @author azusa
 *
 */
public class LogCustomizeTest extends S2TestCase {

    private EmployeeDao dao;

    public void setUp() throws Exception {
        PropertyConfigurator.configure(ResourceUtil
                .getResource("logcustomize.properties"));
        include("org/seasar/dao/impl/LogCustomizeTest.dicon");
    }

    /*
     * EmployeeDaoのログ出力レベルはINFOなので、ここではログがでない
     */
    public void testLogTx() throws Exception {
        dao.findAll();
        String sql = SqlLogRegistryLocator.getInstance().getLast()
                .getCompleteSql();
        assertNotNull(sql);
        System.out.println(sql);
    }

    public void tearDown() throws Exception {
        PropertyConfigurator.configure(ResourceUtil
                .getResource("log4j.properties"));
    }
}
