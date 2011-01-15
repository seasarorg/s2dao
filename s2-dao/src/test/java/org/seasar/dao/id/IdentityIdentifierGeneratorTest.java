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
package org.seasar.dao.id;

import org.seasar.dao.dbms.HSQL;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.impl.BasicUpdateHandler;
import org.seasar.extension.jdbc.impl.PropertyTypeImpl;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author higa
 * 
 */
public class IdentityIdentifierGeneratorTest extends S2TestCase {

    /**
     * Constructor for InvocationImplTest.
     * 
     * @param arg0
     */
    public IdentityIdentifierGeneratorTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(IdentityIdentifierGeneratorTest.class);
    }

    protected void setUp() throws Exception {
        include("j2ee.dicon");
    }

    protected void tearDown() throws Exception {
    }

    public void testGetGeneratedValueTx() throws Exception {
        BasicUpdateHandler updateHandler = new BasicUpdateHandler(
                getDataSource(),
                "insert into identitytable(id_name) values('hoge')");
        updateHandler.execute(null);
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(Hoge.class);
        PropertyDesc propertyDesc = beanDesc.getPropertyDesc("id");
        PropertyType propertyType = new PropertyTypeImpl(propertyDesc,
                ValueTypes.getValueType(int.class));
        IdentityIdentifierGenerator generator = new IdentityIdentifierGenerator(
                propertyType, new HSQL());
        Hoge hoge = new Hoge();
        generator.setIdentifier(hoge, getDataSource());
        System.out.println(hoge.getId());
        assertTrue("1", hoge.getId() >= 0);
    }
}