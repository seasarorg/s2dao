package org.seasar.dao;

/**
 * @author higa
 *
 */
public interface CommandContext {

	public Object getArg(String name);
	
	public Class getArgType(String name);
	
	public void addArg(String name, Object arg, Class argType);

	public String getSql();
	
	public Object[] getBindVariables();
	
	public Class[] getBindVariableTypes();

	public CommandContext addSql(String sql);
	
	public CommandContext addSql(String sql, Object bindVariable, Class bindVariableType);
	
	public CommandContext addSql(String sql, Object[] bindVariables, Class[] bindVariableTypes);
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
}
