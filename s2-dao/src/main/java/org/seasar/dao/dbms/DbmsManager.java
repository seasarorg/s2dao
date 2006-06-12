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
package org.seasar.dao.dbms;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ResourceUtil;

/**
 * @author higa
 * 
 */
public final class DbmsManager {

    private static Map dbmses = new HashMap();

    static {
        Properties dbmsClassNames = ResourceUtil
                .getProperties("dbms.properties");
        for (Iterator i = dbmsClassNames.keySet().iterator(); i.hasNext();) {
            String productName = (String) i.next();
            Dbms dbms = (Dbms) ClassUtil.newInstance(dbmsClassNames
                    .getProperty(productName));
            dbmses.put(productName, dbms);
        }
    }

    private DbmsManager() {
    }

    public static Dbms getDbms(DataSource dataSource) {
        Dbms dbms = null;
        Connection con = DataSourceUtil.getConnection(dataSource);
        try {
            DatabaseMetaData dmd = ConnectionUtil.getMetaData(con);
            dbms = getDbms(dmd);
        } finally {
            ConnectionUtil.close(con);
        }
        return dbms;
    }

    public static Dbms getDbms(DatabaseMetaData dmd) {
        return getDbms(DatabaseMetaDataUtil.getDatabaseProductName(dmd));
    }

    public static Dbms getDbms(String productName) {
        Dbms dbms = (Dbms) dbmses.get(productName);
        if (dbms == null) {
            dbms = (Dbms) dbmses.get("");
        }
        return dbms;
    }
}