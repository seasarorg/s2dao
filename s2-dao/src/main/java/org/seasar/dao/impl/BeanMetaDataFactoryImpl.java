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

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import javax.sql.DataSource;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.Dbms;
import org.seasar.dao.ModifiedProperties;
import org.seasar.dao.PropertyModifiedSupport;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
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
 * @author jflute
 * @author manhole
 */
public class BeanMetaDataFactoryImpl implements BeanMetaDataFactory {

    private static final String GET_MODIFIED_PROPERTIES = "getModifiedProperties";

    private static final String MODIFIED_PROPERTIES = "modifiedProperties";

    protected AnnotationReaderFactory annotationReaderFactory;

    protected ValueTypeFactory valueTypeFactory;

    protected DataSource dataSource;

    public BeanMetaData createBeanMetaData(final Class beanClass) {
        return createBeanMetaData(beanClass, 0);
    }

    public BeanMetaData createBeanMetaData(final Class beanClass,
            final int relationNestLevel) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        final Connection con = DataSourceUtil.getConnection(dataSource);
        try {
            final DatabaseMetaData metaData = ConnectionUtil.getMetaData(con);
            return createBeanMetaData(metaData, beanClass, relationNestLevel);
        } finally {
            ConnectionUtil.close(con);
        }
    }

    protected BeanMetaData createBeanMetaData(
            final DatabaseMetaData dbMetaData, final Class beanClass,
            final int relationNestLevel) {

        final BeanMetaDataImpl bmd = createBeanMetaDataImpl();
        final boolean isEnhancedClass = isEnhancedClass(beanClass);
        final Class originalBeanClass;
        if (isEnhancedClass) {
            // enhance前のクラスがBEANアノテーションで指定されたクラス
            originalBeanClass = beanClass.getSuperclass();
        } else {
            originalBeanClass = beanClass;
        }
        bmd.setDatabaseMetaData(dbMetaData);
        final Dbms dbms = getDbms();
        bmd.setDbms(dbms);
        final BeanAnnotationReader bar = annotationReaderFactory
                .createBeanAnnotationReader(originalBeanClass);
        bmd.setBeanAnnotationReader(bar);
        bmd.setValueTypeFactory(valueTypeFactory);
        bmd
                .setStopRelationCreation(isLimitRelationNestLevel(relationNestLevel));
        bmd.setBeanMetaDataFactory(this);
        bmd.setRelationNestLevel(relationNestLevel);

        final String versionNoPropertyName = bar.getVersionNoPropertyName();
        if (versionNoPropertyName != null) {
            bmd.setVersionNoPropertyName(versionNoPropertyName);
        }
        final String timestampPropertyName = bar.getTimestampPropertyName();
        if (timestampPropertyName != null) {
            bmd.setTimestampPropertyName(timestampPropertyName);
        }

        bmd.setBeanClass(originalBeanClass);
        bmd.initialize();
        if (!isEnhancedClass) {
            final Class enhancedBeanClass = enhanceBeanClass(beanClass, bmd);
            bmd.setBeanClass(enhancedBeanClass);
        }
        return bmd;
    }

    private boolean isEnhancedClass(final Class targetClass) {
        return StringUtil.contains(ClassUtil.getSimpleClassName(targetClass),
                AspectWeaver.SUFFIX_ENHANCED_CLASS);
    }

    private Class enhanceBeanClass(final Class targetClass,
            final BeanMetaData bmd) {
        final BeanAspectWeaver aspectWeaver = new BeanAspectWeaver(targetClass,
                bmd);
        final Class generateBeanClass = aspectWeaver.generateBeanClass();
        return generateBeanClass;
    }

    protected Dbms getDbms() {
        return DbmsManager.getDbms(dataSource);
    }

    protected BeanMetaDataImpl createBeanMetaDataImpl() {
        return new BeanMetaDataImpl();
    }

    protected boolean isLimitRelationNestLevel(final int relationNestLevel) {
        return relationNestLevel == getLimitRelationNestLevel();
    }

    protected int getLimitRelationNestLevel() {
        // You can change relation creation range by changing this.
        return 1;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setAnnotationReaderFactory(
            final AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    public void setValueTypeFactory(final ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    /**
     * Entityに{@link PropertyModifiedSupport}をimplementsさせるエンハンサ。
     *
     */
    private static class BeanAspectWeaver extends AspectWeaver {

        private static final String modifiedPropertiesfieldName = MODIFIED_PROPERTIES
                + "_";

        private BeanMetaData beanMetaData;

        public BeanAspectWeaver(final Class targetClass, final BeanMetaData bmd) {
            super(targetClass, null);
            this.beanMetaData = bmd;
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
         * {@link PropertyModifiedSupport}をEntityにimplementsさせ、
         * {@link ModifiedProperties}を返却するメソッドを実装する。
         */
        private void combineInterface(final CtClass enhancedCtClass)
                throws NotFoundException, CannotCompileException {
            enhancedCtClass.addInterface(classPool
                    .get(PropertyModifiedSupport.class.getName()));
            final String s = "public " + ModifiedProperties.class.getName()
                    + " " + GET_MODIFIED_PROPERTIES + "() {" + "  return "
                    + modifiedPropertiesfieldName + "; }";
            final CtMethod m = CtNewMethod.make(s, enhancedCtClass);
            enhancedCtClass.addMethod(m);
        }

        /**
         * setterが呼ばれたことを記録するインスタンス変数をEntityに実装する。
         */
        private void combineField(final CtClass enhancedCtClass)
                throws CannotCompileException {
            final String s = "private " + ModifiedProperties.class.getName()
                    + " " + modifiedPropertiesfieldName + " = new "
                    + ModifiedPropertiesImpl.class.getName() + "();";
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
            final String versionNoPropertyName = beanMetaData
                    .getVersionNoPropertyName();
            final String timestampPropertyName = beanMetaData
                    .getTimestampPropertyName();
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
                final String propertyClassName = ClassUtil
                        .getSimpleClassName(pd.getPropertyType());
                final String s = "public void " + setterName + "("
                        + propertyClassName + " " + propertyName + ")"
                        + " { super." + setterName + "(" + propertyName + "); "
                        + modifiedPropertiesfieldName + ".addPropertyName(\""
                        + propertyName + "\"); }";
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

    }

}
