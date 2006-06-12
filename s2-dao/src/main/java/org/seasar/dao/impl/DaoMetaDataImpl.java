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

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.DaoAnnotationReader;
import org.seasar.dao.DaoMetaData;
import org.seasar.dao.DaoNotFoundRuntimeException;
import org.seasar.dao.DaoResultSetHandlerFactory;
import org.seasar.dao.Dbms;
import org.seasar.dao.DtoMetaData;
import org.seasar.dao.IllegalSignatureRuntimeException;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.dao.handler.ProcedureHandlerImpl;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.ObjectResultSetHandler;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.MethodNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.exception.NoSuchMethodRuntimeException;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.InputStreamReaderUtil;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.ReaderUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author higa
 * 
 */
public class DaoMetaDataImpl implements DaoMetaData {

    private static final Pattern startWithOrderByPattern = Pattern.compile(
            "(/\\*[^*]+\\*/)*order by", Pattern.CASE_INSENSITIVE);

    private static final Pattern startWithSelectPattern = Pattern.compile(
            "^\\s*select\\s", Pattern.CASE_INSENSITIVE);

    private static final Pattern startWithBeginCommentPattern = Pattern
            .compile("/\\*BEGIN\\*/\\s*WHERE .+", Pattern.CASE_INSENSITIVE);

    private static final String NOT_SINGLE_ROW_UPDATED = "NotSingleRowUpdated";

    protected Class daoClass_;

    protected Class daoInterface_;

    protected BeanDesc daoBeanDesc_;

    protected DataSource dataSource_;

    protected DaoAnnotationReader annotationReader_;

    protected StatementFactory statementFactory_;

    protected ResultSetFactory resultSetFactory_;

    protected AnnotationReaderFactory annotationReaderFactory_;

    protected String encoding = "JISAutoDetect";

    protected Dbms dbms_;

    protected Class beanClass_;

    protected BeanMetaData beanMetaData_;

    protected Map sqlCommands_ = new HashMap();

    private ValueTypeFactory valueTypeFactory;

    protected String[] daoSuffixes_ = new String[] { "Dao" };

    protected String[] insertPrefixes_ = new String[] { "insert", "create",
            "add" };

    protected String[] updatePrefixes_ = new String[] { "update", "modify",
            "store" };

    protected String[] deletePrefixes_ = new String[] { "delete", "remove" };

    private DaoResultSetHandlerFactory resultSetHandlerFactory;

    public DaoMetaDataImpl() {
    }

    /**
     * @deprecated
     */
    public DaoMetaDataImpl(Class daoClass, DataSource dataSource,
            StatementFactory statementFactory, ResultSetFactory resultSetFactory) {
        this(daoClass, dataSource, statementFactory, resultSetFactory,
                new FieldAnnotationReaderFactory(), null, null, null, null,
                null);
    }

    /**
     * @deprecated
     */
    public DaoMetaDataImpl(Class daoClass, DataSource dataSource,
            StatementFactory statementFactory,
            ResultSetFactory resultSetFactory,
            AnnotationReaderFactory annotationReaderFactory) {
        this(daoClass, dataSource, statementFactory, resultSetFactory,
                annotationReaderFactory, null, null, null, null, null);
    }

    /**
     * @deprecated
     */
    public DaoMetaDataImpl(Class daoClass, DataSource dataSource,
            StatementFactory statementFactory,
            ResultSetFactory resultSetFactory,
            AnnotationReaderFactory annotationReaderFactory, String encoding,
            String[] daoSuffixes, String[] insertPrefixes,
            String[] updatePrefixes, String[] deletePrefixes) {
        setDaoClass(daoClass);
        setDataSource(dataSource);
        setStatementFactory(statementFactory);
        setResultSetFactory(resultSetFactory);
        setAnnotationReaderFactory(annotationReaderFactory);
        setValueTypeFactory(new ValueTypeFactoryImpl());
        if (encoding != null) {
            setSqlFileEncoding(encoding);
        }
        if (daoSuffixes != null) {
            setDaoSuffixes(daoSuffixes);
        }
        if (insertPrefixes != null) {
            setInsertPrefixes(insertPrefixes);
        }
        if (updatePrefixes != null) {
            setUpdatePrefixes(updatePrefixes);
        }
        if (deletePrefixes != null) {
            setDeletePrefixes(deletePrefixes);
        }
        initialize();
    }

