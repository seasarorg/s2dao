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

import java.io.InputStream;
import java.io.Reader;
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

import org.seasar.dao.ArgumentDtoAnnotationReader;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.DaoAnnotationReader;
import org.seasar.dao.DaoMetaData;
import org.seasar.dao.DaoNamingConvention;
import org.seasar.dao.DaoNotFoundRuntimeException;
import org.seasar.dao.Dbms;
import org.seasar.dao.DtoMetaData;
import org.seasar.dao.DtoMetaDataFactory;
import org.seasar.dao.IllegalAnnotationRuntimeException;
import org.seasar.dao.IllegalSignatureRuntimeException;
import org.seasar.dao.InjectDaoClassSupport;
import org.seasar.dao.MethodSetupFailureRuntimeException;
import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureMetaDataFactory;
import org.seasar.dao.RelationRowCreator;
import org.seasar.dao.ResultSetHandlerFactory;
import org.seasar.dao.RowCreator;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.SqlFileNotFoundRuntimeException;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.dao.handler.ProcedureHandlerImpl;
import org.seasar.dao.pager.NullPagingSqlRewriter;
import org.seasar.dao.pager.PagingSqlRewriter;
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
import org.seasar.framework.exception.SRuntimeException;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.InputStreamReaderUtil;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.ReaderUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author higa
 * @author azusa
 * 
 */
public class DaoMetaDataImpl implements DaoMetaData {

    private static final Pattern startWithOrderByPattern = Pattern.compile(
            "(/\\*[^*]+\\*/)*order by", Pattern.CASE_INSENSITIVE);

    private static final Pattern startWithSelectPattern = Pattern.compile(
            "^\\s*select\\s", Pattern.CASE_INSENSITIVE);

    private static final Pattern beginCommentPattern = Pattern.compile(
            "/\\*BEGIN\\*/\\s*WHERE", Pattern.CASE_INSENSITIVE);

    private static final Pattern startWithBeginCommentPattern = Pattern
            .compile("/\\*BEGIN\\*/\\s*WHERE .+", Pattern.CASE_INSENSITIVE);

    private static final Pattern startWithIfCommentPattern = Pattern.compile(
            "/\\*IF .+", Pattern.CASE_INSENSITIVE);

    private static final String NOT_SINGLE_ROW_UPDATED = "NotSingleRowUpdated";

    protected Class daoClass;

    protected Class daoInterface;

    protected BeanDesc daoBeanDesc;

    protected DataSource dataSource;

    protected DaoAnnotationReader daoAnnotationReader;

    protected StatementFactory statementFactory;

    protected ResultSetFactory resultSetFactory;

    protected String sqlFileEncoding = "JISAutoDetect";

    protected Dbms dbms;

    protected Class beanClass;

    protected BeanMetaData beanMetaData;

    protected Map sqlCommands = new HashMap();

    protected ValueTypeFactory valueTypeFactory;

    protected BeanMetaDataFactory beanMetaDataFactory;

    protected DtoMetaDataFactory dtoMetaDataFactory;

    protected ResultSetHandlerFactory resultSetHandlerFactory;

    protected DaoNamingConvention daoNamingConvention;

    protected boolean useDaoClassForLog = false;

    protected PagingSqlRewriter pagingSqlRewriter = new NullPagingSqlRewriter();

    protected ArgumentDtoAnnotationReader argumentDtoAnnotationReader;

    public DaoMetaDataImpl() {
    }

    public void initialize() {
        beanClass = daoAnnotationReader.getBeanClass();
        daoInterface = getDaoInterface(daoClass);
        daoBeanDesc = BeanDescFactory.getBeanDesc(daoClass);
        final Connection con = DataSourceUtil.getConnection(dataSource);
        try {
            final DatabaseMetaData dbMetaData = ConnectionUtil.getMetaData(con);
            dbms = DbmsManager.getDbms(dbMetaData);
        } finally {
            ConnectionUtil.close(con);
        }
        this.beanMetaData = beanMetaDataFactory.createBeanMetaData(
                daoInterface, beanClass);
        setupSqlCommand();
    }

