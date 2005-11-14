package org.seasar.dao.node;

import org.seasar.dao.CommandContext;

/**
 * @author higa
 *
 */
public class PrefixSqlNode extends AbstractNode {

	private String prefix_;
	private String sql_;
	
	public PrefixSqlNode(String prefix, String sql) {
		prefix_ = prefix;
		sql_ = sql;
	}
	
	public String getPrefix() {
		return prefix_;
	}

	public String getSql() {
		return sql_;
	}

	/**
	 * @see org.seasar.dao.Node#accept(org.seasar.dao.QueryContext)
	 */
	public void accept(CommandContext ctx) {
		if (ctx.isEnabled()) {
			ctx.addSql(prefix_);
		}
		ctx.addSql(sql_);
	}

}