    public void initialize() {
        Class daoClass = getDaoClass();
        daoInterface_ = getDaoInterface(daoClass);
        daoBeanDesc_ = BeanDescFactory.getBeanDesc(daoClass);
        annotationReader_ = getAnnotationReaderFactory()
                .createDaoAnnotationReader(daoBeanDesc_);
        setBeanClass(annotationReader_.getBeanClass());
        Connection con = DataSourceUtil.getConnection(dataSource_);
        try {
            DatabaseMetaData dbMetaData = ConnectionUtil.getMetaData(con);
            dbms_ = DbmsManager.getDbms(dbMetaData);
            BeanMetaDataImpl beanMetaDataImpl = new BeanMetaDataImpl();
            beanMetaDataImpl.setBeanClass(getBeanClass());
            beanMetaDataImpl.setDatabaseMetaData(dbMetaData);
            beanMetaDataImpl.setDbms(dbms_);
            beanMetaDataImpl
                    .setAnnotationReaderFactory(getAnnotationReaderFactory());
            beanMetaDataImpl.setValueTypeFactory(getValueTypeFactory());
            beanMetaDataImpl.initialize();
            beanMetaData_ = beanMetaDataImpl;
        } finally {
            ConnectionUtil.close(con);
        }
        resultSetHandlerFactory = new DaoResultSetHandlerFactoryImpl(
                beanMetaData_);
        setupSqlCommand();
    }

    protected void setupSqlCommand() {
        BeanDesc idbd = BeanDescFactory.getBeanDesc(daoInterface_);
        String[] names = idbd.getMethodNames();
        for (int i = 0; i < names.length; ++i) {
            Method[] methods = daoBeanDesc_.getMethods(names[i]);
            if (methods.length == 1 && MethodUtil.isAbstract(methods[0])) {
                setupMethod(methods[0]);
            }
        }
    }

    protected void setupMethod(Method method) {
        setupMethod(daoInterface_, method);
    }

    protected void setupMethod(Class daoInterface, Method method) {
        setupMethodByAnnotation(daoInterface, method);

        if (!completedSetupMethod(method)) {
            setupMethodBySqlFile(daoInterface, method);
        }

        if (!completedSetupMethod(method)) {
            setupMethodByInterfaces(daoInterface, method);
        }

        if (!completedSetupMethod(method)) {
            setupMethodBySuperClass(daoInterface, method);
        }

        if (!completedSetupMethod(method)) {
            setupMethodByAuto(method);
        }
    }

    protected void setupMethodByAnnotation(Class daoInterface, Method method) {
        String sql = annotationReader_.getSQL(method, dbms_.getSuffix());
        if (sql != null) {
            setupMethodByManual(method, sql);
        }
        String procedureName = annotationReader_.getStoredProcedureName(method);
        if (procedureName != null) {
            setupProcedure(method, procedureName);
        }
    }

    protected void setupProcedure(final Method method,
            final String procedureName) {

        ProcedureHandlerImpl handler = new ProcedureHandlerImpl();
        handler.setDataSource(dataSource_);
        handler.setMethod(method);
        handler.setProcedureName(procedureName);
        handler.setResultSetHandlerFactory(resultSetHandlerFactory);
        handler.setStatementFactory(statementFactory_);
        handler.initialize();

        final StaticStoredProcedureCommand command = new StaticStoredProcedureCommand(
                handler);
        sqlCommands_.put(method.getName(), command);
    }

    protected String readText(String path) {
        InputStream is = ResourceUtil.getResourceAsStream(path);
        Reader reader = InputStreamReaderUtil.create(is, getSqlFileEncoding());
        return ReaderUtil.readText(reader);
    }

