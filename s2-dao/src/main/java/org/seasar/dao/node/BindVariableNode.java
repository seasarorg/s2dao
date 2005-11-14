package org.seasar.dao.node;

import org.seasar.dao.CommandContext;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.StringUtil;

/**
 * @author higa
 *
 */
public class BindVariableNode extends AbstractNode {

	private String expression_;
	private String[] names;
	
	public BindVariableNode(String expression) {
		expression_ = expression;
		names = StringUtil.split(expression, ".");
//		baseName_ = array[0];
//		if (array.length > 1) {
//			propertyName_ = array[1];
//		}
	}

	public String getExpression() {
		return expression_;
	}

	/**
	 * @see org.seasar.dao.Node#accept(org.seasar.dao.QueryContext)
	 */
	public void accept(CommandContext ctx) {
		Object value = ctx.getArg(names[0]);
		Class clazz = ctx.getArgType(names[0]);
		for(int pos = 1;pos < names.length;pos++){
			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
			PropertyDesc pd = beanDesc.getPropertyDesc(names[pos]);
			if (value == null) {
				break;
			}
			value = pd.getValue(value);
			clazz = pd.getPropertyType();
		}
		ctx.addSql("?", value, clazz);
	}
}
