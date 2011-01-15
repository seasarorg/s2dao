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
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.BasicUpdateHandler;

/**
 * @author higa
 * 
 */
/*
 * INSERT, UPDATE, DELETE文用
 */
public class UpdateDynamicCommand extends AbstractDynamicCommand {

    public UpdateDynamicCommand(DataSource dataSource,
            StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    public Object execute(Object[] args) {
        CommandContext ctx = apply(args);
        BasicUpdateHandler updateHandler = new BasicUpdateHandler(
                getDataSource(), ctx.getSql(), getStatementFactory());
        injectDaoClass(updateHandler);
        return new Integer(updateHandler.execute(ctx.getBindVariables(), ctx
                .getBindVariableTypes()));
    }

}