    protected void setupMethodBySqlFile(Class daoInterface, Method method) {
        String base = daoInterface.getName().replace('.', '/') + "_"
                + method.getName();
        String dbmsPath = base + dbms_.getSuffix() + ".sql";
        String standardPath = base + ".sql";
        if (ResourceUtil.isExist(dbmsPath)) {
            String sql = readText(dbmsPath);
            setupMethodByManual(method, sql);
        } else if (ResourceUtil.isExist(standardPath)) {
            String sql = readText(standardPath);
            setupMethodByManual(method, sql);
        }
    }

    protected void setupMethodByInterfaces(Class daoInterface, Method method) {
        Class[] interfaces = daoInterface.getInterfaces();
        if (interfaces == null) {
            return;
        }
        for (int i = 0; i < interfaces.length; i++) {
            Method interfaceMethod = getSameSignatureMethod(interfaces[i],
                    method);
            if (interfaceMethod != null) {
                setupMethod(interfaces[i], interfaceMethod);
            }
        }
    }

    protected void setupMethodBySuperClass(Class daoInterface, Method method) {
        Class superDaoClass = daoInterface.getSuperclass();
        if (superDaoClass != null && !Object.class.equals(superDaoClass)) {
            Method superClassMethod = getSameSignatureMethod(superDaoClass,
                    method);
            if (superClassMethod != null) {
                setupMethod(superDaoClass, method);
            }
        }
    }

    protected boolean completedSetupMethod(Method method) {
        return sqlCommands_.get(method.getName()) != null;
    }

    private Method getSameSignatureMethod(Class clazz, Method method) {
        try {
            String methodName = method.getName();
            Class[] parameterTypes = method.getParameterTypes();
            return ClassUtil.getMethod(clazz, methodName, parameterTypes);
        } catch (NoSuchMethodRuntimeException e) {
            return null;
        }
    }

    protected void setupMethodByManual(Method method, String sql) {
        if (isSelect(method)) {
            setupSelectMethodByManual(method, sql);
        } else {
            setupUpdateMethodByManual(method, sql);
        }
    }

    protected void setupMethodByAuto(Method method) {
        if (isInsert(method.getName())) {
            setupInsertMethodByAuto(method);
        } else if (isUpdate(method.getName())) {
            setupUpdateMethodByAuto(method);
        } else if (isDelete(method.getName())) {
            setupDeleteMethodByAuto(method);
        } else {
            setupSelectMethodByAuto(method);
        }
    }

    protected void setupSelectMethodByManual(Method method, String sql) {
        SelectDynamicCommand cmd = createSelectDynamicCommand(createResultSetHandler(method));
        cmd.setSql(sql);
        cmd.setArgNames(annotationReader_.getArgNames(method));
        cmd.setArgTypes(method.getParameterTypes());
        sqlCommands_.put(method.getName(), cmd);
    }

    protected SelectDynamicCommand createSelectDynamicCommand(
            ResultSetHandler rsh) {
        return new SelectDynamicCommand(dataSource_, statementFactory_, rsh,
                resultSetFactory_);
    }

    protected SelectDynamicCommand createSelectDynamicCommand(
            ResultSetHandler resultSetHandler, String query) {

        SelectDynamicCommand cmd = createSelectDynamicCommand(resultSetHandler);
        StringBuffer buf = new StringBuffer(255);
        if (startsWithSelect(query)) {
            buf.append(query);
        } else {
            String sql = dbms_.getAutoSelectSql(getBeanMetaData());
            buf.append(sql);
            if (query != null) {
                if (startsWithOrderBy(query)) {
                    buf.append(" ");
                } else if (startsWithBeginComment(query)) {
                    buf.append(" ");
                } else if (sql.lastIndexOf("WHERE") < 0) {
                    buf.append(" WHERE ");
                } else {
                    buf.append(" AND ");
                }
                buf.append(query);
            }
        }
        cmd.setSql(buf.toString());
        return cmd;
    }

    protected boolean startsWithBeginComment(String query) {
        Matcher m = startWithBeginCommentPattern.matcher(query);
        if (m.lookingAt()) {
            return true;
        }
        return false;
    }

