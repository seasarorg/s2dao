package org.seasar.dao.node;

import org.seasar.dao.CommandContext;

/**
 * @author higa
 *
 */
public class ContainerNode extends AbstractNode {

	public ContainerNode() {
	}

	/**
	 * @see org.seasar.dao.Node#accept(org.seasar.dao.QueryContext)
	 */
	public void accept(CommandContext ctx) {
		for (int i = 0; i < getChildSize(); ++i) {
			getChild(i).accept(ctx);
		}
	}
}