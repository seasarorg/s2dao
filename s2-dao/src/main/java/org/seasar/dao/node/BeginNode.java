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