    protected static boolean startsWithSelect(String query) {
        if (query != null) {
            Matcher m = startWithSelectPattern.matcher(query);
            if (m.lookingAt()) {
                return true;
            }
        }
        return false;
    }

    protected static boolean startsWithOrderBy(String query) {
        if (query != null) {
            Matcher m = startWithOrderByPattern.matcher(query);
            if (m.lookingAt()) {
                return true;
            }
        }
        return false;
    }

    protected ResultSetHandler createResultSetHandler(Method method) {
        return resultSetHandlerFactory.createResultSetHandler(method);
    }

    protected boolean isBeanClassAssignable(Class clazz) {
        return beanClass_.isAssignableFrom(clazz)
                || clazz.isAssignableFrom(beanClass_);
    }

    // update & insert & delete
    protected void setupUpdateMethodByManual(Method method, String sql) {
        UpdateDynamicCommand cmd = new UpdateDynamicCommand(dataSource_,
                statementFactory_);
        cmd.setSql(sql);
        String[] argNames = annotationReader_.getArgNames(method);
        if (argNames.length == 0 && isUpdateSignatureForBean(method)) {
            argNames = new String[] { StringUtil.decapitalize(ClassUtil
                    .getShortClassName(beanClass_)) };
        }
        cmd.setArgNames(argNames);
        cmd.setArgTypes(method.getParameterTypes());
        cmd
                .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
        sqlCommands_.put(method.getName(), cmd);
    }

    protected boolean isUpdateSignatureForBean(Method method) {
        return method.getParameterTypes().length == 1
                && isBeanClassAssignable(method.getParameterTypes()[0]);
    }

    protected Class getNotSingleRowUpdatedExceptionClass(Method method) {
        Class[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes != null) {
            for (int i = 0; i < exceptionTypes.length; ++i) {
                Class exceptionType = exceptionTypes[i];
                if (exceptionType.getName().indexOf(NOT_SINGLE_ROW_UPDATED) >= 0) {
                    return exceptionType;
                }
            }
        }
        return null;
    }

    protected void setupInsertMethodByAuto(Method method) {
        checkAutoUpdateMethod(method);
        String[] propertyNames = getPersistentPropertyNames(method);
        InsertAutoDynamicCommand cmd = null;
        if (isUpdateSignatureForBean(method)) {
            cmd = new InsertAutoDynamicCommand();
        } else {
            cmd = new InsertBatchAutoDynamicCommand();
        }
        cmd.setBeanMetaData(getBeanMetaData());
        cmd.setDataSource(dataSource_);
        cmd
                .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
        cmd.setPropertyNames(propertyNames);
        cmd.setStatementFactory(statementFactory_);
        sqlCommands_.put(method.getName(), cmd);
    }

    // update
    protected void setupUpdateMethodByAuto(Method method) {
        checkAutoUpdateMethod(method);
        String[] propertyNames = getPersistentPropertyNames(method);
        AbstractSqlCommand cmd = null;
        if (isUpdateSignatureForBean(method)) {
            cmd = new UpdateAutoStaticCommand(dataSource_, statementFactory_,
                    beanMetaData_, propertyNames);
        } else {
            cmd = new UpdateBatchAutoStaticCommand(dataSource_,
                    statementFactory_, beanMetaData_, propertyNames);
        }
        sqlCommands_.put(method.getName(), cmd);
    }

    protected void setupDeleteMethodByAuto(Method method) {
        checkAutoUpdateMethod(method);
        String[] propertyNames = getPersistentPropertyNames(method);
        SqlCommand cmd = null;
        if (isUpdateSignatureForBean(method)) {
            cmd = new DeleteAutoStaticCommand(dataSource_, statementFactory_,
                    beanMetaData_, propertyNames);
        } else {
            cmd = new DeleteBatchAutoStaticCommand(dataSource_,
                    statementFactory_, beanMetaData_, propertyNames);
        }
        sqlCommands_.put(method.getName(), cmd);
    }

