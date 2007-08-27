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

import org.seasar.dao.ArgumentDtoAnnotationReader;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.FieldUtil;

/**
 * フィールドアノテーションを読み取る{@link ArgumentDtoAnnotationReader}の実装クラスです。
 * 
 * @author taedium
 */
public class FieldArgumentDtoAnnotationReader implements
        ArgumentDtoAnnotationReader {

    /** <code>PROCEDURE_PARAMETERS</code>アノテーション */
    protected String PROCEDURE_PARAMETERS = "PROCEDURE_PARAMETERS";

    /** <code>PROCEDURE_PARAMETER</code>アノテーションのサフィックス */
    protected String PROCEDURE_PARAMETER_SUFFIX = "_PROCEDURE_PARAMETER";

    /** <code>VALUE_TYPE</code>アノテーションのサフィックス */
    protected String VALUE_TYPE_SUFFIX = "_VALUE_TYPE";

    public boolean isProcedureParameters(final Class dtoClass) {
        if (isSimpleType(dtoClass)) {
            return false;
        }
        final BeanDesc dtoDesc = BeanDescFactory.getBeanDesc(dtoClass);
        return dtoDesc.hasField(PROCEDURE_PARAMETERS);
    }

    public String getProcedureParameter(final BeanDesc dtoDesc,
            final Field field) {
        final String name = field.getName() + PROCEDURE_PARAMETER_SUFFIX;
        if (dtoDesc.hasField(name)) {
            final Field f = dtoDesc.getField(name);
            return (String) FieldUtil.get(f, null);
        }
        return null;
    }

    public String getValueType(final BeanDesc dtoDesc, final Field field) {
        final String name = field.getName() + VALUE_TYPE_SUFFIX;
        if (dtoDesc.hasField(name)) {
            final Field f = dtoDesc.getField(name);
            return (String) FieldUtil.get(f, null);
        }
        return null;
    }

    /**
     * 単純なクラスの場合に<code>true</code>を返します。
     * 
     * @param clazz クラス
     * @return 単純なクラスの場合<code>true</code>、そうでない場合<code>false</code>
     */
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
