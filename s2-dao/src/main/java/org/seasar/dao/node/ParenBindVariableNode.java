package org.seasar.dao.node;

import java.lang.reflect.Array;
import java.util.List;

import org.seasar.dao.CommandContext;
import org.seasar.framework.util.OgnlUtil;

/**
 * @author higa
 *  
 */
public class ParenBindVariableNode extends AbstractNode {

	private String expression_;

	private Object parsedExpression_;

	public ParenBindVariableNode(String expression) {
		expression_ = expression;
		parsedExpression_ = OgnlUtil.parseExpression(expression);
	}

	public String getExpression() {
		return expression_;
	}

	/**
	 * @see org.seasar.dao.Node#accept(org.seasar.dao.QueryContext)
	 */
	public void accept(CommandContext ctx) {
		Object var = OgnlUtil.getValue(parsedExpression_, ctx);
		if (var instanceof List) {
			bindArray(ctx, ((List) var).toArray());
		} else if (var == null) {
			return;
		} else if (var.getClass().isArray()) {
			bindArray(ctx, var);
		} else {
			ctx.addSql("?", var, var.getClass());
		}

	}

	private void bindArray(CommandContext ctx, Object array) {
		int length = Array.getLength(array);
		if (length == 0) {
			return;
		}
		Class clazz = null;
		for (int i = 0; i < length; ++i) {
			Object o = Array.get(array, i);
			if (o != null) {
				clazz = o.getClass();
			}
		}
		ctx.addSql("(");
		ctx.addSql("?", Array.get(array, 0), clazz);
		for (int i = 1; i < length; ++i) {
			ctx.addSql(", ?", Array.get(array, i), clazz);
		}
		ctx.addSql(")");
	}
}