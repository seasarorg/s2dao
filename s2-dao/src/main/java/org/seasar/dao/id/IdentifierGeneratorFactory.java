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
package org.seasar.dao.id;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.seasar.dao.Dbms;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ConstructorUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author higa
 * 
 */
public class IdentifierGeneratorFactory {

    private static Map generatorClasses_ = new HashMap();

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
        generatorClasses_.put(name, clazz);
    }

    public static IdentifierGenerator createIdentifierGenerator(
            String propertyName, Dbms dbms) {

        return createIdentifierGenerator(propertyName, dbms, null);
    }

    public static IdentifierGenerator createIdentifierGenerator(
            String propertyName, Dbms dbms, String annotation) {

        if (annotation == null) {
            return new AssignedIdentifierGenerator(propertyName, dbms);
        }
        String[] array = StringUtil.split(annotation, "=, ");
        Class clazz = getGeneratorClass(array[0]);
        IdentifierGenerator generator = createIdentifierGenerator(clazz,
                propertyName, dbms);
        for (int i = 1; i < array.length; i += 2) {
            setProperty(generator, array[i].trim(), array[i + 1].trim());
        }
        return generator;
    }

    protected static Class getGeneratorClass(String name) {
        Class clazz = (Class) generatorClasses_.get(name);
        if (clazz != null) {
            return clazz;
        }
        return ClassUtil.forName(name);
    }

    protected static IdentifierGenerator createIdentifierGenerator(Class clazz,
            String propertyName, Dbms dbms) {
        Constructor constructor = ClassUtil.getConstructor(clazz, new Class[] {
                String.class, Dbms.class });
        return (IdentifierGenerator) ConstructorUtil.newInstance(constructor,
                new Object[] { propertyName, dbms });
    }

    protected static void setProperty(IdentifierGenerator generator,
            String propertyName, String value) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(generator.getClass());
        PropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
        pd.setValue(generator, value);
    }
}
