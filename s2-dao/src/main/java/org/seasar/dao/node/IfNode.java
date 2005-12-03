package org.seasar.dao.node;

import org.seasar.dao.CommandContext;
import org.seasar.dao.IllegalBoolExpressionRuntimeException;
import org.seasar.framework.util.OgnlUtil;

/**
 * @author higa
 *
 */
public class IfNode extends ContainerNode {

	private String expression_;
	private Object parsedExpression_;
	private ElseNode elseNode_;
	
	public IfNode(String expression) {
		expression_ = expression;
		parsedExpression_ = OgnlUtil.parseExpression(expression);
	}

	public String getExpression() {
		return expression_;
	}
	
	public ElseNode getElseNode() {
		return elseNode_;
	}
	
	public void setElseNode(ElseNode elseNode) {
		elseNode_ = elseNode;
	}

	/**
	 * @see org.seasar.dao.Node#accept(org.seasar.dao.QueryContext)
	 */
	public void accept(CommandContext ctx) {
		Object result = OgnlUtil.getValue(parsedExpression_, ctx);
		if (result instanceof Boolean) {
			if (((Boolean) result).booleanValue()) {
				super.accept(ctx);
				ctx.setEnabled(true);
			} else if (elseNode_ != null) {
				elseNode_.accept(ctx);
				ctx.setEnabled(true);
			}
		} else {
			throw new IllegalBoolExpressionRuntimeException(expression_);
		}
		
	}

}
