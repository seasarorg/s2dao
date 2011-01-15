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

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.seasar.dao.BeanEnhancer;
import org.seasar.dao.DaoNamingConvention;
import org.seasar.dao.NotFoundModifiedPropertiesRuntimeException;
import org.seasar.dao.impl.BeanMetaDataImpl.ModifiedPropertySupport;
import org.seasar.framework.aop.javassist.AspectWeaver;
import org.seasar.framework.aop.javassist.EnhancedClassGenerator;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.exception.CannotCompileRuntimeException;
import org.seasar.framework.exception.NoSuchFieldRuntimeException;
import org.seasar.framework.exception.NotFoundRuntimeException;
import org.seasar.framework.util.ClassLoaderUtil;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author manhole
 */
public class BeanEnhancerImpl implements BeanEnhancer, ModifiedPropertySupport {

    public static final String daoNamingConvention_BINDING = "bindingType=must";

    private DaoNamingConvention daoNamingConvention;

    public Class enhanceBeanClass(final Class beanClass,
            final String versionNoPropertyName,
            final String timestampPropertyName) {
        if (isEnhancedClass(beanClass)) {
            return beanClass;
        }
        if (!shouldEnhance(beanClass)) {
            return beanClass;
        }
        final BeanAspectWeaver aspectWeaver = new BeanAspectWeaver(beanClass);
        aspectWeaver.setVersionNoPropertyName(versionNoPropertyName);
        aspectWeaver.setTimestampPropertyName(timestampPropertyName);
        aspectWeaver
                .setModifiedPropertyNamesPropertyName(getDaoNamingConvention()
                        .getModifiedPropertyNamesPropertyName());
        final Class generatedBeanClass = aspectWeaver.generateBeanClass();
        return generatedBeanClass;
    }

    private boolean shouldEnhance(final Class beanClass) {
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(beanClass);
        if (!beanDesc.hasPropertyDesc(getDaoNamingConvention()
                .getModifiedPropertyNamesPropertyName())) {
            return true;
        }
        return false;
    }

    /*
     * entityをエンハンスするのはここだけなので、クラス名を見るだけで充分。
     */
    public boolean isEnhancedClass(final Class beanClass) {
        final String simpleClassName = ClassUtil.getSimpleClassName(beanClass);
        return StringUtil.contains(simpleClassName,
                AspectWeaver.SUFFIX_ENHANCED_CLASS);
    }

    public Class getOriginalClass(final Class beanClass) {
        if (isEnhancedClass(beanClass)) {
            // enhance前のクラスがBEANアノテーションで指定されたクラス
            return beanClass.getSuperclass();
        } else {
            return beanClass;
        }
    }

    public DaoNamingConvention getDaoNamingConvention() {
        return daoNamingConvention;
    }

    public void setDaoNamingConvention(
            final DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
    }

    /**
     * setterが呼ばれたプロパティ名を記録するようBeanを拡張するエンハンサ。
     */
    private static class BeanAspectWeaver extends AspectWeaver {

        private String versionNoPropertyName;

        private String timestampPropertyName;

        private String modifiedPropertiesFieldName;

        private String modifiedPropertiesReadMethodName;

        public BeanAspectWeaver(final Class targetClass) {
            super(targetClass, null);
        }

        public Class generateBeanClass() {
            try {
                final CtClass enhancedCtClass = getEnhancedCtClass();
                combineField(enhancedCtClass);
                combineInterface(enhancedCtClass);
                combineProperties(enhancedCtClass);
                final Class beanClass = enhancedClassGenerator
                        .toClass(ClassLoaderUtil.getClassLoader(targetClass));
                return beanClass;
            } catch (final CannotCompileException e) {
                throw new CannotCompileRuntimeException(e);
            } catch (final NotFoundException e) {
                throw new NotFoundRuntimeException(e);
            }
        }

        /**
         * {@link Set}を返却するメソッドを実装する。
         */
        private void combineInterface(final CtClass enhancedCtClass)
                throws NotFoundException, CannotCompileException {
            final String s = "public " + Set.class.getName() + " "
                    + modifiedPropertiesReadMethodName + "() {" + "  return "
                    + modifiedPropertiesFieldName + "; }";
            final CtMethod m = CtNewMethod.make(s, enhancedCtClass);
            enhancedCtClass.addMethod(m);
        }

        /**
         * setterが呼ばれたことを記録するインスタンス変数をEntityに実装する。
         */
        private void combineField(final CtClass enhancedCtClass)
                throws CannotCompileException {
            final String s = "private " + Set.class.getName() + " "
                    + modifiedPropertiesFieldName + " = new "
                    + HashSet.class.getName() + "();";
            final CtField modifiedPropertiesField = CtField.make(s,
                    enhancedCtClass);
            enhancedCtClass.addField(modifiedPropertiesField);
        }

