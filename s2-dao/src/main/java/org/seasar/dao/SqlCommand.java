package org.seasar.dao;


/**
 * @author higa
 *  
 */
public interface SqlCommand {

	public Object execute(Object[] args);
}