    protected String[] getPersistentPropertyNames(Method method) {
        List names = new ArrayList();
        String[] props = annotationReader_.getNoPersistentProps(method);
        if (props != null) {
            for (int i = 0; i < beanMetaData_.getPropertyTypeSize(); ++i) {
                PropertyType pt = beanMetaData_.getPropertyType(i);
                if (pt.isPersistent()
                        && !isPropertyExist(props, pt.getPropertyName())) {
                    names.add(pt.getPropertyName());
                }
            }
        } else {
            props = annotationReader_.getPersistentProps(method);
            if (props != null) {
                names.addAll(Arrays.asList(props));
                for (int i = 0; i < beanMetaData_.getPrimaryKeySize(); ++i) {
                    String pk = beanMetaData_.getPrimaryKey(i);
                    PropertyType pt = beanMetaData_
                            .getPropertyTypeByColumnName(pk);
                    names.add(pt.getPropertyName());
                }
                if (beanMetaData_.hasVersionNoPropertyType()) {
                    names.add(beanMetaData_.getVersionNoPropertyName());
                }
                if (beanMetaData_.hasTimestampPropertyType()) {
                    names.add(beanMetaData_.getTimestampPropertyName());
                }
            }
        }
        if (names.size() == 0) {
            for (int i = 0; i < beanMetaData_.getPropertyTypeSize(); ++i) {
                PropertyType pt = beanMetaData_.getPropertyType(i);
                if (pt.isPersistent()) {
                    names.add(pt.getPropertyName());
                }
            }
        }
        return (String[]) names.toArray(new String[names.size()]);
    }

    protected boolean isPropertyExist(String[] props, String propertyName) {
        for (int i = 0; i < props.length; ++i) {
            if (props[i].equalsIgnoreCase(propertyName)) {
                return true;
            }
        }
        return false;
    }

    protected void setupSelectMethodByAuto(Method method) {
        String query = annotationReader_.getQuery(method);
        ResultSetHandler handler = createResultSetHandler(method);
        SelectDynamicCommand cmd = null;
        String[] argNames = annotationReader_.getArgNames(method);
        if (query != null && !startsWithOrderBy(query)) {
            cmd = createSelectDynamicCommand(handler, query);
        } else {
            cmd = createSelectDynamicCommand(handler);
            String sql = null;
            if (argNames.length == 0 && method.getParameterTypes().length == 1) {
                argNames = new String[] { "dto" };
                sql = createAutoSelectSqlByDto(method.getParameterTypes()[0]);
            } else {
                sql = createAutoSelectSql(argNames);
            }
            if (query != null) {
                sql = sql + " " + query;
            }
            cmd.setSql(sql);
        }
        cmd.setArgNames(argNames);
        cmd.setArgTypes(method.getParameterTypes());
        sqlCommands_.put(method.getName(), cmd);
    }

    protected String createAutoSelectSqlByDto(Class dtoClass) {
        String sql = dbms_.getAutoSelectSql(getBeanMetaData());
        StringBuffer buf = new StringBuffer(sql);
        // TODO どうするか要検討
        if (dtoClass.isPrimitive()) {
            return sql;
        }
        DtoMetaData dmd = createDtoMetaData(dtoClass);
        boolean began = false;
        if (!(sql.lastIndexOf("WHERE") > 0)) {
            buf.append("/*BEGIN*/ WHERE ");
            began = true;
        }
        for (int i = 0; i < dmd.getPropertyTypeSize(); ++i) {
            PropertyType pt = dmd.getPropertyType(i);
            String aliasName = pt.getColumnName();
            if (!beanMetaData_.hasPropertyTypeByAliasName(aliasName)) {
                continue;
            }
            if (!beanMetaData_.getPropertyTypeByAliasName(aliasName)
                    .isPersistent()) {
                continue;
            }
            String columnName = beanMetaData_.convertFullColumnName(aliasName);
            String propertyName = "dto." + pt.getPropertyName();
            buf.append("/*IF ");
            buf.append(propertyName);
            buf.append(" != null*/");
            buf.append(" ");
            if (!began || i != 0) {
                buf.append("AND ");
            }
            buf.append(columnName);
            buf.append(" = /*");
            buf.append(propertyName);
            buf.append("*/null");
            buf.append("/*END*/");
        }
        if (began) {
            buf.append("/*END*/");
        }
        return buf.toString();
    }

