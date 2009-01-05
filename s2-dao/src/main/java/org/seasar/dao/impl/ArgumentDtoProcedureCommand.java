/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.seasar.dao.InjectDaoClassSupport;
import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.handler.ArgumentDtoProcedureHandler;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * 引数のDTOのメタデータを利用してプロシージャを実行する{@link SqlCommand}の実装クラスです。
 * 
 * @author taedium
 */
public class ArgumentDtoProcedureCommand implements SqlCommand,
        InjectDaoClassSupport {

    protected DataSource dataSource;

    protected ResultSetHandler resultSetHandler;

    protected StatementFactory statementFactory;

    protected ResultSetFactory resultSetFactory;

    protected ProcedureMetaData procedureMetaData;

    protected Class daoClass;

    /**
     * インスタンスを構築します。
     * 
     * @param dataSource データソース
     * @param resultSetHandler　{@link ResultSet}のハンドラ
     * @param statementFactory　{@link Statement}のファクトリ
     * @param resultSetFactory　{@link ResultSet}のファクトリ
     * @param procedureMetaData　プロシージャのメタ情報
     */
    public ArgumentDtoProcedureCommand(final DataSource dataSource,
            final ResultSetHandler resultSetHandler,
            final StatementFactory statementFactory,
            final ResultSetFactory resultSetFactory,
            final ProcedureMetaData procedureMetaData) {

        this.dataSource = dataSource;
        this.resultSetHandler = resultSetHandler;
        this.statementFactory = statementFactory;
        this.resultSetFactory = resultSetFactory;
        this.procedureMetaData = procedureMetaData;
    }

    public Object execute(final Object[] args) {
        final ArgumentDtoProcedureHandler handler = new ArgumentDtoProcedureHandler(
                dataSource, createSql(procedureMetaData), resultSetHandler,
                statementFactory, resultSetFactory, procedureMetaData);
        if (daoClass != null) {
            handler.setLoggerClass(daoClass);
        }
        handler.setFetchSize(-1);
        return handler.execute(args);
    }

    public void setDaoClass(Class clazz) {
        daoClass = clazz;
    }

    /**
     * SQLを作成します。
     * 
     * @param procedureMetaData プロシージャのメタ情報
     * @return SQL
     */
    protected String createSql(final ProcedureMetaData procedureMetaData) {
        final StringBuffer buf = new StringBuffer();
        buf.append("{");
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