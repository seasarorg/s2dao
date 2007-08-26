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
package org.seasar.dao.handler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;
import org.seasar.framework.exception.EmptyRuntimeException;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * @author taedium
 *
 */
public abstract class AbstractProcedureHandler extends BasicSelectHandler
        implements ProcedureHandler {

    private ProcedureMetaData procedureMetaData;

    public ProcedureMetaData getProcedureMetaData() {
        return procedureMetaData;
    }

    public void setProcedureMetaData(final ProcedureMetaData procedureMetaData) {
        this.procedureMetaData = procedureMetaData;
    }

    public AbstractProcedureHandler(final DataSource dataSource, final String sql,
            final ResultSetHandler resultSetHandler,
            final StatementFactory statementFactory,
            final ResultSetFactory resultSetFactory,
            final ProcedureMetaData procedureMetaData) {

        super(dataSource, sql, resultSetHandler, statementFactory,
                resultSetFactory);
        setProcedureMetaData(procedureMetaData);
    }

    public Object execute(final Connection connection, final Object[] args,
            final Class[] argTypes) {
        logSql(args, argTypes);
        CallableStatement cs = null;
        try {
            cs = prepareCallableStatement(connection);
            bindArgs(cs, args);
            if (cs.execute()) {
                return handleResultSet(cs);
            } else {
                return handleNoResultSet(cs, args);
            }
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            StatementUtil.close(cs);
        }
    }

    protected CallableStatement prepareCallableStatement(final Connection connection) {
        if (getSql() == null) {
            throw new EmptyRuntimeException("sql");
        }
        final CallableStatement cs = getStatementFactory().createCallableStatement(
                connection, getSql());
        if (getFetchSize() > -1) {
            StatementUtil.setFetchSize(cs, getFetchSize());
        }
        if (getMaxRows() > -1) {
            StatementUtil.setMaxRows(cs, getMaxRows());
        }
        return cs;
    }

    protected Object handleResultSet(final CallableStatement cs)
            throws SQLException {
        ResultSet rs = null;
        try {
            rs = getResultSetFactory().getResultSet(cs);
            return getResultSetHandler().handle(rs);
        } finally {
            ResultSetUtil.close(rs);
        }
    }

    protected boolean isReturnOrOutType(final ProcedureParameterType ppt) {
        return ppt.isReturnType() || ppt.isOutType();
    }

    protected Object getValue(final CallableStatement cs, final ProcedureParameterType ppt)
            throws SQLException {
        final ValueType valueType = ppt.getValueType();
        if (ppt.hasIndex()) {
            final int index = ppt.getIndex().intValue();
            return valueType.getValue(cs, index);
        }
        final String name = ppt.getParameterName();
        return valueType.getValue(cs, name);
    }

    protected void bindValue(final CallableStatement cs, final ProcedureParameterType ppt,
            final Object value) throws SQLException {
        final ValueType valueType = ppt.getValueType();
        if (ppt.hasIndex()) {
            final int index = ppt.getIndex().intValue();
            valueType.bindValue(cs, index, value);
        } else {
            final String name = ppt.getParameterName();
            valueType.bindValue(cs, name, value);
        }
    }

    protected void registerOutParameter(final CallableStatement cs,
            final ProcedureParameterType ppt) throws SQLException {
        final ValueType valueType = ppt.getValueType();
        if (ppt.hasIndex()) {
            final int index = ppt.getIndex().intValue();
            valueType.registerOutParameter(cs, index);
        } else {
            final String name = ppt.getParameterName();
            valueType.registerOutParameter(cs, name);
        }
    }

    protected String getCompleteSql(final Object[] args) {
        return getSql();
    }

    protected abstract void bindArgs(CallableStatement cs, Object[] args)
            throws SQLException;

    protected abstract Object handleNoResultSet(CallableStatement cs,
            Object[] args) throws SQLException;
}