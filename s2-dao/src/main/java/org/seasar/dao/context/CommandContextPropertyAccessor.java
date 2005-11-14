package org.seasar.dao.context;

import java.util.Map;

import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import org.seasar.dao.CommandContext;

/**
 * @author higa
 *
 */
public class CommandContextPropertyAccessor extends ObjectPropertyAccessor {

	public Object getProperty(Map cx, Object target, Object name)
		throws OgnlException {

		CommandContext ctx = (CommandContext) target;
		String argName = name.toString();
		return ctx.getArg(argName);
	}

}
