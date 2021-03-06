/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import org.seasar.dao.ValueTypeFactory;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.framework.container.S2Container;

/**
 * @author manhole
 */
public class ValueTypeFactoryImpl implements ValueTypeFactory {

    public static final String container_BINDING = "bindingType=must";

    private S2Container container;

    public ValueType getValueTypeByName(String name) {
        return (ValueType) container.getComponent(name);
    }

    public ValueType getValueTypeByClass(Class clazz) {
        return ValueTypes.getValueType(clazz);
    }

    public void setContainer(S2Container container) {
        this.container = container.getRoot();
    }

}
