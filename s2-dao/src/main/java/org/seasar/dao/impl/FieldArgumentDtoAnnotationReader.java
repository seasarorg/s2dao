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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.seasar.dao.ArgumentDtoAnnotationReader;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author taedium
 *
 */
public class FieldArgumentDtoAnnotationReader implements
        ArgumentDtoAnnotationReader {

    protected String PROCEDURE_PARAMETERS = "PROCEDURE_PARAMETERS";

    protected String PROCEDURE_PARAMETER_SUFFIX = "_PROCEDURE_PARAMETER";

    protected String VALUE_TYPE_SUFFIX = "_VALUE_TYPE";

    protected String PROCEDURE_PARAMETER_TYPE = "type";

    protected String PROCEDURE_PARAMETER_IN = "in";

    protected String PROCEDURE_PARAMETER_OUT = "out";

    protected String PROCEDURE_PARAMETER_INOUT = "inout";

    protected String PROCEDURE_PARAMETER_RETURN = "return";

    protected String PROCEDURE_PARAMETER_NAME = "name";

    protected String PROCEDURE_PARAMETER_INDEX = "index";

    public boolean isProcedureParameters(final Class dtoClass) {
        if (isSimpleType(dtoClass)) {
            return false;
        }
        final BeanDesc dtoDesc = BeanDescFactory.getBeanDesc(dtoClass);
        return dtoDesc.hasField(PROCEDURE_PARAMETERS);
    }

    public ProcedureParameterType getProcedureParameter(final BeanDesc dtoDesc,
            final PropertyDesc propertyDesc) {
        final String fieldName = propertyDesc.getPropertyName()
                + PROCEDURE_PARAMETER_SUFFIX;
        String annotation = null;
        if (dtoDesc.hasField(fieldName)) {
            final Field field = dtoDesc.getField(fieldName);
            annotation = (String) FieldUtil.get(field, null);
        }
        final Map pairs = getKeyValuePairs(annotation);
        final ProcedureParameterType ppt = new ProcedureParameterTypeImpl(
                propertyDesc);
        setupParameterName(ppt, pairs, propertyDesc.getPropertyName());
        setupParameterType(ppt, pairs);
        setupParameterIndex(ppt, pairs);
        return ppt;
    }

    public String getValueType(final BeanDesc dtoDesc,
            final PropertyDesc propertyDesc) {
        final String fieldName = propertyDesc.getPropertyName()
                + VALUE_TYPE_SUFFIX;
        if (dtoDesc.hasField(fieldName)) {
            final Field field = dtoDesc.getField(fieldName);
            return (String) FieldUtil.get(field, null);
        }
        return null;
    }

    protected Map getKeyValuePairs(final String annotation) {
        final Map map = new HashMap();
        final String[] pairs = StringUtil.split(annotation, ",");
        for (int i = 0; i < pairs.length; i++) {
            final String[] pair = pairs[i].split("=");
            if (pair.length != 2) {
                throw new RuntimeException(); //TODO
            }
            final String key = trim(pair[0]);
            final String value = trim(pair[1]);
            map.put(key, value);
        }
        return map;
    }

    protected String trim(final String s) {
        return StringUtil.ltrim(StringUtil.rtrim(s));
    }

    protected void setupParameterName(final ProcedureParameterType ppt,
            final Map pairs, final String defaultName) {
        final String name = (String) pairs.get(PROCEDURE_PARAMETER_NAME);
        if (name == null) {
            ppt.setParameterName(defaultName);
        } else {
            ppt.setParameterName(name);
        }
    }

    protected void setupParameterType(final ProcedureParameterType ppt,
            final Map pairs) {
        final String type = (String) pairs.get(PROCEDURE_PARAMETER_TYPE);
        if (type == null) {
            ppt.setInType(true);
        } else if (type.equalsIgnoreCase(PROCEDURE_PARAMETER_IN)) {
            ppt.setInType(true);
        } else if (type.equalsIgnoreCase(PROCEDURE_PARAMETER_OUT)) {
            ppt.setOutType(true);
        } else if (type.equalsIgnoreCase(PROCEDURE_PARAMETER_INOUT)) {
            ppt.setInType(true);
            ppt.setOutType(true);
        } else if (type.equalsIgnoreCase(PROCEDURE_PARAMETER_RETURN)) {
            ppt.setReturnType(true);
        } else {
            throw new RuntimeException(); //TODO
        }
    }

    protected void setupParameterIndex(final ProcedureParameterType ppt,
            final Map pairs) {
        final String index = (String) pairs.get(PROCEDURE_PARAMETER_INDEX);
        if (index == null) {
            return;
        }
        try {
            final Integer i = Integer.valueOf(index);
            ppt.setIndex(i);
        } catch (final NumberFormatException e) {
            throw new RuntimeException(e); //TODO
        }
    }

    protected boolean isSimpleType(final Class clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        return clazz == String.class || clazz.isPrimitive()
                || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz)
                || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz)
                || clazz == byte[].class;
    }

}
