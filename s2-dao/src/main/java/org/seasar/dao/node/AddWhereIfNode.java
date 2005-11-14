package org.seasar.dao.node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.dao.CommandContext;
import org.seasar.dao.context.CommandContextImpl;

public class AddWhereIfNode extends ContainerNode {
	Pattern pat = Pattern.compile("\\s*(order\\sby)|$)");
	public AddWhereIfNode() {
	}
	
	public void accept(CommandContext ctx) {
		CommandContext childCtx = new CommandContextImpl(ctx);
		super.accept(childCtx);
		if (childCtx.isEnabled()) {
			String sql = childCtx.getSql();
			Matcher m = pat.matcher(sql);
			if (!m.lookingAt()) {
				sql = " WHERE " + sql;
			}
			ctx.addSql(sql, childCtx.getBindVariables(), childCtx.getBindVariableTypes());
		}
	}

}