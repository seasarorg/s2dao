/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.seasar.dao.DaoAnnotationReader;
import org.seasar.dao.NullBean;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author uehara keizou
 * 
 */
public class FieldDaoAnnotationReader implements DaoAnnotationReader {

    public String BEAN = "BEAN";

    public String BEAN_SUFFIX = "_BEAN";

    public String PROCEDURE_SUFFIX = "_PROCEDURE";

    public String ARGS_SUFFIX = "_ARGS";

    public String SQL_SUFFIX = "_SQL";

    public String QUERY_SUFFIX = "_QUERY";

    public String NO_PERSISTENT_PROPS_SUFFIX = "_NO_PERSISTENT_PROPS";

    public String PERSISTENT_PROPS_SUFFIX = "_PERSISTENT_PROPS";

    public String SQL_FILE_SUFFIX = "_SQL_FILE";

    protected BeanDesc daoBeanDesc;

    /**
     * @param daoBeanDesc
     */
    public FieldDaoAnnotationReader(BeanDesc daoBeanDesc) {
        this.daoBeanDesc = daoBeanDesc;
    }

    public String[] getArgNames(Method method) {
        String argsKey = method.getName() + ARGS_SUFFIX;
        if (daoBeanDesc.hasField(argsKey)) {
            Field argNamesField = daoBeanDesc.getField(argsKey);
            String argNames = (String) FieldUtil.get(argNamesField, null);
            return StringUtil.split(argNames, " ,");
        } else {
            return new String[0];
        }
    }

    public String getQuery(Method method) {
        String key = method.getName() + QUERY_SUFFIX;
        if (daoBeanDesc.hasField(key)) {
            Field queryField = daoBeanDesc.getField(key);
            return (String) FieldUtil.get(queryField, null);
        } else {
            return null;
        }
    }

    public String getStoredProcedureName(Method method) {
        String key = method.getName() + PROCEDURE_SUFFIX;
        if (daoBeanDesc.hasField(key)) {
            Field queryField = daoBeanDesc.getField(key);
            return (String) FieldUtil.get(queryField, null);
        } else {
            return null;
        }
    }

    public Class getBeanClass() {
        if (daoBeanDesc.hasField(BEAN)) {
            Field beanField = daoBeanDesc.getField(BEAN);
            return (Class) FieldUtil.get(beanField, null);
        }
        return NullBean.class;
    }

    public Class getBeanClass(Method method) {
        String fieldName = method.getName() + BEAN_SUFFIX;
        if (daoBeanDesc.hasField(fieldName)) {
            Field field = daoBeanDesc.getField(fieldName);
            return (Class) FieldUtil.get(field, null);
        }
        if (List.class.isAssignableFrom(method.getReturnType())) {
            return null;
        }
        if (method.getReturnType().isArray()) {
            return method.getReturnType().getComponentType();
        }
        if (isSingleValueType(method.getReturnType())) {
            return null;
        }
        return method.getReturnType();
    }

    protected boolean isSingleValueType(Class clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }
        return clazz == String.class || clazz == Boolean.class
                || Number.class.isAssignableFrom(clazz)
                || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz);
    }

    public String[] getNoPersistentProps(Method method) {
        return getProps(method, method.getName() + NO_PERSISTENT_PROPS_SUFFIX);
    }

    public String[] getPersistentProps(Method method) {
        return getProps(method, method.getName() + PERSISTENT_PROPS_SUFFIX);
    }

    private String[] getProps(Method method, String fieldName) {
        if (daoBeanDesc.hasField(fieldName)) {
            Field field = daoBeanDesc.getField(fieldName);
            String s = (String) FieldUtil.get(field, null);
            return StringUtil.split(s, ", ");
        }
        return null;
    }

    public String getSQL(Method method, String dbmsSuffix) {
        String key = method.getName() + dbmsSuffix + SQL_SUFFIX;
        if (daoBeanDesc.hasField(key)) {
            Field queryField = daoBeanDesc.getField(key);
            return (String) FieldUtil.get(queryField, null);
        }
        key = method.getName() + SQL_SUFFIX;
        if (daoBeanDesc.hasField(key)) {
            Field queryField = daoBeanDesc.getField(key);
            return (String) FieldUtil.get(queryField, null);
        }
        return null;
    }

    public boolean isSqlFile(final Method method) {
        final String fieldName = method.getName() + SQL_FILE_SUFFIX;
        if (daoBeanDesc.hasField(fieldName)) {
            return true;
        }
        return false;
    }

}