        /**
         * setterを拡張し、
         * (1)スーパークラスの同メソッドを呼び、
         * (2)modifiedPropertiesフィールドへsetterが呼ばれたことを記録します。
         */
        private void combineProperties(final CtClass enhancedCtClass)
                throws CannotCompileException {
            final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(targetClass);
            final int propertyDescSize = beanDesc.getPropertyDescSize();
            for (int i = 0; i < propertyDescSize; i++) {
                final PropertyDesc pd = beanDesc.getPropertyDesc(i);
                if (!pd.hasWriteMethod() || !pd.hasReadMethod()) {
                    continue;
                }
                final String propertyName = pd.getPropertyName();
                if (propertyName.equalsIgnoreCase(versionNoPropertyName)) {
                    continue;
                }
                if (propertyName.equalsIgnoreCase(timestampPropertyName)) {
                    continue;
                }

                final String setterName = pd.getWriteMethod().getName();
                final String getterName = pd.getReadMethod().getName();
                final String propertyClassName = ClassUtil
                        .getSimpleClassName(pd.getPropertyType());
                final String oldName = propertyName + "Old";
                final String s = "public void " + setterName + "("
                        + propertyClassName + " " + propertyName + ")"
                        + " { final " + propertyClassName + " " + oldName
                        + " = super." + getterName + "(); " + "super."
                        + setterName + "(" + propertyName + "); " + "if (!"
                        + EqualsUtil.class.getName() + ".equals(" + oldName
                        + ", " + propertyName + ")) "
                        + modifiedPropertiesFieldName + ".add(\""
                        + propertyName + "\"); }";
                //System.out.println(s);
                final CtMethod m = CtNewMethod.make(s, enhancedCtClass);
                enhancedCtClass.addMethod(m);
            }
        }

        private CtClass getEnhancedCtClass() {
            final String enhancedClassFieldName = "enhancedClass";
            try {
                final Field field = EnhancedClassGenerator.class
                        .getDeclaredField(enhancedClassFieldName);
                field.setAccessible(true);
                final CtClass enhancedCtClass = (CtClass) FieldUtil.get(field,
                        enhancedClassGenerator);
                return enhancedCtClass;
            } catch (final NoSuchFieldException e) {
                throw new NoSuchFieldRuntimeException(
                        EnhancedClassGenerator.class, enhancedClassFieldName, e);
            }
        }

        public void setModifiedPropertyNamesPropertyName(
                final String modifiedPropertiesName) {
            modifiedPropertiesFieldName = modifiedPropertiesName + "_";
            modifiedPropertiesReadMethodName = "get"
                    + StringUtil.capitalize(modifiedPropertiesName);
        }

        public void setVersionNoPropertyName(final String versionNoPropertyName) {
            this.versionNoPropertyName = versionNoPropertyName;
        }

        public void setTimestampPropertyName(final String timestampPropertyName) {
            this.timestampPropertyName = timestampPropertyName;
        }

    }

    public static class EqualsUtil {

        public static boolean equals(final long o1, final long o2) {
            return o1 == o2;
        }

        public static boolean equals(final int o1, final int o2) {
            return o1 == o2;
        }

        public static boolean equals(final short o1, final short o2) {
            return o1 == o2;
        }

        public static boolean equals(final char o1, final char o2) {
            return o1 == o2;
        }

        public static boolean equals(final boolean o1, final boolean o2) {
            return o1 == o2;
        }

        public static boolean equals(final double o1, final double o2) {
            return o1 == o2;
        }

        public static boolean equals(final float o1, final float o2) {
            return o1 == o2;
        }

        public static boolean equals(final Object o1, final Object o2) {
            if (o1 == o2) {
                return true;
            }
            if (o1 == null || o2 == null) {
                return false;
            }
            return o1.equals(o2);
        }

    }

    /* (non-Javadoc)
     * @see org.seasar.dao.BeanEnhancer#getSupporter()
     */
    public ModifiedPropertySupport getSupporter() {
        return this;
    }

    public Set getModifiedPropertyNames(final Object bean) {
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
        final String propertyName = getDaoNamingConvention()
                .getModifiedPropertyNamesPropertyName();
        if (!beanDesc.hasPropertyDesc(propertyName)) {
            throw new NotFoundModifiedPropertiesRuntimeException(bean
                    .getClass().getName(), propertyName);
        }
        final PropertyDesc propertyDesc = beanDesc
                .getPropertyDesc(propertyName);
        final Object value = propertyDesc.getValue(bean);
        final Set names = (Set) value;
        return names;
    }

}
