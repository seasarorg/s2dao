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
package org.seasar.dao.impl;

import java.lang.reflect.Field;

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;

public class FieldBeanAnnotationReader implements BeanAnnotationReader {

    public String TABLE = "TABLE";

    public String RELNO_SUFFIX = "_RELNO";

    public String RELKEYS_SUFFIX = "_RELKEYS";

    public String ID_SUFFIX = "_ID";

    public String NO_PERSISTENT_PROPS = "NO_PERSISTENT_PROPS";

    public String VERSION_NO_PROPERTY = "VERSION_NO_PROPERTY";

    public String TIMESTAMP_PROPERTY = "TIMESTAMP_PROPERTY";

    public String COLUMN_SUFFIX = "_COLUMN";

    public String VALUE_TYPE_SUFFIX = "_VALUE_TYPE";

    private BeanDesc beanDesc_;

    public FieldBeanAnnotationReader(Class beanClass_) {
        beanDesc_ = BeanDescFactory.getBeanDesc(beanClass_);
    }

    public String getColumnAnnotation(PropertyDesc pd) {
        String propertyName = pd.getPropertyName();
        String columnNameKey = propertyName + COLUMN_SUFFIX;
        String columnName = propertyName;
        if (beanDesc_.hasField(columnNameKey)) {
            Field field = beanDesc_.getField(columnNameKey);
            columnName = (String) FieldUtil.get(field, null);
        }
        return columnName;
    }

    public String getTableAnnotation() {
        if (beanDesc_.hasField(TABLE)) {
            Field field = beanDesc_.getField(TABLE);
            return (String) FieldUtil.get(field, null);
        }
        return null;
    }

    public String getVersionNoProteryNameAnnotation() {
        if (beanDesc_.hasField(VERSION_NO_PROPERTY)) {
            Field field = beanDesc_.getField(VERSION_NO_PROPERTY);
            return (String) FieldUtil.get(field, null);
        }
        return null;
    }

    public String getTimestampPropertyName() {
        if (beanDesc_.hasField(TIMESTAMP_PROPERTY)) {
            Field field = beanDesc_.getField(TIMESTAMP_PROPERTY);
            return (String) FieldUtil.get(field, null);
        }
        return null;
    }

    public String getId(PropertyDesc pd) {
        String idKey = pd.getPropertyName() + ID_SUFFIX;
        if (beanDesc_.hasField(idKey)) {
            Field field = beanDesc_.getField(idKey);
            return (String) FieldUtil.get(field, null);
        }
        return null;
    }

    public String[] getNoPersisteneProps() {
        if (beanDesc_.hasField(NO_PERSISTENT_PROPS)) {
            Field field = beanDesc_.getField(NO_PERSISTENT_PROPS);
            String str = (String) FieldUtil.get(field, null);
            return StringUtil.split(str, ", ");
        }
        return null;
    }

    public String getRelationKey(PropertyDesc pd) {
        String propertyName = pd.getPropertyName();
        String relkeysKey = propertyName + RELKEYS_SUFFIX;
        if (beanDesc_.hasField(relkeysKey)) {
            Field field = beanDesc_.getField(relkeysKey);
            return (String) FieldUtil.get(field, null);
        }
        return null;
    }

    public int getRelationNo(PropertyDesc pd) {
        String relnoKey = pd.getPropertyName() + RELNO_SUFFIX;
        Field field = beanDesc_.getField(relnoKey);
        return FieldUtil.getInt(field, null);
    }

    public boolean hasRelationNo(PropertyDesc pd) {
        String relnoKey = pd.getPropertyName() + RELNO_SUFFIX;
        return beanDesc_.hasField(relnoKey);
    }

    public Class getValueType(PropertyDesc pd) {
        String valueTypeKey = pd.getPropertyName() + VALUE_TYPE_SUFFIX;
        if (beanDesc_.hasField(valueTypeKey)) {
            Field field = beanDesc_.getField(valueTypeKey);
            return (Class) FieldUtil.get(field, null);
        }
        return null;
    }

}
