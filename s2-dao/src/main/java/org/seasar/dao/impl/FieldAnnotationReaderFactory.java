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

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.ArgumentDtoAnnotationReader;
import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.DaoAnnotationReader;
import org.seasar.framework.beans.BeanDesc;

public class FieldAnnotationReaderFactory implements AnnotationReaderFactory {

    public DaoAnnotationReader createDaoAnnotationReader(BeanDesc daoBeanDesc) {
        return new FieldDaoAnnotationReader(daoBeanDesc);
    }

    public BeanAnnotationReader createBeanAnnotationReader(Class beanClass) {
        return new FieldBeanAnnotationReader(beanClass);
    }

    public ArgumentDtoAnnotationReader createArgumentDtoAnnotationReader() {
        return new FieldArgumentDtoAnnotationReader();
    }

}