    private DtoMetaDataImpl createDtoMetaData(Class dtoClass) {
        final DtoMetaDataImpl dtoMetaData = new DtoMetaDataImpl();
        dtoMetaData.setBeanClass(dtoClass);
        dtoMetaData.setBeanAnnotationReader(getAnnotationReaderFactory()
                .createBeanAnnotationReader(dtoClass));
        dtoMetaData.setValueTypeFactory(getValueTypeFactory());
        dtoMetaData.initialize();
        return dtoMetaData;
    }

    protected String createAutoSelectSql(String[] argNames) {
        String sql = dbms_.getAutoSelectSql(getBeanMetaData());
        StringBuffer buf = new StringBuffer(sql);
        if (argNames.length != 0) {
            boolean began = false;
            if (!(sql.lastIndexOf("WHERE") > 0)) {
                buf.append("/*BEGIN*/ WHERE ");
                began = true;
            }
            for (int i = 0; i < argNames.length; ++i) {
                String columnName = beanMetaData_
                        .convertFullColumnName(argNames[i]);
                buf.append("/*IF ");
                buf.append(argNames[i]);
                buf.append(" != null*/");
                buf.append(" ");
                if (!began || i != 0) {
                    buf.append("AND ");
                }
                buf.append(columnName);
                buf.append(" = /*");
                buf.append(argNames[i]);
                buf.append("*/null");
                buf.append("/*END*/");
            }
            if (began) {
                buf.append("/*END*/");
            }
        }
        return buf.toString();
    }

    protected void checkAutoUpdateMethod(Method method) {
        if (method.getParameterTypes().length != 1
                || !isBeanClassAssignable(method.getParameterTypes()[0])
                && !method.getParameterTypes()[0].isAssignableFrom(List.class)
                && !method.getParameterTypes()[0].isArray()) {
            throw new IllegalSignatureRuntimeException("EDAO0006", method
                    .toString());
        }
    }

    protected boolean isSelect(Method method) {
        if (isInsert(method.getName())) {
            return false;
        }
        if (isUpdate(method.getName())) {
            return false;
        }
        if (isDelete(method.getName())) {
            return false;
        }
        return true;
    }

