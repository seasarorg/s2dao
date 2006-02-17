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
package org.seasar.dao.node;

import org.seasar.dao.CommandContext;
import org.seasar.dao.context.CommandContextImpl;

/**
 * @author higa
 *
 */
public class BeginNode extends ContainerNode {

	public BeginNode() {
	}
	
	public void accept(CommandContext ctx) {
		CommandContext childCtx = new CommandContextImpl(ctx);
		super.accept(childCtx);
		if (childCtx.isEnabled()) {
			ctx.addSql(childCtx.getSql(), childCtx.getBindVariables(), childCtx.getBindVariableTypes());
		}
	}
}