    protected void setupSqlCommand() {
        final BeanDesc idbd = BeanDescFactory.getBeanDesc(daoInterface);
        final String[] names = idbd.getMethodNames();
        for (int i = 0; i < names.length; ++i) {
            final Method[] methods = daoBeanDesc.getMethods(names[i]);
            if (methods.length == 1 && MethodUtil.isAbstract(methods[0])) {
                setupMethod(methods[0]);
            }
        }
    }

    protected void setupMethod(final Method method) {
        setupMethod(daoInterface, method);
    }

    protected void setupMethod(final Class daoInterface, final Method method) {
        try {
            assertAnnotation(method);

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

            if (!completedSetupMethod(method)
                    && daoAnnotationReader.isSqlFile(method)) {
                throw new SqlFileNotFoundRuntimeException(daoInterface, method);
            }

            if (!completedSetupMethod(method)) {
                setupMethodByAuto(method);
            }
        } catch (final SRuntimeException e) {
            throw new MethodSetupFailureRuntimeException(
                    daoInterface.getName(), method.getName(), e);
        }
    }

    protected void setupMethodByAnnotation(final Class daoInterface,
            final Method method) {
        final String sql = daoAnnotationReader.getSQL(method, dbms.getSuffix());
        if (sql != null) {
            setupMethodByManual(method, sql);
        }
        final String procedureName = daoAnnotationReader
                .getStoredProcedureName(method);
        if (procedureName != null) {
            setupProcedureMethod(method, procedureName);
        }
    }

    protected void assertAnnotation(final Method method) {
        if (isInsert(method.getName()) || isUpdate(method.getName())) {
            if (daoAnnotationReader.getQuery(method) != null) {
                throw new IllegalAnnotationRuntimeException("Query");
            }
        }
    }

