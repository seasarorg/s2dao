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
package org.seasar.dao.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.seasar.dao.util.SelectableDataSourceProxyUtil;
import org.seasar.extension.datasource.impl.DataSourceFactoryImpl;
import org.seasar.extension.datasource.impl.SelectableDataSourceProxy;

/**
 * @author taedium
 * 
 */
public class SelectableDataSourceProxyUtilTest extends TestCase {

    public void testDataSource() throws Exception {
        DataSource dataSource = new MyDataSource();
        assertNull(SelectableDataSourceProxyUtil
                .getSelectableDataSourceName(dataSource));
    }

    public void testSelectableDataSourceProxy() throws Exception {
        DataSourceFactoryImpl dataSourceFactory = new DataSourceFactoryImpl();
        SelectableDataSourceProxy proxy = new SelectableDataSourceProxy();
        proxy.setDataSourceFactory(dataSourceFactory);

        dataSourceFactory.setSelectableDataSourceName("hoge");
        assertEquals("hoge", SelectableDataSourceProxyUtil
                .getSelectableDataSourceName(proxy));

        dataSourceFactory.setSelectableDataSourceName("foo");
        assertEquals("foo", SelectableDataSourceProxyUtil
                .getSelectableDataSourceName(proxy));
    }

    public static class MyDataSource implements DataSource {

        public Connection getConnection() throws SQLException {
            return null;
        }

        public Connection getConnection(String username, String password)
                throws SQLException {
            return null;
        }

        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        public void setLoginTimeout(int seconds) throws SQLException {
        }

        public void setLogWriter(PrintWriter out) throws SQLException {
        }
    }
}
