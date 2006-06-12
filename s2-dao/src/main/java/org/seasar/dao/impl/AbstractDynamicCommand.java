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
import org.seasar.dao.Node;
import org.seasar.dao.context.CommandContextImpl;
import org.seasar.dao.parser.SqlParserImpl;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author higa
 * 
 */
public abstract class AbstractDynamicCommand extends AbstractSqlCommand {

    private Node rootNode;

    private String[] argNames = new String[0];

    private Class[] argTypes = new Class[0];

    public AbstractDynamicCommand(DataSource dataSource,
            StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    public void setSql(String sql) {
        super.setSql(sql);
        this.rootNode = new SqlParserImpl(sql).parse();
    }

    public String[] getArgNames() {
        return argNames;
    }

    public void setArgNames(String[] argNames) {
        this.argNames = argNames;
    }

    public Class[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class[] argTypes) {
        this.argTypes = argTypes;
    }

    protected CommandContext apply(Object[] args) {
        CommandContext ctx = createCommandContext(args);
        rootNode.accept(ctx);
        return ctx;
    }

    protected CommandContext createCommandContext(Object[] args) {
        CommandContext ctx = new CommandContextImpl();
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                Class argType = null;
                if (args[i] != null) {
                    if (i < argTypes.length) {
                        argType = argTypes[i];
                    } else if (args[i] != null) {
                        argType = args[i].getClass();
                    }
                }
                if (i < argNames.length) {
                    ctx.addArg(argNames[i], args[i], argType);
                } else {
                    ctx.addArg("$" + (i + 1), args[i], argType);
                }
            }
        }
        return ctx;
    }
}