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
public class EmbeddedValueNode extends AbstractNode {

	private String expression_;
	private String baseName_;
	private String propertyName_;
	
	public EmbeddedValueNode(String expression) {
		expression_ = expression;
		String[] array = StringUtil.split(expression, ".");
		baseName_ = array[0];
		if (array.length > 1) {
			propertyName_ = array[1];
		}
	}

	public String getExpression() {
		return expression_;
	}

	/**
	 * @see org.seasar.dao.Node#accept(org.seasar.dao.QueryContext)
	 */
	public void accept(CommandContext ctx) {
		Object value = ctx.getArg(baseName_);
		Class clazz = ctx.getArgType(baseName_);
		if (propertyName_ != null) {
			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
			PropertyDesc pd = beanDesc.getPropertyDesc(propertyName_);
			value = pd.getValue(value);
			clazz = pd.getPropertyType();
		}
		if (value != null) {
			ctx.addSql(value.toString());
		}
	}
}
