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

import org.seasar.dao.ProcedureParameterType;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.exception.SRuntimeException;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * @author manhole
 */
public class ProcedureHandlerImpl extends AbstractBasicProcedureHandler {

    private Method daoMethod;

    private ResultSetHandler resultSetHandler;

    protected Object execute(final Connection connection, final Object[] args) {
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

    protected Object handleResultSet(final CallableStatement cs)
            throws SQLException {
        ResultSet rs = null;
        try {
            rs = cs.getResultSet();
            return resultSetHandler.handle(rs);
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
                if (ppt.isRegisterable()) {
                    result.put(ppt.getParameterName(), cs.getObject(i + 1));
                }
            }
            return result;
        } else {
            Object result = null;
            for (int i = 0; i < procedureMetaData.getParameterTypeSize(); i++) {
                ProcedureParameterType ppt = procedureMetaData
                        .getParameterType(i);
                if (ppt.isRegisterable()) {
                    if (result != null) {
                        throw new SRuntimeException("EDAO0010");
                    }
                    result = cs.getObject(i + 1);
                }
            }
            return result;
        }
    }

    public void setDaoMethod(Method daoMethod) {
        this.daoMethod = daoMethod;
    }

    public void setResultSetHandler(ResultSetHandler resultSetHandler) {
        this.resultSetHandler = resultSetHandler;
    }

}
