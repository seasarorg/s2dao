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
import org.seasar.dao.Dbms;
import org.seasar.dao.DtoMetaData;
import org.seasar.dao.IllegalSignatureRuntimeException;
import org.seasar.dao.ResultSetHandlerFactory;
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

    private static final Pattern startWithIfCommentPattern = Pattern.compile(
            "/\\*IF .+", Pattern.CASE_INSENSITIVE);

    private static final String NOT_SINGLE_ROW_UPDATED = "NotSingleRowUpdated";

    protected Class daoClass;

    protected Class daoInterface;

    protected BeanDesc daoBeanDesc;

    protected DataSource dataSource;

    protected DaoAnnotationReader annotationReader;

    protected StatementFactory statementFactory;

    protected ResultSetFactory resultSetFactory;

    protected AnnotationReaderFactory annotationReaderFactory;

    protected String sqlFileEncoding = "JISAutoDetect";

    protected Dbms dbms;

    protected Class beanClass;

    protected BeanMetaData beanMetaData;

    protected Map sqlCommands = new HashMap();

    protected ValueTypeFactory valueTypeFactory;

    protected String[] daoSuffixes = new String[] { "Dao" };

    protected String[] insertPrefixes = new String[] { "insert", "create",
            "add" };

    protected String[] updatePrefixes = new String[] { "update", "modify",
            "store" };

    protected String[] deletePrefixes = new String[] { "delete", "remove" };

    protected String[] unlessNullSuffixes = new String[] { "UnlessNull" };

    protected ResultSetHandlerFactory resultSetHandlerFactory;

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
        daoInterface = getDaoInterface(daoClass);
        daoBeanDesc = BeanDescFactory.getBeanDesc(daoClass);
        annotationReader = getAnnotationReaderFactory()
                .createDaoAnnotationReader(daoBeanDesc);
        setBeanClass(annotationReader.getBeanClass());
        Connection con = DataSourceUtil.getConnection(dataSource);
        try {
            DatabaseMetaData dbMetaData = ConnectionUtil.getMetaData(con);
            dbms = DbmsManager.getDbms(dbMetaData);
            BeanMetaDataImpl beanMetaDataImpl = new BeanMetaDataImpl();
            beanMetaDataImpl.setBeanClass(getBeanClass());
            beanMetaDataImpl.setDatabaseMetaData(dbMetaData);
            beanMetaDataImpl.setDbms(dbms);
            beanMetaDataImpl
                    .setAnnotationReaderFactory(getAnnotationReaderFactory());
            beanMetaDataImpl.setValueTypeFactory(getValueTypeFactory());
            beanMetaDataImpl.initialize();
            this.beanMetaData = beanMetaDataImpl;
        } finally {
            ConnectionUtil.close(con);
        }
        resultSetHandlerFactory = new ResultSetHandlerFactoryImpl(beanMetaData);
        setupSqlCommand();
    }

    protected void setupSqlCommand() {
        BeanDesc idbd = BeanDescFactory.getBeanDesc(daoInterface);
        String[] names = idbd.getMethodNames();
        for (int i = 0; i < names.length; ++i) {
            Method[] methods = daoBeanDesc.getMethods(names[i]);
            if (methods.length == 1 && MethodUtil.isAbstract(methods[0])) {
                setupMethod(methods[0]);
            }
        }
    }

    protected void setupMethod(Method method) {
        setupMethod(daoInterface, method);
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
        String sql = annotationReader.getSQL(method, dbms.getSuffix());
        if (sql != null) {
            setupMethodByManual(method, sql);
        }
        String procedureName = annotationReader.getStoredProcedureName(method);
        if (procedureName != null) {
            setupProcedureMethod(method, procedureName);
        }
    }

    protected void setupProcedureMethod(final Method method,
            final String procedureName) {

        final ProcedureHandlerImpl handler = new ProcedureHandlerImpl();
        handler.setDataSource(dataSource);
        handler.setDaoMethod(method);
        handler.setProcedureName(procedureName);
        handler.setResultSetHandlerFactory(resultSetHandlerFactory);
        handler.setStatementFactory(statementFactory);
        handler.initialize();

        final StaticStoredProcedureCommand command = new StaticStoredProcedureCommand(
                handler);
        sqlCommands.put(method.getName(), command);
    }

    protected String readText(String path) {
        InputStream is = ResourceUtil.getResourceAsStream(path);
        Reader reader = InputStreamReaderUtil.create(is, getSqlFileEncoding());
        return ReaderUtil.readText(reader);
    }

    protected void setupMethodBySqlFile(Class daoInterface, Method method) {
        String base = daoInterface.getName().replace('.', '/') + "_"
                + method.getName();
        String dbmsPath = base + dbms.getSuffix() + ".sql";
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
        return sqlCommands.get(method.getName()) != null;
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
        cmd.setArgNames(annotationReader.getArgNames(method));
        cmd.setArgTypes(method.getParameterTypes());
        sqlCommands.put(method.getName(), cmd);
    }

    protected SelectDynamicCommand createSelectDynamicCommand(
            ResultSetHandler rsh) {
        return new SelectDynamicCommand(dataSource, statementFactory, rsh,
                resultSetFactory);
    }

    protected SelectDynamicCommand createSelectDynamicCommand(
            ResultSetHandler resultSetHandler, String query) {

        SelectDynamicCommand cmd = createSelectDynamicCommand(resultSetHandler);
        StringBuffer buf = new StringBuffer(255);
        if (startsWithSelect(query)) {
            buf.append(query);
        } else {
            String sql = dbms.getAutoSelectSql(getBeanMetaData());
            buf.append(sql);
            if (query != null) {
                boolean began = false;
                if (startsWithOrderBy(query)) {
                    buf.append(" ");
                } else if (startsWithBeginComment(query)) {
                    buf.append(" ");
                } else if (sql.lastIndexOf("WHERE") < 0) {
                    if (startsWithIfComment(query)) {
                        buf.append("/*BEGIN*/");
                        began = true;
                    }
                    buf.append(" WHERE ");
                } else {
                    if (startsWithIfComment(query)) {
                        buf.append("/*BEGIN*/");
                        began = true;
                    }
                    buf.append(" AND ");
                }
                buf.append(query);
                if (began) {
                    buf.append("/*END*/");
                }
            }
        }
        cmd.setSql(buf.toString());
        return cmd;
    }

    protected boolean startsWithIfComment(String query) {
        Matcher m = startWithIfCommentPattern.matcher(query);
        if (m.lookingAt()) {
            return true;
        }
        return false;
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
        return beanClass.isAssignableFrom(clazz)
                || clazz.isAssignableFrom(beanClass);
    }

    // update & insert & delete
    protected void setupUpdateMethodByManual(Method method, String sql) {
        UpdateDynamicCommand cmd = new UpdateDynamicCommand(dataSource,
                statementFactory);
        cmd.setSql(sql);
        String[] argNames = annotationReader.getArgNames(method);
        if (argNames.length == 0 && isUpdateSignatureForBean(method)) {
            argNames = new String[] { StringUtil.decapitalize(ClassUtil
                    .getShortClassName(beanClass)) };
        }
        cmd.setArgNames(argNames);
        cmd.setArgTypes(method.getParameterTypes());
        cmd
                .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
        sqlCommands.put(method.getName(), cmd);
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
        final SqlCommand command;
        if (isUpdateSignatureForBean(method)) {
            InsertAutoDynamicCommand cmd = new InsertAutoDynamicCommand();
            cmd.setBeanMetaData(getBeanMetaData());
            cmd.setDataSource(dataSource);
            cmd
                    .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
            cmd.setPropertyNames(propertyNames);
            cmd.setStatementFactory(statementFactory);
            command = cmd;
        } else {
            InsertBatchAutoStaticCommand cmd = new InsertBatchAutoStaticCommand(
                    dataSource, statementFactory, getBeanMetaData(),
                    propertyNames);
            command = cmd;
        }
        sqlCommands.put(method.getName(), command);
    }

    // update
    protected void setupUpdateMethodByAuto(Method method) {
        checkAutoUpdateMethod(method);
        String[] propertyNames = getPersistentPropertyNames(method);
        AbstractSqlCommand cmd = null;
        if (isUpdateSignatureForBean(method)) {
            if (isUnlessNull(method.getName())) {
                cmd = createUpdateAutoDynamicCommand(method, propertyNames);
            } else {
                cmd = new UpdateAutoStaticCommand(dataSource, statementFactory,
                        beanMetaData, propertyNames);
            }
        } else {
            cmd = new UpdateBatchAutoStaticCommand(dataSource,
                    statementFactory, beanMetaData, propertyNames);
        }
        sqlCommands.put(method.getName(), cmd);
    }

    /**
     * @param method
     * @param propertyNames
     * @return
     */
    private AbstractSqlCommand createUpdateAutoDynamicCommand(Method method,
            String[] propertyNames) {
        AbstractSqlCommand cmd;
        UpdateAutoDynamicCommand uac = new UpdateAutoDynamicCommand(dataSource,
                statementFactory);
        uac.setBeanMetaData(beanMetaData);
        uac.setPropertyNames(propertyNames);
        uac
                .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
        cmd = uac;
        return cmd;
    }

    protected void setupDeleteMethodByAuto(Method method) {
        checkAutoUpdateMethod(method);
        String[] propertyNames = getPersistentPropertyNames(method);
        SqlCommand cmd = null;
        if (isUpdateSignatureForBean(method)) {
            cmd = new DeleteAutoStaticCommand(dataSource, statementFactory,
                    beanMetaData, propertyNames);
        } else {
            cmd = new DeleteBatchAutoStaticCommand(dataSource,
                    statementFactory, beanMetaData, propertyNames);
        }
        sqlCommands.put(method.getName(), cmd);
    }

    protected String[] getPersistentPropertyNames(Method method) {
        List names = new ArrayList();
        String[] props = annotationReader.getNoPersistentProps(method);
        if (props != null) {
            for (int i = 0; i < beanMetaData.getPropertyTypeSize(); ++i) {
                PropertyType pt = beanMetaData.getPropertyType(i);
                if (pt.isPersistent()
                        && !isPropertyExist(props, pt.getPropertyName())) {
                    names.add(pt.getPropertyName());
                }
            }
        } else {
            props = annotationReader.getPersistentProps(method);
            if (props != null) {
                names.addAll(Arrays.asList(props));
                for (int i = 0; i < beanMetaData.getPrimaryKeySize(); ++i) {
                    String pk = beanMetaData.getPrimaryKey(i);
                    PropertyType pt = beanMetaData
                            .getPropertyTypeByColumnName(pk);
                    names.add(pt.getPropertyName());
                }
                if (beanMetaData.hasVersionNoPropertyType()) {
                    names.add(beanMetaData.getVersionNoPropertyName());
                }
                if (beanMetaData.hasTimestampPropertyType()) {
                    names.add(beanMetaData.getTimestampPropertyName());
                }
            }
        }
        if (names.size() == 0) {
            for (int i = 0; i < beanMetaData.getPropertyTypeSize(); ++i) {
                PropertyType pt = beanMetaData.getPropertyType(i);
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
        String query = annotationReader.getQuery(method);
        ResultSetHandler handler = createResultSetHandler(method);
        SelectDynamicCommand cmd = null;
        String[] argNames = annotationReader.getArgNames(method);
        Class[] types = method.getParameterTypes();
        if (query != null && !startsWithOrderBy(query)) {
            cmd = createSelectDynamicCommand(handler, query);
        } else {
            cmd = createSelectDynamicCommand(handler);
            String sql = null;
            if (argNames.length == 0 && method.getParameterTypes().length == 1) {
                Class clazz = method.getParameterTypes()[0];
                if (isUpdateSignatureForBean(method)) {
                    clazz = beanClass;
                }
                sql = createAutoSelectSqlByDto(clazz);
                types = new Class[] { clazz };
            } else {
                sql = createAutoSelectSql(argNames);
            }
            if (query != null) {
                sql = sql + " " + query;
            }
            cmd.setSql(sql);
        }
        cmd.setArgNames(argNames);
        cmd.setArgTypes(types);
        sqlCommands.put(method.getName(), cmd);
    }

    protected String createAutoSelectSqlByDto(Class dtoClass) {
        String sql = dbms.getAutoSelectSql(getBeanMetaData());
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
            if (!beanMetaData.hasPropertyTypeByAliasName(aliasName)) {
                continue;
            }
            if (!beanMetaData.getPropertyTypeByAliasName(aliasName)
                    .isPersistent()) {
                continue;
            }
            String columnName = beanMetaData.convertFullColumnName(aliasName);
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
        for (int i = 0; i < beanMetaData.getPropertyTypeSize(); i++) {
            PropertyType master = beanMetaData.getPropertyType(i);
            String name = master.getPropertyName();
            if (dtoMetaData.hasPropertyType(name)) {
                PropertyType slave = dtoMetaData.getPropertyType(name);
                slave.setColumnName(master.getColumnName());
            }
        }
        return dtoMetaData;
    }

    protected String createAutoSelectSql(String[] argNames) {
        String sql = dbms.getAutoSelectSql(getBeanMetaData());
        StringBuffer buf = new StringBuffer(sql);
        if (argNames.length != 0) {
            boolean began = false;
            if (!(sql.lastIndexOf("WHERE") > 0)) {
                buf.append("/*BEGIN*/ WHERE ");
                began = true;
            }
            for (int i = 0; i < argNames.length; ++i) {
                String columnName = beanMetaData
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
        for (int i = 0; i < insertPrefixes.length; ++i) {
            if (methodName.startsWith(insertPrefixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isUpdate(String methodName) {
        for (int i = 0; i < updatePrefixes.length; ++i) {
            if (methodName.startsWith(updatePrefixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isDelete(String methodName) {
        for (int i = 0; i < deletePrefixes.length; ++i) {
            if (methodName.startsWith(deletePrefixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isUnlessNull(String methodName) {
        for (int i = 0; i < unlessNullSuffixes.length; i++) {
            if (methodName.endsWith(unlessNullSuffixes[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.seasar.dao.DaoMetaData#getBeanClass()
     */
    public Class getBeanClass() {
        return beanClass;
    }

    protected void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * @see org.seasar.dao.DaoMetaData#getBeanMetaData()
     */
    public BeanMetaData getBeanMetaData() {
        return beanMetaData;
    }

    /**
     * @see org.seasar.dao.DaoMetaData#getSqlCommand(java.lang.String)
     */
    public SqlCommand getSqlCommand(String methodName)
            throws MethodNotFoundRuntimeException {

        SqlCommand cmd = (SqlCommand) sqlCommands.get(methodName);
        if (cmd == null) {
            throw new MethodNotFoundRuntimeException(daoClass, methodName, null);
        }
        return cmd;
    }

    /**
     * @see org.seasar.dao.DaoMetaData#hasSqlCommand(java.lang.String)
     */
    public boolean hasSqlCommand(String methodName) {
        return sqlCommands.containsKey(methodName);
    }

    /**
     * @see org.seasar.dao.DaoMetaData#createFindCommand(java.lang.String)
     */
    public SqlCommand createFindCommand(String query) {
        return createSelectDynamicCommand(new BeanListMetaDataResultSetHandler(
                beanMetaData), query);
    }

    public SqlCommand createFindArrayCommand(String query) {
        return createSelectDynamicCommand(
                new BeanArrayMetaDataResultSetHandler(beanMetaData), query);
    }

    /**
     * @see org.seasar.dao.DaoMetaData#createFindBeanCommand(java.lang.String)
     */
    public SqlCommand createFindBeanCommand(String query) {
        return createSelectDynamicCommand(new BeanMetaDataResultSetHandler(
                beanMetaData), query);
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
                for (int j = 0; j < daoSuffixes.length; j++) {
                    if (intf.getName().endsWith(daoSuffixes[j])) {
                        return intf;
                    }
                }
            }
        }
        throw new DaoNotFoundRuntimeException(clazz);
    }

    public void setDbms(Dbms dbms) {
        this.dbms = dbms;
    }

    protected AnnotationReaderFactory getAnnotationReaderFactory() {
        return annotationReaderFactory;
    }

    public void setAnnotationReaderFactory(
            AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    public void setResultSetFactory(ResultSetFactory resultSetFactory) {
        this.resultSetFactory = resultSetFactory;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    public void setDaoSuffixes(String[] daoSuffixes) {
        this.daoSuffixes = daoSuffixes;
    }

    public void setDeletePrefixes(String[] deletePrefixes) {
        this.deletePrefixes = deletePrefixes;
    }

    protected String getSqlFileEncoding() {
        return sqlFileEncoding;
    }

    public void setSqlFileEncoding(String sencoding) {
        this.sqlFileEncoding = sencoding;
    }

    public void setInsertPrefixes(String[] insertPrefixes) {
        this.insertPrefixes = insertPrefixes;
    }

    public void setUpdatePrefixes(String[] updatePrefixes) {
        this.updatePrefixes = updatePrefixes;
    }

    public void setUnlessNullSuffixes(String[] suffixes) {
        this.unlessNullSuffixes = suffixes;
    }

    protected ValueTypeFactory getValueTypeFactory() {
        return valueTypeFactory;
    }

    public void setValueTypeFactory(ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Class getDaoClass() {
        return daoClass;
    }

    public void setDaoClass(Class daoClass) {
        this.daoClass = daoClass;
    }

    static class ResultSetHandlerFactoryImpl implements ResultSetHandlerFactory {

        final BeanMetaData beanMetaData;

        ResultSetHandlerFactoryImpl(BeanMetaData beanMetaData) {
            this.beanMetaData = beanMetaData;
        }

        public ResultSetHandler createResultSetHandler(final Method method) {
            final Class beanClass = beanMetaData.getBeanClass();
            if (List.class.isAssignableFrom(method.getReturnType())) {
                return new BeanListMetaDataResultSetHandler(beanMetaData);
            } else if (isBeanClassAssignable(beanClass, method.getReturnType())) {
                return new BeanMetaDataResultSetHandler(beanMetaData);
            } else if (method.getReturnType().isAssignableFrom(
                    Array.newInstance(beanClass, 0).getClass())) {
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
