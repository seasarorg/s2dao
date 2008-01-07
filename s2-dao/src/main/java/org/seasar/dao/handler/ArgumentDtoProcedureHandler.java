/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
import java.sql.Statement;

import javax.sql.DataSource;

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;
import org.seasar.framework.exception.EmptyRuntimeException;
import org.seasar.framework.exception.SIllegalArgumentException;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * 引数のDTOに含まれる値をプロシージャにバインディングする{@link ProcedureHandler}の実装クラスです。
 * 
 * @author taedium
 */
public class ArgumentDtoProcedureHandler extends BasicSelectHandler implements
        ProcedureHandler {

    private ProcedureMetaData procedureMetaData;

    /**
     * プロシージャのメタ情報を返します。
     * 
     * @return プロシージャのメタ情報
     */
    public ProcedureMetaData getProcedureMetaData() {
        return procedureMetaData;
    }

    /**
     * プロシージャのメタ情報を設定します。
     * 
     * @param procedureMetaData プロシージャのメタ情報
     */
    public void setProcedureMetaData(final ProcedureMetaData procedureMetaData) {
        this.procedureMetaData = procedureMetaData;
    }

    /**
     * インスタンスを構築します。
     * 
     * @param dataSource データソース
     * @param sql SQL
     * @param resultSetHandler　{@link ResultSet}のハンドラ
     * @param statementFactory　{@link Statement}のファクトリ
     * @param resultSetFactory　{@link ResultSet}のファクトリ
     * @param procedureMetaData　プロシージャのメタ情報
     */
    public ArgumentDtoProcedureHandler(final DataSource dataSource,
            final String sql, final ResultSetHandler resultSetHandler,
            final StatementFactory statementFactory,
            final ResultSetFactory resultSetFactory,
            final ProcedureMetaData procedureMetaData) {

        super(dataSource, sql, resultSetHandler, statementFactory,
                resultSetFactory);
        setProcedureMetaData(procedureMetaData);
    }

    public Object execute(final Connection connection, final Object[] args,
            final Class[] argTypes) {
        final Object dto = getArgumentDto(args);
        logSql(args, argTypes);
        CallableStatement cs = null;
        try {
            cs = prepareCallableStatement(connection);
            bindArgs(cs, dto);
            if (cs.execute()) {
                return handleResultSet(cs);
            } else {
                return handleOutParameters(cs, dto);
            }
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            StatementUtil.close(cs);
        }
    }

    protected String getCompleteSql(final Object[] args) {
        String sql = getSql();
        Object dto = getArgumentDto(args);
        if (args == null || dto == null) {
            return sql;
        }
        StringBuffer buf = new StringBuffer(100);
        int pos = 0;
        int pos2 = 0;
        int size = procedureMetaData.getParameterTypeSize();
        for (int i = 0; i < size; i++) {
            ProcedureParameterType ppt = procedureMetaData.getParameterType(i);
            if ((pos2 = sql.indexOf('?', pos)) < 0) {
                break;
            }
            buf.append(sql.substring(pos, pos2));
            pos = pos2 + 1;
            if (ppt.isInType()) {
                buf.append(getBindVariableText(ppt.getValue(dto)));
            } else {
                buf.append(sql.substring(pos2, pos));
            }
        }
        buf.append(sql.substring(pos));
        return buf.toString();
    }

    /**
     * ストアドプロシージャを表す文を生成します。
     * 
     * @param connection コネクション
     * @return　ストアドプロシージャを表す文
     */
    protected CallableStatement prepareCallableStatement(
            final Connection connection) {
        if (getSql() == null) {
            throw new EmptyRuntimeException("sql");
        }
        final CallableStatement cs = getStatementFactory()
                .createCallableStatement(connection, getSql());
        if (getFetchSize() > -1) {
            StatementUtil.setFetchSize(cs, getFetchSize());
        }
        if (getMaxRows() > -1) {
            StatementUtil.setMaxRows(cs, getMaxRows());
        }
        return cs;
    }

    /**
     * 引数をバインドします。
     * 
     * @param cs　ストアドプロシージャを表す文
     * @param dto 引数のDTO
     * @throws SQLException SQL例外が発生した場合
     */
    protected void bindArgs(final CallableStatement cs, final Object dto)
            throws SQLException {
        if (dto == null) {
            return;
        }
        final int size = procedureMetaData.getParameterTypeSize();
        for (int i = 0; i < size; i++) {
            final ProcedureParameterType ppt = procedureMetaData
                    .getParameterType(i);
            final ValueType valueType = ppt.getValueType();
            if (ppt.isOutType()) {
                valueType.registerOutParameter(cs, i + 1);
            }
            if (ppt.isInType()) {
                final Object value = ppt.getValue(dto);
                valueType.bindValue(cs, i + 1, value);
            }
        }
    }

    /**
     * <code>ResultSet</code>を処理します。
     * 
     * @param cs ストアドプロシージャを表す文
     * @return <code>ResultSet</code>から変換された値
     * @throws SQLException SQL例外が発生した場合
     */
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

    /**
     * <code>OUT</code>パラメータを処理します。
     * 
     * @param cs　ストアドプロシージャを表す文
     * @param args 引数のDTO
     * @return 引数のDTO
     * @throws SQLException
     */
    protected Object handleOutParameters(final CallableStatement cs,
            final Object dto) throws SQLException {
        if (dto == null) {
            return null;
        }
        final int size = procedureMetaData.getParameterTypeSize();
        for (int i = 0; i < size; i++) {
            final ProcedureParameterType ppt = procedureMetaData
                    .getParameterType(i);
            final ValueType valueType = ppt.getValueType();
            if (ppt.isOutType()) {
                final Object value = valueType.getValue(cs, i + 1);
                ppt.setValue(dto, value);
            }
        }
        return dto;
    }

    /**
     * 引数のDTOを返します。
     * 
     * @param args 引数のDTO
     * @return 引数のDTO
     */
    protected Object getArgumentDto(Object[] args) {
        if (args.length == 0) {
            return null;
        }
        if (args.length == 1) {
            if (args[0] == null) {
                throw new SIllegalArgumentException("EDAO0029", new Object[] {});
            }
            return args[0];
        }
        throw new IllegalArgumentException("args");
    }

}
