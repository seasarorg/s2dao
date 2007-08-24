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

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
import org.seasar.framework.exception.SRuntimeException;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * @author manhole
 * @author taedium
 */
public class ProcedureHandlerImpl extends BasicSelectHandler implements
        ProcedureHandler {

    protected ProcedureMetaData procedureMetaData;

    private Method daoMethod;

    public void setProcedureMetaData(ProcedureMetaData procedureMetaData) {
        this.procedureMetaData = procedureMetaData;
    }

    public void setDaoMethod(Method daoMethod) {
        this.daoMethod = daoMethod;
    }

    public ProcedureHandlerImpl(DataSource dataSource, String sql,
            ResultSetHandler resultSetHandler,
            StatementFactory statementFactory,
            ResultSetFactory resultSetFactory,
            ProcedureMetaData procedureMetaData, Method daoMethod) {

        super(dataSource, sql, resultSetHandler, statementFactory,
                resultSetFactory);
        setProcedureMetaData(procedureMetaData);
        setDaoMethod(daoMethod);
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
                return handleNoResultSet(cs);
            }
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            StatementUtil.close(cs);
        }
    }

    protected CallableStatement prepareCallableStatement(Connection connection) {
        if (getSql() == null) {
            throw new EmptyRuntimeException("sql");
        }
        CallableStatement cs = getStatementFactory().createCallableStatement(
                connection, getSql());
        if (getFetchSize() > -1) {
            StatementUtil.setFetchSize(cs, getFetchSize());
        }
        if (getMaxRows() > -1) {
            StatementUtil.setMaxRows(cs, getMaxRows());
        }
        return cs;
    }

    protected void bindArgs(CallableStatement cs, Object[] args)
            throws SQLException {
        if (args == null) {
            return;
        }
        int size = procedureMetaData.getParameterTypeSize();
        for (int i = 0, argIndex = 0; i < size; i++) {
            ProcedureParameterType ppt = procedureMetaData.getParameterType(i);
            ValueType valueType = ppt.getValueType();
            if (isRegisterable(ppt)) {
                valueType.registerOutParameter(cs, i + 1);
            }
            if (ppt.isInType()) {
                valueType.bindValue(cs, i + 1, args[argIndex++]);
            }
        }
    }

    protected Object handleResultSet(final CallableStatement cs)
            throws SQLException {
        ResultSet rs = null;
        try {
            rs = cs.getResultSet();
            return getResultSetHandler().handle(rs);
        } finally {
            ResultSetUtil.close(rs);
        }
    }

    protected Object handleNoResultSet(final CallableStatement cs)
            throws SQLException {
        final Class returnType = daoMethod.getReturnType();
        if (Map.class.isAssignableFrom(returnType)) {
            final Map result = new HashMap();
            for (int i = 0; i < procedureMetaData.getParameterTypeSize(); i++) {
                ProcedureParameterType ppt = procedureMetaData
                        .getParameterType(i);
                ValueType valueType = ppt.getValueType();
                if (isRegisterable(ppt)) {
                    result.put(ppt.getParameterName(), valueType.getValue(cs,
                            i + 1));
                }
            }
            return result;
        } else {
            Object result = null;
            for (int i = 0; i < procedureMetaData.getParameterTypeSize(); i++) {
                if (result != null) {
                    throw new SRuntimeException("EDAO0010");
                }
                ProcedureParameterType ppt = procedureMetaData
                        .getParameterType(i);
                ValueType valueType = ppt.getValueType();
                if (isRegisterable(ppt)) {
                    result = valueType.getValue(cs, i + 1);
                }
            }
            return result;
        }
    }

    protected boolean isRegisterable(ProcedureParameterType ppt) {
        return ppt.isReturnType() || ppt.isOutType();
    }

    protected String getCompleteSql(Object[] args) {
        String sql = getSql();
        if (args == null || args.length == 0) {
            return sql;
        }
        StringBuffer buf = new StringBuffer(100);
        int pos = 0;
        int pos2 = 0;
        int pos3 = 0;
        int size = procedureMetaData.getParameterTypeSize();
        if (procedureMetaData.hasReturnParameterType()) {
            if ((pos = sql.indexOf('?')) > 0) {
                buf.append(sql.subSequence(0, pos + 1));
                pos2 = pos + 1;
            }
            size--;
        }
        for (int i = 0, argIndex = 0; i < size; i++) {
            ProcedureParameterType ppt = procedureMetaData.getParameterType(i);
            if ((pos3 = sql.indexOf('?', pos2)) < 0) {
                break;
            }
            if (ppt.isInType()) {
                buf.append(sql.substring(pos2, pos3));
                buf.append(getBindVariableText(args[argIndex]));
                pos2 = pos3 + 1;
                argIndex++;
            } else {
                buf.append(sql.substring(pos2, pos3 + 1));
                pos2 = pos3 + 1;
            }
        }
        buf.append(sql.substring(pos2));
        return buf.toString();
    }

}
