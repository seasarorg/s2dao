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

	private Node rootNode_;

	private String[] argNames_ = new String[0];
	
	private Class[] argTypes_ = new Class[0];

	public AbstractDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
		super(dataSource, statementFactory);
	}

	public void setSql(String sql) {
		super.setSql(sql);
		rootNode_ = new SqlParserImpl(sql).parse();
	}

	public String[] getArgNames() {
		return argNames_;
	}

	public void setArgNames(String[] argNames) {
		argNames_ = argNames;
	}
	
	public Class[] getArgTypes() {
		return argTypes_;
	}
	
	public void setArgTypes(Class[] argTypes) {
		argTypes_ = argTypes;
	}

	protected CommandContext apply(Object[] args) {
		CommandContext ctx = createCommandContext(args);
		rootNode_.accept(ctx);
		return ctx;
	}

	protected CommandContext createCommandContext(Object[] args) {
		CommandContext ctx = new CommandContextImpl();
		if (args != null) {
			for (int i = 0; i < args.length; ++i) {
				Class argType = null;
				if (args[i] != null)
				if (i < argTypes_.length) {
					argType = argTypes_[i];
				} else if (args[i] != null) {
					argType = args[i].getClass();
				}
				if (i < argNames_.length) {
					ctx.addArg(argNames_[i], args[i], argType);
				} else {
					ctx.addArg("$" + (i + 1), args[i], argType);
				}
			}
		}
		return ctx;
	}
}