    protected boolean isInsert(String methodName) {
        for (int i = 0; i < insertPrefixes_.length; ++i) {
            if (methodName.startsWith(insertPrefixes_[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isUpdate(String methodName) {
        for (int i = 0; i < updatePrefixes_.length; ++i) {
            if (methodName.startsWith(updatePrefixes_[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isDelete(String methodName) {
        for (int i = 0; i < deletePrefixes_.length; ++i) {
            if (methodName.startsWith(deletePrefixes_[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.seasar.dao.DaoMetaData#getBeanClass()
     */
    public Class getBeanClass() {
        return beanClass_;
    }

    protected void setBeanClass(Class beanClass) {
        beanClass_ = beanClass;
    }

    /**
     * @see org.seasar.dao.DaoMetaData#getBeanMetaData()
     */
    public BeanMetaData getBeanMetaData() {
        return beanMetaData_;
    }

    /**
     * @see org.seasar.dao.DaoMetaData#getSqlCommand(java.lang.String)
     */
    public SqlCommand getSqlCommand(String methodName)
            throws MethodNotFoundRuntimeException {

        SqlCommand cmd = (SqlCommand) sqlCommands_.get(methodName);
        if (cmd == null) {
            throw new MethodNotFoundRuntimeException(daoClass_, methodName,
                    null);
        }
        return cmd;
    }

    /**
     * @see org.seasar.dao.DaoMetaData#hasSqlCommand(java.lang.String)
     */
    public boolean hasSqlCommand(String methodName) {
        return sqlCommands_.containsKey(methodName);
    }

    /**
     * @see org.seasar.dao.DaoMetaData#createFindCommand(java.lang.String)
     */
    public SqlCommand createFindCommand(String query) {
        return createSelectDynamicCommand(new BeanListMetaDataResultSetHandler(
                beanMetaData_), query);
    }

    public SqlCommand createFindArrayCommand(String query) {
        return createSelectDynamicCommand(
                new BeanArrayMetaDataResultSetHandler(beanMetaData_), query);
    }

    /**
     * @see org.seasar.dao.DaoMetaData#createFindBeanCommand(java.lang.String)
     */
    public SqlCommand createFindBeanCommand(String query) {
        return createSelectDynamicCommand(new BeanMetaDataResultSetHandler(
                beanMetaData_), query);
    }

    /**
     * @see org.seasar.dao.DaoMetaData#createFindObjectCommand(java.lang.String)
     */
    public SqlCommand createFindObjectCommand(String query) {
        return createSelectDynamicCommand(new ObjectResultSetHandler(), query);
    }

    public Class getDaoInterface(Class clazz) {
        if (clazz.isInterface()) {
            return clazz;
        }
        for (Class target = clazz; target != AbstractDao.class; target = target
                .getSuperclass()) {
            Class[] interfaces = target.getInterfaces();
            for (int i = 0; i < interfaces.length; ++i) {
                Class intf = interfaces[i];
                for (int j = 0; j < daoSuffixes_.length; j++) {
                    if (intf.getName().endsWith(daoSuffixes_[j])) {
                        return intf;
                    }
                }
            }
        }
        throw new DaoNotFoundRuntimeException(clazz);
    }

    public void setDbms(Dbms dbms) {
        dbms_ = dbms;
    }

    protected AnnotationReaderFactory getAnnotationReaderFactory() {
        return annotationReaderFactory_;
    }

    public void setAnnotationReaderFactory(
            AnnotationReaderFactory annotationReaderFactory) {
        annotationReaderFactory_ = annotationReaderFactory;
    }

    public void setResultSetFactory(ResultSetFactory resultSetFactory) {
        resultSetFactory_ = resultSetFactory;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        statementFactory_ = statementFactory;
    }

    public void setDaoSuffixes(String[] daoSuffixes) {
        daoSuffixes_ = daoSuffixes;
    }

    public void setDeletePrefixes(String[] deletePrefixes) {
        deletePrefixes_ = deletePrefixes;
    }

    protected String getSqlFileEncoding() {
        return encoding;
    }

    public void setSqlFileEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setInsertPrefixes(String[] insertPrefixes) {
        insertPrefixes_ = insertPrefixes;
    }

    public void setUpdatePrefixes(String[] updatePrefixes) {
        updatePrefixes_ = updatePrefixes;
    }

    protected ValueTypeFactory getValueTypeFactory() {
        return valueTypeFactory;
    }

    public void setValueTypeFactory(ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    public void setDataSource(DataSource dataSource) {
        dataSource_ = dataSource;
    }

    protected Class getDaoClass() {
        return daoClass_;
    }

    public void setDaoClass(Class daoClass) {
        daoClass_ = daoClass;
    }

    static class DaoResultSetHandlerFactoryImpl implements
            DaoResultSetHandlerFactory {

        final BeanMetaData beanMetaData;

        DaoResultSetHandlerFactoryImpl(BeanMetaData beanMetaData) {
            this.beanMetaData = beanMetaData;
        }

        public ResultSetHandler createResultSetHandler(final Method method) {
            final Class beanClass = beanMetaData.getBeanClass();
            if (List.class.isAssignableFrom(method.getReturnType())) {
                return new BeanListMetaDataResultSetHandler(beanMetaData);
            } else if (isBeanClassAssignable(beanClass, method.getReturnType())) {
                return new BeanMetaDataResultSetHandler(beanMetaData);
            } else if (Array.newInstance(beanClass, 0).getClass()
                    .isAssignableFrom(method.getReturnType())) {
                return new BeanArrayMetaDataResultSetHandler(beanMetaData);
            } else {
                return new ObjectResultSetHandler();
            }
        }

        private boolean isBeanClassAssignable(Class beanClass, Class clazz) {
            return beanClass.isAssignableFrom(clazz)
                    || clazz.isAssignableFrom(beanClass);
        }

    }

}
