package org.seasar.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.seasar.dao.DaoAnnotationReader;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author uehara keizou
 *
 */
public class FieldDaoAnnotationReader implements DaoAnnotationReader {
	
	public String BEAN = "BEAN";

	public String PROCEDURE_SUFFIX = "_PROCEDURE";

	public String ARGS_SUFFIX = "_ARGS";

	public String SQL_SUFFIX = "_SQL";

	public String QUERY_SUFFIX = "_QUERY";

	public String NO_PERSISTENT_PROPS_SUFFIX = "_NO_PERSISTENT_PROPS";

	public String PERSISTENT_PROPS_SUFFIX = "_PERSISTENT_PROPS";
	
	protected BeanDesc daoBeanDesc_;
	
	
	/**
	 * @param daoBeanDesc_
	 */
	public FieldDaoAnnotationReader(BeanDesc daoBeanDesc_) {
		this.daoBeanDesc_ = daoBeanDesc_;
	}
	public String[] getArgNames(Method method) {
		String argsKey = method.getName() + ARGS_SUFFIX;
		if (daoBeanDesc_.hasField(argsKey)) {
			Field argNamesField = daoBeanDesc_.getField(argsKey);
			String argNames = (String) FieldUtil.get(argNamesField, null);
			return StringUtil.split(argNames, " ,");
		} else {
			return new String[0];
		}
	}
	public String getQuery(Method method) {
		String key = method.getName() + QUERY_SUFFIX;
		if (daoBeanDesc_.hasField(key)) {
			Field queryField = daoBeanDesc_.getField(key);
			return (String) FieldUtil.get(queryField, null);
		} else {
			return null;
		}
	}
	public String getStoredProcedureName(Method method) {
		String key = method.getName() + PROCEDURE_SUFFIX;
		if (daoBeanDesc_.hasField(key)) {
			Field queryField = daoBeanDesc_.getField(key);
			return (String) FieldUtil.get(queryField, null);
		} else {
			return null;
		}
	}
	public Class getBeanClass() {
		Field beanField = daoBeanDesc_.getField(BEAN);
		return (Class) FieldUtil.get(beanField, null);
	}

	public String[] getNoPersistentProps(Method method) {
		return getProps(method,method.getName() + NO_PERSISTENT_PROPS_SUFFIX);
	}
	public String[] getPersistentProps(Method method) {
		return getProps(method,method.getName() + PERSISTENT_PROPS_SUFFIX);
	}
	private String[] getProps(Method method,String fieldName){
		if (daoBeanDesc_.hasField(fieldName)) {
			Field field = daoBeanDesc_.getField(fieldName);
			String s = (String) FieldUtil.get(field, null);
			return StringUtil.split(s, ", ");
		}
		return null;
	}
	public String getSQL(Method method, String dbmsSuffix) {
		String key = method.getName() + dbmsSuffix + SQL_SUFFIX;
		if (daoBeanDesc_.hasField(key)) {
			Field queryField = daoBeanDesc_.getField(key);
			return (String) FieldUtil.get(queryField, null);
		}
		key = method.getName() + SQL_SUFFIX;
		if (daoBeanDesc_.hasField(key)) {
			Field queryField = daoBeanDesc_.getField(key);
			return (String) FieldUtil.get(queryField, null);
		}
		return null;
	}
	
}

