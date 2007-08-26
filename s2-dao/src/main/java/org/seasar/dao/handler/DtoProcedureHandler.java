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
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.exception.SQLRuntimeException;

/**
 * @author taedium
 *
 */
public class DtoProcedureHandler extends AbstractProcedureHandler {

    public DtoProcedureHandler(final DataSource dataSource, final String sql,
            final ResultSetHandler resultSetHandler,
            final StatementFactory statementFactory,
            final ResultSetFactory resultSetFactory,
            final ProcedureMetaData procedureMetaData) {

        super(dataSource, sql, resultSetHandler, statementFactory,
                resultSetFactory, procedureMetaData);
    }

    public Object execute(final Object[] args) throws SQLRuntimeException {
        if (args.length != 1) {
            throw new IllegalArgumentException("args"); // TODO
        }
        return super.execute(args);
    }

    protected String getCompleteSql(final Object[] args) {
        return getSql();
    }

    protected void bindArgs(final CallableStatement cs, final Object[] args)
            throws SQLException {
        final ProcedureMetaData procedureMetaData = getProcedureMetaData();
        final int size = procedureMetaData.getParameterTypeSize();
        for (int i = 0; i < size; i++) {
            final ProcedureParameterType ppt = procedureMetaData.getParameterType(i);
            final PropertyDesc pd = ppt.getPropertyDesc();
            if (isReturnOrOutType(ppt)) {
                registerOutParameter(cs, ppt);
            }
            if (ppt.isInType()) {
                final Object value = pd.getValue(args[0]);
                bindValue(cs, ppt, value);
            }
        }
    }

    protected Object handleNoResultSet(final CallableStatement cs,
            final Object[] args) throws SQLException {
        final ProcedureMetaData procedureMetaData = getProcedureMetaData();
        final int size = procedureMetaData.getParameterTypeSize();
        for (int i = 0; i < size; i++) {
            final ProcedureParameterType ppt = procedureMetaData.getParameterType(i);
            final PropertyDesc pd = ppt.getPropertyDesc();
            if (isReturnOrOutType(ppt)) {
                final Object value = getValue(cs, ppt);
                pd.setValue(args[0], value);
            }
        }
        return args[0];
    }

}
