package org.seasar.dao;

import java.lang.reflect.Method;

/**
 * @author uehara keizou
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface DaoAnnotationReader {

	/**
	 * @param name
	 * @return
	 */
	String getQuery(Method method);
	
	String getStoredProcedureName(Method method);
	/**
	 * @param method
	 * @return
	 */
	String[] getArgNames(Method method);

	/**
	 * @return
	 */
	Class getBeanClass();

	/**
	 * @param methodName
	 * @return
	 */
	String[] getNoPersistentProps(Method method);

	/**
	 * @param methodName
	 * @return
	 */
	String[] getPersistentProps(Method method);

	/**
	 * @param name
	 * @param suffix
	 * @return
	 */
	String getSQL(Method method, String suffix);

}
