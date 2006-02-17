/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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
