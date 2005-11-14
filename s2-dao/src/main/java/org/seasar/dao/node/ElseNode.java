package org.seasar.dao.node;

import org.seasar.dao.CommandContext;

/**
 * @author higa
 *
 */
public class ElseNode extends ContainerNode {

	public ElseNode() {
	}
	
	public void accept(CommandContext ctx) {
		super.accept(ctx);
		ctx.setEnabled(true);
	}
}