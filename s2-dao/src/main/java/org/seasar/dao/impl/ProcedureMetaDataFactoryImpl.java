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
 * {@link ProcedureMetaDataFactory}の実装クラスです。
 * 
 * @author taedium
 */
public class ProcedureMetaDataFactoryImpl implements ProcedureMetaDataFactory {

    /** プロシージャ名 */
    protected String procedureName;

    /** データソース */
    protected DataSource dataSource;

    /** DBMS */
    protected Dbms dbms;

    /**
     * インスタンスを構築します。
     * 
     * @param procedureName プロシージャ名
     * @param dataSource データソース
     * @param dbms　DBMS
     */
    public ProcedureMetaDataFactoryImpl(final String procedureName,
            final DataSource dataSource, final Dbms dbms) {
        this.procedureName = procedureName;
        this.dataSource = dataSource;
        this.dbms = dbms;
    }

    public ProcedureMetaData createProcedureMetaData() {
        final Connection con = DataSourceUtil.getConnection(dataSource);
        final DatabaseMetaData dmd = ConnectionUtil.getMetaData(con);
        ResultSet rs = null;
        try {
            final ProcedureNamePattern pattern = getProcedureNamePattern(dbms,
                    dmd, procedureName);
            rs = dmd.getProcedureColumns(pattern.catalog, pattern.schema,
                    pattern.name, null);
            try {
                final ProcedureMetaDataImpl meta = new ProcedureMetaDataImpl(
                        procedureName);
                int index = 1;
                while (rs.next()) {
                    final String columnName = rs.getString(4);
                    final int columnType = rs.getInt(5);
                    final int dataType = rs.getInt(6);
                    final ProcedureParameterTypeImpl ppt = new ProcedureParameterTypeImpl();
                    ppt.setParameterName(columnName);
                    ppt.setValueType(ValueTypes.getValueType(dataType));
                    ppt.setIndex(new Integer(index));
                    switch (columnType) {
                    case DatabaseMetaData.procedureColumnIn:
                        ppt.setInType(true);
                        break;
                    case DatabaseMetaData.procedureColumnInOut:
                        ppt.setInType(true);
                        ppt.setOutType(true);
                        break;
                    case DatabaseMetaData.procedureColumnOut:
                        ppt.setOutType(true);
                        break;
                    case DatabaseMetaData.procedureColumnReturn:
                        ppt.setReturnType(true);
                        break;
                    case DatabaseMetaData.procedureColumnResult:
                        continue; // ignore
                    default:
                        throw new SRuntimeException("EDAO0010",
                                new Object[] { procedureName });
                    }
                    meta.addParameterType(ppt);
                    index++;
                }
                return meta;
            } finally {
                ResultSetUtil.close(rs);
            }
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            ConnectionUtil.close(con);
        }
    }

    /**
     * プロシージャの名前のパターンを返します。
     * 
     * @param dbms DBMS
     * @param databaseMetaData データベースのメタデータ
     * @param procedureName プロシージャ名
     * @return プロシージャの名前のパターン
     */
    protected ProcedureNamePattern getProcedureNamePattern(final Dbms dbms,
            final DatabaseMetaData databaseMetaData, final String procedureName) {
        final String name = DatabaseMetaDataUtil.convertIdentifier(
                databaseMetaData, procedureName);
        ProcedureNamePattern pattern = createProcedureNamePattern(dbms,
                databaseMetaData, name);
        if (pattern == null) {
            pattern = createProcedureNamePattern(dbms, databaseMetaData,
                    procedureName);
            if (pattern == null) {
                throw new SRuntimeException("EDAO0012",
                        new Object[] { procedureName });
            }
        }
        return pattern;
    }

    /**
     * プロシージャの名前のパターンを作成します。
     * 
     * @param dbms DBMS
     * @param databaseMetaData データベースのメタデータ
     * @param procedureName プロシージャ名
     * @return プロシージャの名前のパターン
     */
    protected ProcedureNamePattern createProcedureNamePattern(final Dbms dbms,
            final DatabaseMetaData databaseMetaData, final String procedureName) {
        final ResultSet rs = dbms
                .getProcedures(databaseMetaData, procedureName);
        if (rs == null) {
            return null;
        }
        try {
            if (rs.next()) {
                final ProcedureNamePattern pattern = new ProcedureNamePattern();
                pattern.catalog = rs.getString(1);
                pattern.schema = rs.getString(2);
                pattern.name = rs.getString(3);
                if (rs.next()) {
                    throw new SRuntimeException("EDAO0013",
                            new Object[] { procedureName });
                }
                return pattern;
            }
            return null;
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            ResultSetUtil.close(rs);
        }
    }

    /**
     * プロシージャの名前のパターンです。
     * 
     * @author taedium
     */
    protected static class ProcedureNamePattern {
        protected String catalog;

        protected String schema;

        protected String name;
    }
}
