/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
import org.seasar.framework.exception.ClassNotFoundRuntimeException;
import org.seasar.framework.util.ClassUtil;

/**
 * @author manhole
 */
public class AnnotationReaderFactoryImpl implements AnnotationReaderFactory {

    private static final String TIGER_ANNOTATION_READER_FACTORY = "org.seasar.dao.annotation.tiger.impl.AnnotationReaderFactoryImpl";

    private static final String BACKPORT175_ANNOTATION_READER_FACTORY = "org.seasar.dao.annotation.backport175.impl.AnnotationReaderFactoryImpl";

    private AnnotationReaderFactory annotationReaderFactory;

    public AnnotationReaderFactoryImpl() {
        Class clazz = FieldAnnotationReaderFactory.class;
        try {
            clazz = ClassUtil.forName(TIGER_ANNOTATION_READER_FACTORY);
        } catch (ClassNotFoundRuntimeException ignore1) {
            try {
                clazz = ClassUtil
                        .forName(BACKPORT175_ANNOTATION_READER_FACTORY);
            } catch (ClassNotFoundRuntimeException ignore2) {
            }
        }
        annotationReaderFactory = (AnnotationReaderFactory) ClassUtil
                .newInstance(clazz);
    }

    public BeanAnnotationReader createBeanAnnotationReader(Class beanClass) {
        return annotationReaderFactory.createBeanAnnotationReader(beanClass);
    }

    public DaoAnnotationReader createDaoAnnotationReader(BeanDesc daoBeanDesc) {
        return annotationReaderFactory.createDaoAnnotationReader(daoBeanDesc);
    }

    public ArgumentDtoAnnotationReader createArgumentDtoAnnotationReader() {
        return annotationReaderFactory.createArgumentDtoAnnotationReader();
    }

}
