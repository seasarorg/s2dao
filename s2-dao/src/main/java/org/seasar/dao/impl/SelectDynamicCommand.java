/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import javax.sql.DataSource;

import org.seasar.dao.CommandContext;
import org.seasar.dao.pager.PagingSqlRewriter;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;

/**
 * @author higa
 * @author manhole
 */
public class SelectDynamicCommand extends AbstractDynamicCommand {

    private ResultSetHandler resultSetHandler;

    private ResultSetFactory resultSetFactory;

    private PagingSqlRewriter pagingSqlRewriter;

    public SelectDynamicCommand(DataSource dataSource,
            StatementFactory statementFactory,
            ResultSetHandler resultSetHandler,
            ResultSetFactory resultSetFactory,
            PagingSqlRewriter pagingSqlRewriter) {

        super(dataSource, statementFactory);
        this.resultSetHandler = resultSetHandler;
        this.resultSetFactory = resultSetFactory;
        this.pagingSqlRewriter = pagingSqlRewriter;
    }

    public ResultSetHandler getResultSetHandler() {
        return resultSetHandler;
    }

    public Object execute(Object[] args) {
        CommandContext ctx = apply(args);
        Object[] bindVariables = ctx.getBindVariables();
        Class[] bindVariableTypes = ctx.getBindVariableTypes();
        String sql = ctx.getSql();
        String executingSql = pagingSqlRewriter.rewrite(sql, bindVariables,
                bindVariableTypes);
        BasicSelectHandler selectHandler = new BasicSelectHandler(
                getDataSource(), executingSql, resultSetHandler,
                getStatementFactory(), resultSetFactory);
        injectDaoClass(selectHandler);
        /*
         * Statement#setFetchSizeをサポートしていないDBMSがあるため、
         * S2DaoからはsetFetchSizeを行わないようにする。
         * https://www.seasar.org/issues/browse/DAO-2
         */
        selectHandler.setFetchSize(-1);
        Object ret = selectHandler.execute(ctx.getBindVariables(), ctx
                .getBindVariableTypes());
        pagingSqlRewriter.setCount(sql, args, bindVariables, bindVariableTypes);

        return ret;
    }
}
