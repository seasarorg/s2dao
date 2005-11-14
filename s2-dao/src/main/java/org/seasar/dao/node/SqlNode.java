package org.seasar.dao.node;

import org.seasar.dao.CommandContext;

/**
 * @author higa
 *
 */
public class SqlNode extends AbstractNode {

	private String sql_;
	
	public SqlNode(String sql) {
		sql_ = sql;
	}

	public String getSql() {
		return sql_;
	}

	/**
	 * @see org.seasar.dao.Node#accept(org.seasar.dao.QueryContext)
	 */
	public void accept(CommandContext ctx) {
		ctx.addSql(sql_);
	}

}
