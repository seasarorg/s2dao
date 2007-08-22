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
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.seasar.framework.exception.EmptyRuntimeException;
import org.seasar.framework.exception.SQLRuntimeException;

/**
 * @author higa
 * 
 */
public abstract class AbstractBasicProcedureHandler implements ProcedureHandler {

    protected ProcedureMetaData procedureMetaData;

    protected DataSource dataSource;

    protected String sql;

    protected StatementFactory statementFactory = BasicStatementFactory.INSTANCE;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setProcedureMetaData(ProcedureMetaData procedureMetaData) {
        this.procedureMetaData = procedureMetaData;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public StatementFactory getStatementFactory() {
        return statementFactory;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    protected Connection getConnection() {
        if (dataSource == null) {
            throw new EmptyRuntimeException("dataSource");
        }
        return DataSourceUtil.getConnection(dataSource);
    }

    protected CallableStatement prepareCallableStatement(Connection connection) {
        if (sql == null) {
            throw new EmptyRuntimeException("sql");
        }
        return statementFactory.createCallableStatement(connection, sql);
    }

    public Object execute(Object[] args) throws SQLRuntimeException {
        Connection connection = getConnection();
        try {
            return execute(connection, args);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    protected abstract Object execute(Connection connection, Object[] args);

    protected void bindArgs(CallableStatement cs, Object[] args)
            throws SQLException {
        if (args == null) {
            return;
        }
        int argPos = 0;
        for (int i = 0; i < procedureMetaData.getParameterTypeSize(); i++) {
            ProcedureParameterType ppt = procedureMetaData.getParameterType(i);
            ValueType valueType = ppt.getValueType();
            if (ppt.isRegisterable()) {
                cs.registerOutParameter(i + 1, ppt.getSqlType());
            }
            if (ppt.isBindable()) {
                valueType.bindValue(cs, i + 1, args[argPos++]);
            }
        }
    }

}
