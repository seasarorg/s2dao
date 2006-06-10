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
package org.seasar.dao.handler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.exception.SRuntimeException;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * @author Satoshi Kimura
 * @author manhole
 */
public class ReturnsResultsetProcedureHandler extends
        AbstractBasicProcedureHandler {

    protected ResultSetHandler resultSetHandler_;

    public ResultSetHandler getResultSetHandler() {
        return resultSetHandler_;
    }

    public void setResultSetHandler(ResultSetHandler handler) {
        this.resultSetHandler_ = handler;
    }

    public ReturnsResultsetProcedureHandler(DataSource ds,
            String procedureName, ResultSetHandler resultSetHandler) {
        this(ds, procedureName, BasicStatementFactory.INSTANCE,
                resultSetHandler);
    }

    public ReturnsResultsetProcedureHandler(DataSource ds,
            String procedureName, StatementFactory statementFactory,
            ResultSetHandler resultSetHandler) {
        setDataSource(ds);
        setProcedureName(procedureName);
        setStatementFactory(statementFactory);
        setResultSetHandler(resultSetHandler);
    }

    public void initialize() {
        if (initTypes() > 1) {
            throw new SRuntimeException("EDAO0010");
        }
    }

    protected Object execute(Connection connection, Object[] args) {
        CallableStatement cs = null;
        ResultSet rs = null;
        try {
            cs = prepareCallableStatement(connection);
            bindArgs(cs, args);
            cs.execute();
            rs = cs.getResultSet();
            return getResultSetHandler().handle(rs);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            try {
                ResultSetUtil.close(rs);
            } finally {
                StatementUtil.close(cs);
            }
        }
    }

}
