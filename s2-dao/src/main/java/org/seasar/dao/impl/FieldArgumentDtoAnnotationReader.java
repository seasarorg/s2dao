/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

import org.seasar.dao.ArgumentDtoAnnotationReader;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.util.FieldUtil;

/**
 * フィールドアノテーションを読み取る{@link ArgumentDtoAnnotationReader}の実装クラスです。
 * 
 * @author taedium
 */
public class FieldArgumentDtoAnnotationReader implements
        ArgumentDtoAnnotationReader {

    /** <code>PROCEDURE_PARAMETER</code>アノテーションのサフィックス */
    protected String PROCEDURE_PARAMETER_SUFFIX = "_PROCEDURE_PARAMETER";

    /** <code>PROCEDURE_PARAMETER_INDEX</code>アノテーションのサフィックス */
    protected String PROCEDURE_PARAMETER_INDEX_SUFFIX = "_PROCEDURE_PARAMETER_INDEX";

    /** <code>VALUE_TYPE</code>アノテーションのサフィックス */
    protected String VALUE_TYPE_SUFFIX = "_VALUE_TYPE";

    public String getProcedureParameter(final BeanDesc dtoDesc,
            final Field field) {
        final String name = field.getName() + PROCEDURE_PARAMETER_SUFFIX;
        if (dtoDesc.hasField(name)) {
            final Field f = dtoDesc.getField(name);
            return (String) FieldUtil.get(f, null);
        }
        return null;
    }

    public Integer getProcedureParameterIndex(BeanDesc dtoDesc, Field field) {
        final String name = field.getName() + PROCEDURE_PARAMETER_INDEX_SUFFIX;
        if (dtoDesc.hasField(name)) {
            final Field f = dtoDesc.getField(name);
            final int index = FieldUtil.getInt(f, null);
            return new Integer(index);
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

}
