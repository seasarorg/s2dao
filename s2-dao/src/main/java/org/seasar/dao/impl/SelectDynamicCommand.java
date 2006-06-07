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
package org.seasar.dao.impl;

import javax.sql.DataSource;

import org.seasar.dao.CommandContext;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;

/**
 * @author higa
 * 
 */
public class SelectDynamicCommand extends AbstractDynamicCommand {

    private ResultSetHandler resultSetHandler_;

    private ResultSetFactory resultSetFactory_;

    public SelectDynamicCommand(DataSource dataSource,
            StatementFactory statementFactory,
            ResultSetHandler resultSetHandler, ResultSetFactory resultSetFactory) {

        super(dataSource, statementFactory);
        resultSetHandler_ = resultSetHandler;
        resultSetFactory_ = resultSetFactory;
    }

    public ResultSetHandler getResultSetHandler() {
        return resultSetHandler_;
    }

    public Object execute(Object[] args) {
        CommandContext ctx = apply(args);
        BasicSelectHandler selectHandler = new BasicSelectHandler(
                getDataSource(), ctx.getSql(), resultSetHandler_,
                getStatementFactory(), resultSetFactory_);
        selectHandler.setFetchSize(-1);
        return selectHandler.execute(ctx.getBindVariables(), ctx
                .getBindVariableTypes());
    }
}