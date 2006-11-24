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

import java.util.HashSet;
import java.util.Set;

import org.seasar.dao.ModifiedProperties;

/**
 * @author manhole
 */
public class ModifiedPropertiesImpl implements ModifiedProperties {

    private final Set propertyNames = new HashSet();

    public void addPropertyName(final String propertyName) {
        propertyNames.add(propertyName);
    }

    public void clear() {
        propertyNames.clear();
    }

    public Set getPropertyNames() {
        return propertyNames;
    }

}
