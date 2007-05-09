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

import junit.framework.TestCase;

import org.seasar.dao.DtoMetaData;
import org.seasar.framework.util.DisposableUtil;

/**
 * @author higa
 *
 */
public class DtoMetaDataFactoryImplTest extends TestCase {

    private DtoMetaDataFactoryImpl factory;

    protected void setUp() throws Exception {
        factory = new DtoMetaDataFactoryImpl();
        factory.setBeanAnnotationReader(new FieldBeanAnnotationReader(
                EmployeeDto.class));
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
    }

    protected void tearDown() throws Exception {
        DisposableUtil.dispose();
    }

    /**
     * Test method for {@link org.seasar.dao.impl.DtoMetaDataFactoryImpl#getDtoMetaData(java.lang.Class)}.
     */
    public void testGetDtoMetaData() {
        DtoMetaData dmd = factory.getDtoMetaData(EmployeeDto.class);
        assertNotNull(dmd);
        assertSame(dmd, factory.getDtoMetaData(EmployeeDto.class));
    }
}