    protected void setupProcedureMethod(final Method method,
            final String procedureName) {

        SqlCommand command;
        final ResultSetHandler resultSetHandler = createResultSetHandler(method);
        final Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 1
                && argumentDtoAnnotationReader
                        .isProcedureParameters(paramTypes[0])) {
            final ProcedureMetaDataFactory factory = new DtoProcedureMetaDataFactory(
                    procedureName, paramTypes[0], valueTypeFactory,
                    argumentDtoAnnotationReader);
            final ProcedureMetaData metaData = factory
                    .createProcedureMetaData();
            command = new DtoProcedureCommand(dataSource, resultSetHandler,
                    statementFactory, resultSetFactory, metaData);
        } else {
            final ProcedureHandlerImpl handler = new ProcedureHandlerImpl();
            handler.setDataSource(dataSource);
            handler.setDbms(dbms);
            handler.setDaoMethod(method);
            handler.setDaoAnnotationReader(daoAnnotationReader);
            handler.setBeanMetaData(beanMetaData);
            handler.setProcedureName(procedureName);
            handler.setResultSetHandlerFactory(resultSetHandlerFactory);
            handler.setStatementFactory(statementFactory);
            handler.initialize();
            command = new StaticStoredProcedureCommand(handler);
        }
        sqlCommands.put(method.getName(), command);
    }

    protected String readText(final String path) {
        final InputStream is = ResourceUtil.getResourceAsStream(path);
        final Reader reader = InputStreamReaderUtil.create(is,
                getSqlFileEncoding());
        return ReaderUtil.readText(reader);
    }

    protected void setupMethodBySqlFile(final Class daoInterface,
            final Method method) {
        final String base = daoInterface.getName().replace('.', '/') + "_"
                + method.getName();
        final String dbmsPath = base + dbms.getSuffix() + ".sql";
        final String standardPath = base + ".sql";
        if (ResourceUtil.isExist(dbmsPath)) {
            final String sql = readText(dbmsPath);
            setupMethodByManual(method, sql);
        } else if (ResourceUtil.isExist(standardPath)) {
            final String sql = readText(standardPath);
            setupMethodByManual(method, sql);
        } else if (isDelete(method.getName())) {
            final String query = daoAnnotationReader.getQuery(method);
            if (StringUtil.isNotBlank(query)) {
                if (query.trim().toUpperCase().startsWith("WHERE")) {
                    setupMethodByManual(method, "DELETE FROM "
                            + beanMetaData.getTableName() + " " + query);
                } else {
                    setupMethodByManual(method, "DELETE FROM "
                            + beanMetaData.getTableName() + " WHERE " + query);
                }
            }
        }
    }

    protected void setupMethodByInterfaces(final Class daoInterface,
            final Method method) {
        final Class[] interfaces = daoInterface.getInterfaces();
        if (interfaces == null) {
            return;
        }
        for (int i = 0; i < interfaces.length; i++) {
            final Method interfaceMethod = getSameSignatureMethod(
                    interfaces[i], method);
            if (interfaceMethod != null) {
                setupMethod(interfaces[i], interfaceMethod);
            }
        }
    }

    protected void setupMethodBySuperClass(final Class daoInterface,
            final Method method) {
        final Class superDaoClass = daoInterface.getSuperclass();
        if (superDaoClass != null && !Object.class.equals(superDaoClass)) {
            final Method superClassMethod = getSameSignatureMethod(
                    superDaoClass, method);
            if (superClassMethod != null) {
                setupMethod(superDaoClass, method);
            }
        }
    }

    protected boolean completedSetupMethod(final Method method) {
        return sqlCommands.get(method.getName()) != null;
    }

    private Method getSameSignatureMethod(final Class clazz, final Method method) {
        try {
            final String methodName = method.getName();
            final Class[] parameterTypes = method.getParameterTypes();
            return ClassUtil.getMethod(clazz, methodName, parameterTypes);
        } catch (final NoSuchMethodRuntimeException e) {
            return null;
        }
    }

    protected void setupMethodByManual(final Method method, final String sql) {
        if (isSelect(method)) {
            setupSelectMethodByManual(method, sql);
        } else {
            setupUpdateMethodByManual(method, sql);
        }
    }

    protected void setupMethodByAuto(final Method method) {
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

    protected void setupSelectMethodByManual(final Method method,
            final String sql) {
        final SelectDynamicCommand cmd = createSelectDynamicCommand(createResultSetHandler(method));
        cmd.setSql(sql);
        cmd.setArgNames(daoAnnotationReader.getArgNames(method));
        cmd.setArgTypes(method.getParameterTypes());
        sqlCommands.put(method.getName(), cmd);
    }

    protected SelectDynamicCommand createSelectDynamicCommand(
            final ResultSetHandler rsh) {
        return new SelectDynamicCommand(dataSource, statementFactory, rsh,
                resultSetFactory, pagingSqlRewriter);
    }

    protected SelectDynamicCommand createSelectDynamicCommand(
            final ResultSetHandler resultSetHandler, final String query) {

        final SelectDynamicCommand cmd = createSelectDynamicCommand(resultSetHandler);
        final StringBuffer buf = new StringBuffer(255);
        if (startsWithSelect(query)) {
            buf.append(query);
        } else {
            final String sql = dbms.getAutoSelectSql(getBeanMetaData());
            buf.append(sql);
            if (query != null) {
                String adjustedQuery = query;
                boolean began = false;
                boolean whereContained = sql.lastIndexOf("WHERE") > 0;
                if (startsWithOrderBy(query)) {
                    buf.append(" ");
                } else if (startsWithBeginComment(query)) {
                    buf.append(" ");
                    if (whereContained) {
                        final Matcher matcher = beginCommentPattern
                                .matcher(query);
                        adjustedQuery = matcher.replaceFirst("/*BEGIN*/AND");
                    }
                } else if (!whereContained) {
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
                buf.append(adjustedQuery);
                if (began) {
                    buf.append("/*END*/");
                }
            }
        }
        cmd.setSql(buf.toString());
        return cmd;
    }

    protected boolean startsWithIfComment(final String query) {
        final Matcher m = startWithIfCommentPattern.matcher(query);
        if (m.lookingAt()) {
            return true;
        }
        return false;
    }

    protected boolean startsWithBeginComment(final String query) {
        final Matcher m = startWithBeginCommentPattern.matcher(query);
        if (m.lookingAt()) {
            return true;
        }
        return false;
    }

    protected static boolean startsWithSelect(final String query) {
        if (query != null) {
            final Matcher m = startWithSelectPattern.matcher(query);
            if (m.lookingAt()) {
                return true;
            }
        }
        return false;
    }

    protected static boolean startsWithOrderBy(final String query) {
        if (query != null) {
            final Matcher m = startWithOrderByPattern.matcher(query);
            if (m.lookingAt()) {
                return true;
            }
        }
        return false;
    }

    protected ResultSetHandler createResultSetHandler(final Method method) {
        return resultSetHandlerFactory.getResultSetHandler(daoAnnotationReader,
                beanMetaData, method);
    }

    protected boolean isBeanClassAssignable(final Class clazz) {
        return beanClass.isAssignableFrom(clazz)
                || clazz.isAssignableFrom(beanClass);
    }

    // update & insert & delete
    protected void setupUpdateMethodByManual(final Method method,
            final String sql) {
        final UpdateDynamicCommand cmd = new UpdateDynamicCommand(dataSource,
                statementFactory);
        cmd.setSql(sql);
        String[] argNames = daoAnnotationReader.getArgNames(method);
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

    protected boolean isUpdateSignatureForBean(final Method method) {
        return method.getParameterTypes().length == 1
                && isBeanClassAssignable(method.getParameterTypes()[0]);
    }

    protected Class getNotSingleRowUpdatedExceptionClass(final Method method) {
        final Class[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes != null) {
            for (int i = 0; i < exceptionTypes.length; ++i) {
                final Class exceptionType = exceptionTypes[i];
                if (exceptionType.getName().indexOf(NOT_SINGLE_ROW_UPDATED) >= 0) {
                    return exceptionType;
                }
            }
        }
        return null;
    }

    protected void setupInsertMethodByAuto(final Method method) {
        checkAutoUpdateMethod(method);
        final String[] propertyNames = getPersistentPropertyNames(method);
        final SqlCommand command;
        if (isUpdateSignatureForBean(method)) {
            final InsertAutoDynamicCommand cmd = new InsertAutoDynamicCommand();
            cmd.setBeanMetaData(getBeanMetaData());
            cmd.setDataSource(dataSource);
            cmd
                    .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
            cmd.setPropertyNames(propertyNames);
            cmd.setStatementFactory(statementFactory);
            command = cmd;
        } else {
            final InsertBatchAutoStaticCommand cmd = new InsertBatchAutoStaticCommand(
                    dataSource, statementFactory, getBeanMetaData(),
                    propertyNames);
            command = cmd;
        }
        sqlCommands.put(method.getName(), command);
    }

    // update
    protected void setupUpdateMethodByAuto(final Method method) {
        checkAutoUpdateMethod(method);
        final String[] propertyNames = getPersistentPropertyNames(method);
        AbstractSqlCommand cmd;
        if (isUpdateSignatureForBean(method)) {
            if (isUnlessNull(method.getName())) {
                cmd = createUpdateAutoDynamicCommand(method, propertyNames);
            } else if (isModifiedOnly(method.getName())) {
                cmd = createUpdateModifiedOnlyCommand(method, propertyNames);
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
    private AbstractSqlCommand createUpdateAutoDynamicCommand(
            final Method method, final String[] propertyNames) {
        AbstractSqlCommand cmd;
        final UpdateAutoDynamicCommand uac = new UpdateAutoDynamicCommand(
                dataSource, statementFactory);
        uac.setBeanMetaData(beanMetaData);
        uac.setPropertyNames(propertyNames);
        uac
                .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
        cmd = uac;
        return cmd;
    }

    private AbstractSqlCommand createUpdateModifiedOnlyCommand(
            final Method method, final String[] propertyNames) {
        final UpdateModifiedOnlyCommand uac = new UpdateModifiedOnlyCommand(
                dataSource, statementFactory);
        uac.setBeanMetaData(beanMetaData);
        uac.setPropertyNames(propertyNames);
        uac
                .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
        return uac;
    }

    protected void setupDeleteMethodByAuto(final Method method) {
        checkAutoUpdateMethod(method);
        final String[] propertyNames = getPersistentPropertyNames(method);
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

    protected String[] getPersistentPropertyNames(final Method method) {
        final List names = new ArrayList();
        String[] props = daoAnnotationReader.getNoPersistentProps(method);
        if (props != null) {
            for (int i = 0; i < beanMetaData.getPropertyTypeSize(); ++i) {
                final PropertyType pt = beanMetaData.getPropertyType(i);
                if (pt.isPersistent()
                        && !isPropertyExist(props, pt.getPropertyName())) {
                    names.add(pt.getPropertyName());
                }
            }
        } else {
            props = daoAnnotationReader.getPersistentProps(method);
            if (props != null) {
                names.addAll(Arrays.asList(props));
                for (int i = 0; i < beanMetaData.getPrimaryKeySize(); ++i) {
                    final String pk = beanMetaData.getPrimaryKey(i);
                    final PropertyType pt = beanMetaData
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
                final PropertyType pt = beanMetaData.getPropertyType(i);
                if (pt.isPersistent()) {
                    names.add(pt.getPropertyName());
                }
            }
        }
        return (String[]) names.toArray(new String[names.size()]);
    }

    protected boolean isPropertyExist(final String[] props,
            final String propertyName) {
        for (int i = 0; i < props.length; ++i) {
            if (props[i].equalsIgnoreCase(propertyName)) {
                return true;
            }
        }
        return false;
    }

    protected void setupSelectMethodByAuto(final Method method) {
        final ResultSetHandler handler = createResultSetHandler(method);
        final String[] argNames = daoAnnotationReader.getArgNames(method);
        final String query = daoAnnotationReader.getQuery(method);
        SelectDynamicCommand cmd = null;
        if (query != null && !startsWithOrderBy(query)) {
            cmd = setupQuerySelectMethodByAuto(method, handler, argNames, query);
        } else {
            cmd = setupNonQuerySelectMethodByAuto(method, handler, argNames,
                    query);
        }
        sqlCommands.put(method.getName(), cmd);
    }

    protected boolean isQuerySelectMethodByAuto(final Method method,
            final String query) {
        return query != null && !startsWithOrderBy(query);
    }

    protected SelectDynamicCommand setupQuerySelectMethodByAuto(
            final Method method, final ResultSetHandler handler,
            final String[] argNames, final String query) {
        final Class[] types = method.getParameterTypes();
        final SelectDynamicCommand cmd = createSelectDynamicCommand(handler,
                query);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(types);
        return cmd;
    }

    protected SelectDynamicCommand setupNonQuerySelectMethodByAuto(
            final Method method, final ResultSetHandler handler,
            final String[] argNames, final String query) {
        if (isAutoSelectSqlByDto(method, argNames)) {
            return setupNonQuerySelectMethodByDto(method, handler, argNames,
                    query);
        } else {
            return setupNonQuerySelectMethodByArgs(method, handler, argNames,
                    query);
        }
    }

    protected boolean isAutoSelectSqlByDto(final Method method,
            final String[] argNames) {
        return argNames.length == 0 && method.getParameterTypes().length == 1;
    }

    protected SelectDynamicCommand setupNonQuerySelectMethodByDto(
            final Method method, final ResultSetHandler handler,
            final String[] argNames, final String query) {
        final SelectDynamicCommand cmd = createSelectDynamicCommand(handler);
        Class clazz = method.getParameterTypes()[0];
        if (isUpdateSignatureForBean(method)) {
            clazz = beanClass;
        }
        final Class[] types = new Class[] { clazz };
        String sql = createAutoSelectSqlByDto(clazz);
        if (query != null) {
            sql = sql + " " + query;
        }
        cmd.setSql(sql);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(types);
        return cmd;
    }

    protected SelectDynamicCommand setupNonQuerySelectMethodByArgs(
            final Method method, final ResultSetHandler handler,
            final String[] argNames, final String query) {
        final SelectDynamicCommand cmd = createSelectDynamicCommand(handler);
        final Class[] types = method.getParameterTypes();
        String sql = createAutoSelectSql(argNames);
        if (query != null) {
            sql = sql + " " + query;
        }
        cmd.setSql(sql);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(types);
        return cmd;
    }

    protected String createAutoSelectSqlByDto(final Class dtoClass) {
        final String sql = dbms.getAutoSelectSql(getBeanMetaData());
        final StringBuffer buf = new StringBuffer(sql);
        // TODO どうするか要検討
        if (dtoClass.isPrimitive()) {
            return sql;
        }
        final DtoMetaData dmd = createDtoMetaData(dtoClass);
        boolean began = false;
        if (!(sql.lastIndexOf("WHERE") > 0)) {
            buf.append("/*BEGIN*/ WHERE ");
            began = true;
        }
        for (int i = 0; i < dmd.getPropertyTypeSize(); ++i) {
            final PropertyType pt = dmd.getPropertyType(i);
            final String aliasName = pt.getColumnName();
            if (!beanMetaData.hasPropertyTypeByAliasName(aliasName)) {
                continue;
            }
            if (!beanMetaData.getPropertyTypeByAliasName(aliasName)
                    .isPersistent()) {
                continue;
            }
            final String columnName = beanMetaData
                    .convertFullColumnName(aliasName);
            final String propertyName = "dto." + pt.getPropertyName();
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

    private DtoMetaData createDtoMetaData(final Class dtoClass) {
        final DtoMetaData dtoMetaData = dtoMetaDataFactory
                .getDtoMetaData(dtoClass);
        for (int i = 0; i < beanMetaData.getPropertyTypeSize(); i++) {
            final PropertyType master = beanMetaData.getPropertyType(i);
            final String name = master.getPropertyName();
            if (dtoMetaData.hasPropertyType(name)) {
                final PropertyType slave = dtoMetaData.getPropertyType(name);
                slave.setColumnName(master.getColumnName());
            }
        }
        return dtoMetaData;
    }

    protected String createAutoSelectSql(final String[] argNames) {
        final String sql = dbms.getAutoSelectSql(getBeanMetaData());
        final StringBuffer buf = new StringBuffer(sql);
        if (argNames.length != 0) {
            boolean began = false;
            if (!(sql.lastIndexOf("WHERE") > 0)) {
                buf.append("/*BEGIN*/ WHERE ");
                began = true;
            }
            for (int i = 0; i < argNames.length; ++i) {
                final String columnName = beanMetaData
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

    protected void checkAutoUpdateMethod(final Method method) {
        if (method.getParameterTypes().length != 1
                || !isBeanClassAssignable(method.getParameterTypes()[0])
                && !method.getParameterTypes()[0].isAssignableFrom(List.class)
                && !method.getParameterTypes()[0].isArray()) {
            throw new IllegalSignatureRuntimeException("EDAO0006", method
                    .toString());
        }
    }

    protected boolean isSelect(final Method method) {
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

    protected boolean isInsert(final String methodName) {
        final String[] insertPrefixes = getDaoNamingConvention()
                .getInsertPrefixes();
        for (int i = 0; i < insertPrefixes.length; ++i) {
            if (methodName.startsWith(insertPrefixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isUpdate(final String methodName) {
        final String[] updatePrefixes = getDaoNamingConvention()
                .getUpdatePrefixes();
        for (int i = 0; i < updatePrefixes.length; ++i) {
            if (methodName.startsWith(updatePrefixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isDelete(final String methodName) {
        final String[] deletePrefixes = getDaoNamingConvention()
                .getDeletePrefixes();
        for (int i = 0; i < deletePrefixes.length; ++i) {
            if (methodName.startsWith(deletePrefixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isUnlessNull(final String methodName) {
        final String[] unlessNullSuffixes = getDaoNamingConvention()
                .getUnlessNullSuffixes();
        for (int i = 0; i < unlessNullSuffixes.length; i++) {
            if (methodName.endsWith(unlessNullSuffixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isModifiedOnly(final String methodName) {
        final String[] modifiedOnlySuffixes = getDaoNamingConvention()
                .getModifiedOnlySuffixes();
        for (int i = 0; i < modifiedOnlySuffixes.length; i++) {
            if (methodName.endsWith(modifiedOnlySuffixes[i])) {
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

    protected void setBeanClass(final Class beanClass) {
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
    public SqlCommand getSqlCommand(final String methodName)
            throws MethodNotFoundRuntimeException {

        final SqlCommand cmd = (SqlCommand) sqlCommands.get(methodName);
        if (cmd == null) {
            throw new MethodNotFoundRuntimeException(daoClass, methodName, null);
        }
        if (useDaoClassForLog) {
            if (cmd instanceof InjectDaoClassSupport) {
                ((InjectDaoClassSupport) cmd).setDaoClass(daoClass);
            }
        }
        return cmd;
    }

    /**
     * @see org.seasar.dao.DaoMetaData#hasSqlCommand(java.lang.String)
     */
    public boolean hasSqlCommand(final String methodName) {
        return sqlCommands.containsKey(methodName);
    }

    /**
     * @see org.seasar.dao.DaoMetaData#createFindCommand(java.lang.String)
     */
    public SqlCommand createFindCommand(final String query) {
        return createSelectDynamicCommand(new BeanListMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator()),
                query);
    }

    public SqlCommand createFindArrayCommand(final String query) {
        return createSelectDynamicCommand(
                new BeanArrayMetaDataResultSetHandler(beanMetaData,
                        createRowCreator(), createRelationRowCreator()), query);
    }

    /**
     * @see org.seasar.dao.DaoMetaData#createFindBeanCommand(java.lang.String)
     */
    public SqlCommand createFindBeanCommand(final String query) {
        return createSelectDynamicCommand(new BeanMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator()),
                query);
    }

    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }

    protected RelationRowCreator createRelationRowCreator() {
        return new RelationRowCreatorImpl();
    }

    /**
     * @see org.seasar.dao.DaoMetaData#createFindObjectCommand(java.lang.String)
     */
    public SqlCommand createFindObjectCommand(final String query) {
        return createSelectDynamicCommand(new ObjectResultSetHandler(), query);
    }

    public Class getDaoInterface(final Class clazz) {
        if (clazz.isInterface()) {
            return clazz;
        }
        final String[] daoSuffixes = getDaoNamingConvention().getDaoSuffixes();
        for (Class target = clazz; target != AbstractDao.class; target = target
                .getSuperclass()) {
            final Class[] interfaces = target.getInterfaces();
            for (int i = 0; i < interfaces.length; ++i) {
                final Class intf = interfaces[i];
                for (int j = 0; j < daoSuffixes.length; j++) {
                    if (intf.getName().endsWith(daoSuffixes[j])) {
                        return intf;
                    }
                }
            }
        }
        throw new DaoNotFoundRuntimeException(clazz);
    }

    public void setDbms(final Dbms dbms) {
        this.dbms = dbms;
    }

    public void setResultSetFactory(final ResultSetFactory resultSetFactory) {
        this.resultSetFactory = resultSetFactory;
    }

    public void setStatementFactory(final StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    protected String getSqlFileEncoding() {
        return sqlFileEncoding;
    }

    public void setSqlFileEncoding(final String sencoding) {
        this.sqlFileEncoding = sencoding;
    }

    public ValueTypeFactory getValueTypeFactory() {
        return valueTypeFactory;
    }

    public void setValueTypeFactory(final ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Class getDaoClass() {
        return daoClass;
    }

    public void setDaoClass(final Class daoClass) {
        this.daoClass = daoClass;
    }

    public void setBeanMetaDataFactory(
            final BeanMetaDataFactory beanMetaDataFactory) {
        this.beanMetaDataFactory = beanMetaDataFactory;
    }

    /**
     * @return Returns the dtoMetaDataFactory.
     */
    public DtoMetaDataFactory getDtoMetaDataFactory() {
        return dtoMetaDataFactory;
    }

    /**
     * @param dtoMetaDataFactory The dtoMetaDataFactory to set.
     */
    public void setDtoMetaDataFactory(
            final DtoMetaDataFactory dtoMetaDataFactory) {
        this.dtoMetaDataFactory = dtoMetaDataFactory;
    }

    public DaoNamingConvention getDaoNamingConvention() {
        return daoNamingConvention;
    }

    public void setDaoNamingConvention(
            final DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
    }

    public boolean isUseDaoClassForLog() {
        return useDaoClassForLog;
    }

    public void setUseDaoClassForLog(final boolean setUserDaoClassForLog) {
        this.useDaoClassForLog = setUserDaoClassForLog;
    }

    public void setResultSetHandlerFactory(
            final ResultSetHandlerFactory resultSetHandlerFactory) {
        this.resultSetHandlerFactory = resultSetHandlerFactory;
    }

    public DaoAnnotationReader getDaoAnnotationReader() {
        return daoAnnotationReader;
    }

    public void setDaoAnnotationReader(
            final DaoAnnotationReader daoAnnotationReader) {
        this.daoAnnotationReader = daoAnnotationReader;
    }

    public void setPagingSQLRewriter(final PagingSqlRewriter pagingSqlRewriter) {
        this.pagingSqlRewriter = pagingSqlRewriter;
    }

    public void setArgumentDtoAnnotationReader(
            final ArgumentDtoAnnotationReader argumentDtoAnnotationReader) {
        this.argumentDtoAnnotationReader = argumentDtoAnnotationReader;
    }

}
