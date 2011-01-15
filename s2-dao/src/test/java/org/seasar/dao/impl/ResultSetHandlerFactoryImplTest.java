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

import org.seasar.dao.impl.BeanMetaDataResultSetHandler.RestrictBeanMetaDataResultSetHandler;
import org.seasar.dao.impl.DtoMetaDataResultSetHandler.RestrictDtoMetaDataResultSetHandler;
import org.seasar.dao.impl.MapResultSetHandler.RestrictMapResultSetHandler;
import org.seasar.dao.impl.ObjectResultSetHandler.RestrictObjectResultSetHandler;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;

/**
 * @author azusa
 * 
 */
public class ResultSetHandlerFactoryImplTest extends S2DaoTestCase {

    private ResultSetHandlerFactoryImpl resultSetHandlerFactoryImpl;

    protected void setUp() throws Exception {
        include("j2ee.dicon");
        resultSetHandlerFactoryImpl = new ResultSetHandlerFactoryImpl();
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
    }

    public void testCreateMapResultSetHandler_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl.createMapResultSetHandler() instanceof RestrictMapResultSetHandler);
    }

    public void testCreateMapResultSetHandler() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        assertFalse(resultSetHandlerFactoryImpl.createMapResultSetHandler() instanceof RestrictMapResultSetHandler);
    }

    public void testCreateBeanMetaDataResultSetHandler_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl
                .createBeanMetaDataResultSetHandler(getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class)) instanceof RestrictBeanMetaDataResultSetHandler);
    }

    public void testCreateBeanMetaDataResultSetHandler() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        ResultSetHandler handler = resultSetHandlerFactoryImpl
                .createBeanMetaDataResultSetHandler(getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class));
        assertTrue(handler instanceof BeanMetaDataResultSetHandler);

        assertFalse(handler instanceof RestrictBeanMetaDataResultSetHandler);
    }

    public void testCreateDtoMetaDataResultSet_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl
                .createDtoMetaDataResultSetHandler(getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class)) instanceof RestrictDtoMetaDataResultSetHandler);
    }

    public void testCreateDtoMetaDataResultSet() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        ResultSetHandler handler = resultSetHandlerFactoryImpl
                .createDtoMetaDataResultSetHandler(getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class));
        assertTrue(handler instanceof DtoMetaDataResultSetHandler);

        assertFalse(handler instanceof RestrictDtoMetaDataResultSetHandler);
    }

    public void testCreateObjectMetaDataResultSet_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl
                .createObjectResultSetHandler(null) instanceof RestrictObjectResultSetHandler);
    }

    public void testCreateObjectMetaDataResultSet() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        ResultSetHandler handler = resultSetHandlerFactoryImpl
                .createObjectResultSetHandler(null);
        assertTrue(handler instanceof ObjectResultSetHandler);
        assertFalse(handler instanceof RestrictObjectResultSetHandler);
    }

}
