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

import javax.sql.DataSource;

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.handler.ProcedureHandlerImpl;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;

public class StaticStoredProcedureCommand implements SqlCommand {

    private ProcedureMetaData procedureMetaData;

    private DataSource dataSource;

    private StatementFactory statementFactory;

    private ResultSetHandler resultSetHandler;

    private Method daoMethod;

    private String sql;

    public StaticStoredProcedureCommand(DataSource dataSource,
            StatementFactory statementFactory,
            ResultSetHandler resultSetHandler,
            ProcedureMetaData procedureMetaData, Method daoMethod) {
        this.dataSource = dataSource;
        this.statementFactory = statementFactory;
        this.resultSetHandler = resultSetHandler;
        this.procedureMetaData = procedureMetaData;
        this.daoMethod = daoMethod;
        this.sql = createSql();
    }

    public Object execute(Object[] args) {
        ProcedureHandlerImpl handler = new ProcedureHandlerImpl();
        handler.setDataSource(dataSource);
        handler.setStatementFactory(statementFactory);
        handler.setProcedureMetaData(procedureMetaData);
        handler.setDaoMethod(daoMethod);
        handler.setSql(sql);
        handler.setResultSetHandler(resultSetHandler);
        return handler.execute(args);
    }

    public String createSql() {
        StringBuffer buf = new StringBuffer();
        buf.append("{ ");
        int size = procedureMetaData.getParameterTypeSize();
        if (procedureMetaData.hasReturnParameterType()) {
            buf.append("? = ");
            size--;
        }
        buf.append("call ");
        buf.append(procedureMetaData.getProcedureName());
        buf.append(" (");
        for (int i = 0; i < size; i++) {
            buf.append("?, ");
        }
        if (size > 0) {
            buf.setLength(buf.length() - 2);
        }
        buf.append(")}");
        return buf.toString();
    }

}
