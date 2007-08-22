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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;
import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureMetaDataFactory;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.exception.SRuntimeException;
import org.seasar.framework.util.ResultSetUtil;

/**
 * @author taedium
 *
 */
public class ProcedureMetaDataFactoryImpl implements ProcedureMetaDataFactory {

    public static final String dataSource_BINDING = "bindingType=must";

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ProcedureMetaData createProcedureMetaData(
            final String procedureName, final Dbms dbms, final Method method) {

        final ProcedureMetaDataImpl result = new ProcedureMetaDataImpl(
                procedureName);
        final Connection con = DataSourceUtil.getConnection(dataSource);
        final DatabaseMetaData dmd = ConnectionUtil.getMetaData(con);
        ResultSet rs = null;
        try {
            final ProcedureInfo info = getProcedureInfo(dbms, dmd,
                    procedureName);
            rs = dmd.getProcedureColumns(info.catalog, info.schema, info.name,
                    null);
            try {
                while (rs.next()) {
                    final String columnName = rs.getString(4);
                    final int columnType = rs.getInt(5);
                    final int dataType = rs.getInt(6);
                    final ProcedureParameterTypeImpl ppt = new ProcedureParameterTypeImpl();
                    ppt.setParameterName(columnName);
                    ppt.setSqlType(dataType);
                    ppt.setValueType(ValueTypes.getValueType(dataType));
                    switch (columnType) {
                    case DatabaseMetaData.procedureColumnIn:
                        ppt.setBindable(true);
                        result.addInParameterType(ppt);
                        break;
                    case DatabaseMetaData.procedureColumnInOut:
                        ppt.setBindable(true);
                        ppt.setRegisterable(true);
                        result.addInOutParameterType(ppt);
                        break;
                    case DatabaseMetaData.procedureColumnOut:
                        ppt.setRegisterable(true);
                        result.addOutParameterType(ppt);
                        break;
                    case DatabaseMetaData.procedureColumnReturn:
                        ppt.setRegisterable(true);
                        result.setReturnParameterType(ppt);
                        break;
                    case DatabaseMetaData.procedureColumnResult:
                        continue; // ignore
                    default:
                        throw new SRuntimeException("EDAO0010",
                                new Object[] { procedureName });
                    }
                }
                return result;
            } finally {
                ResultSetUtil.close(rs);
            }
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            ConnectionUtil.close(con);
        }
    }

    protected ProcedureInfo getProcedureInfo(final Dbms dbms,
            final DatabaseMetaData databaseMetaData, final String procedureName) {
        final String name = DatabaseMetaDataUtil.convertIdentifier(
                databaseMetaData, procedureName);
        ProcedureInfo info = createProcedureInfo(dbms, databaseMetaData, name);
        if (info == null) {
            info = createProcedureInfo(dbms, databaseMetaData, procedureName);
            if (info == null) {
                throw new SRuntimeException("EDAO0012",
                        new Object[] { procedureName });
            }
        }
        return info;
    }

    protected ProcedureInfo createProcedureInfo(final Dbms dbms,
            final DatabaseMetaData databaseMetaData, final String procedureName) {
        final ResultSet rs = dbms
                .getProcedures(databaseMetaData, procedureName);
        if (rs == null) {
            return null;
        }
        try {
            final ProcedureInfo info = new ProcedureInfo();
            if (rs.next()) {
                info.catalog = rs.getString(1);
                info.schema = rs.getString(2);
                info.name = rs.getString(3);
                if (rs.next()) {
                    throw new SRuntimeException("EDAO0013",
                            new Object[] { procedureName });
                }
            }
            return info;
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            ResultSetUtil.close(rs);
        }
    }

    protected static class ProcedureInfo {
        protected String catalog;

        protected String schema;

        protected String name;
    }
}
