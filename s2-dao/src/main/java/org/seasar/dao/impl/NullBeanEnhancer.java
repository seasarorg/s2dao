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

import java.util.Collections;
import java.util.Set;

import org.seasar.dao.BeanEnhancer;
import org.seasar.dao.impl.BeanMetaDataImpl.ModifiedPropertySupport;

/**
 * @author taichi
 *
 */
public class NullBeanEnhancer implements BeanEnhancer, ModifiedPropertySupport {

    private static final Set EMPTY_SET = Collections.EMPTY_SET;

    /* (non-Javadoc)
     * @see org.seasar.dao.BeanEnhancer#enhanceBeanClass(java.lang.Class, java.lang.String, java.lang.String)
     */
    public Class enhanceBeanClass(Class beanClass,
            String versionNoPropertyName, String timestampPropertyName) {
        return beanClass;
    }

    /* (non-Javadoc)
     * @see org.seasar.dao.BeanEnhancer#getOriginalClass(java.lang.Class)
     */
    public Class getOriginalClass(Class beanClass) {
        return beanClass;
    }

    /* (non-Javadoc)
     * @see org.seasar.dao.BeanEnhancer#isEnhancedClass(java.lang.Class)
     */
    public boolean isEnhancedClass(Class beanClass) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.seasar.dao.BeanEnhancer#getSupporter()
     */
    public ModifiedPropertySupport getSupporter() {
        return this;
    }

    /* (non-Javadoc)
     * @see org.seasar.dao.impl.BeanMetaDataImpl.ModifiedPropertySupport#getModifiedPropertyNames(java.lang.Object)
     */
    public Set getModifiedPropertyNames(Object bean) {
        return EMPTY_SET;
    }
}
