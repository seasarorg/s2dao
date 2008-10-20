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
package org.seasar.dao.id;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.seasar.dao.Dbms;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.exception.EmptyRuntimeException;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ConstructorUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author higa
 * 
 */
public class IdentifierGeneratorFactory {

    private static Map generatorClasses = new HashMap();

    static {
        addIdentifierGeneratorClass("assigned",
                AssignedIdentifierGenerator.class);
        addIdentifierGeneratorClass("identity",
                IdentityIdentifierGenerator.class);
        addIdentifierGeneratorClass("sequence",
                SequenceIdentifierGenerator.class);
    }

    private IdentifierGeneratorFactory() {
    }

    public static void addIdentifierGeneratorClass(String name, Class clazz) {
        generatorClasses.put(name, clazz);
    }

    public static IdentifierGenerator createIdentifierGenerator(
            PropertyType propertyType, Dbms dbms) {

        return createIdentifierGenerator(propertyType, dbms, null);
    }

    public static IdentifierGenerator createIdentifierGenerator(
            PropertyType propertyType, Dbms dbms, String annotation) {
        if (propertyType == null) {
            throw new EmptyRuntimeException("propertyType");
        }
        if (dbms == null) {
            throw new EmptyRuntimeException("dbms");
        }
        if (annotation == null) {
            return new AssignedIdentifierGenerator(propertyType, dbms);
        }
        String[] array = StringUtil.split(annotation, "=, ");
        Class clazz = getGeneratorClass(array[0]);
        IdentifierGenerator generator = createIdentifierGenerator(clazz,
                propertyType, dbms);
        for (int i = 1; i < array.length; i += 2) {
            setProperty(generator, array[i].trim(), array[i + 1].trim());
        }
        return generator;
    }

    protected static Class getGeneratorClass(String name) {
        Class clazz = (Class) generatorClasses.get(name);
        if (clazz != null) {
            return clazz;
        }
        return ClassUtil.forName(name);
    }

    protected static IdentifierGenerator createIdentifierGenerator(Class clazz,
            PropertyType propertyType, Dbms dbms) {
        Constructor constructor = ClassUtil.getConstructor(clazz, new Class[] {
                PropertyType.class, Dbms.class });
        return (IdentifierGenerator) ConstructorUtil.newInstance(constructor,
                new Object[] { propertyType, dbms });
    }

    protected static void setProperty(IdentifierGenerator generator,
            String propertyName, String value) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(generator.getClass());
        PropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
        pd.setValue(generator, value);
    }
}
