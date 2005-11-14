package org.seasar.dao.context;

import java.util.ArrayList;
import java.util.List;

import ognl.OgnlRuntime;

import org.seasar.dao.CommandContext;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.CaseInsensitiveMap;

/**
 * @author higa
 *  
 */
public class CommandContextImpl implements CommandContext {

	private static Logger logger_ = Logger.getLogger(CommandContextImpl.class);

	private CaseInsensitiveMap args_ = new CaseInsensitiveMap();
	
	private CaseInsensitiveMap argTypes_ = new CaseInsensitiveMap();

	private StringBuffer sqlBuf_ = new StringBuffer(100);

	private List bindVariables_ = new ArrayList();
	
	private List bindVariableTypes_ = new ArrayList();

	private boolean enabled_ = true;

	private CommandContext parent_;

	static {
		OgnlRuntime.setPropertyAccessor(CommandContext.class,
				new CommandContextPropertyAccessor());
	}

	public CommandContextImpl() {
	}

	public CommandContextImpl(CommandContext parent) {
		parent_ = parent;
		enabled_ = false;
	}

	/**
	 * @see org.seasar.dao.CommandContext#getArg(java.lang.String)
	 */
	public Object getArg(String name) {
		if (args_.containsKey(name)) {
			return args_.get(name);
		} else if (parent_ != null) {
			return parent_.getArg(name);
		} else {
			if (args_.size() == 1) {
				return args_.get(0);
			}
			logger_.log("WDAO0001", new Object[] { name });
			return null;
		}
	}
	
	/**
	 * @see org.seasar.dao.CommandContext#getArgType(java.lang.String)
	 */
	public Class getArgType(String name) {
		if (argTypes_.containsKey(name)) {
			return (Class) argTypes_.get(name);
		} else if (parent_ != null) {
			return parent_.getArgType(name);
		} else {
			if (argTypes_.size() == 1) {
				return (Class) argTypes_.get(0);
			}
			logger_.log("WDAO0001", new Object[] { name });
			return null;
		}
	}

	/**
	 * @see org.seasar.dao.CommandContext#addArg(java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public void addArg(String name, Object arg, Class argType) {
		args_.put(name, arg);
		argTypes_.put(name, argType);
	}

	/**
	 * @see org.seasar.dao.CommandContext#getSql()
	 */
	public String getSql() {
		return sqlBuf_.toString();
	}

	/**
	 * @see org.seasar.dao.CommandContext#getBindVariables()
	 */
	public Object[] getBindVariables() {
		return bindVariables_.toArray(new Object[bindVariables_.size()]);
	}
	
	/**
	 * @see org.seasar.dao.CommandContext#getBindVariableTypes()
	 */
	public Class[] getBindVariableTypes() {
		return (Class[]) bindVariableTypes_.toArray(new Class[bindVariableTypes_.size()]);
	}

	/**
	 * @see org.seasar.dao.CommandContext#addSql(java.lang.String)
	 */
	public CommandContext addSql(String sql) {
		sqlBuf_.append(sql);
		return this;
	}

	/**
	 * @see org.seasar.dao.CommandContext#addSql(java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public CommandContext addSql(String sql, Object bindVariable,
			Class bindVariableType) {
		
		sqlBuf_.append(sql);
		bindVariables_.add(bindVariable);
		bindVariableTypes_.add(bindVariableType);
		return this;
	}

	public CommandContext addSql(String sql, Object[] bindVariables,
			Class[] bindVariableTypes) {
		
		sqlBuf_.append(sql);
		for (int i = 0; i < bindVariables.length; ++i) {
			bindVariables_.add(bindVariables[i]);
			bindVariableTypes_.add(bindVariableTypes[i]);
		}
		return this;
	}

	/**
	 * @see org.seasar.dao.CommandContext#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled_;
	}

	/**
	 * @see org.seasar.dao.CommandContext#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		enabled_ = enabled;
	}
}