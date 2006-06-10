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
package org.seasar.dao.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.exception.SRuntimeException;
import org.seasar.framework.util.ResultSetUtil;

/**
 * @author manhole
 */
public class ProcedureUtil {

    public static ProcedureMetaData getProcedureMetaData(DataSource dataSource,
            String procedureName) {
        final Connection con = DataSourceUtil.getConnection(dataSource);
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = ConnectionUtil.getMetaData(con);
            String[] names = DatabaseMetaDataUtil.convertIdentifier(dmd,
                    procedureName).split("\\.");
            final int namesLength = names.length;
            if (namesLength == 1) {
                rs = dmd.getProcedures(null, null, names[0]);
            } else if (namesLength == 2) {
                rs = dmd.getProcedures(null, names[0], names[1]);
            } else if (namesLength == 3) {
                rs = dmd.getProcedures(names[0], names[1], names[2]);
            }
            int len = 0;
            names = new String[3];
            ProcedureMetaData procedureMetaData = new ProcedureMetaData();
            while (rs.next()) {
                procedureMetaData.setProcedureCat(rs.getString(1));
                procedureMetaData.setProcedureSchem(rs.getString(2));
                procedureMetaData.setProcedureName(rs.getString(3));
                procedureMetaData.setProcedureType(rs.getShort(8));
                len++;
            }
            if (len < 1) {
                throw new SRuntimeException("EDAO0012",
                        new Object[] { procedureName });
            }
            if (len > 1) {
                throw new SRuntimeException("EDAO0013",
                        new Object[] { procedureName });
            }
            return procedureMetaData;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            try {
                ResultSetUtil.close(rs);
            } finally {
                ConnectionUtil.close(con);
            }
        }
    }

}
