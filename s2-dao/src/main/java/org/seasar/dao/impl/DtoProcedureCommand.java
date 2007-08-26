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

import javax.sql.DataSource;

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.handler.DtoProcedureHandler;
import org.seasar.dao.handler.ProcedureHandler;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * 引数のDTOのメタデータを利用してプロシージャを実行するコマンドです。
 * 
 * @author taedium
 */
public class DtoProcedureCommand extends AbstractProcedureCommand {

    protected ProcedureHandler handler;

    public DtoProcedureCommand(final DataSource dataSource,
            final ResultSetHandler resultSetHandler,
            final StatementFactory statementFactory,
            final ResultSetFactory resultSetFactory,
            final ProcedureMetaData procedureMetaData) {

        final DtoProcedureHandler handler = new DtoProcedureHandler(dataSource,
                createSql(procedureMetaData), resultSetHandler,
                statementFactory, resultSetFactory, procedureMetaData);
        handler.setFetchSize(-1);
        this.handler = handler;
    }

    public Object execute(final Object[] args) {
        return handler.execute(args);
    }

}