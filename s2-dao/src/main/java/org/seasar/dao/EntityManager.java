package org.seasar.dao;

import java.util.List;

/**
 * @author higa
 *
 */
public interface EntityManager {

	public List find(String query);
	
	public List find(String query, Object arg1);
	
	public List find(String query, Object arg1, Object arg2);
	
	public List find(String query, Object arg1, Object arg2, Object arg3);
	
	public List find(String query, Object[] args);
	
	public Object[] findArray(String query);
	
	public Object[] findArray(String query, Object arg1);
	
	public Object[] findArray(String query, Object arg1, Object arg2);
	
	public Object[] findArray(String query, Object arg1, Object arg2, Object arg3);
	
	public Object[] findArray(String query, Object[] args);
	
	public Object findBean(String query);
	
	public Object findBean(String query, Object arg1);
	
	public Object findBean(String query, Object arg1, Object arg2);
	
	public Object findBean(String query, Object arg1, Object arg2, Object arg3);
	
	public Object findBean(String query, Object[] args);
	
	public Object findObject(String query);
	
	public Object findObject(String query, Object arg1);
	
	public Object findObject(String query, Object arg1, Object arg2);
	
	public Object findObject(String query, Object arg1, Object arg2, Object arg3);
	
	public Object findObject(String query, Object[] args);
}
