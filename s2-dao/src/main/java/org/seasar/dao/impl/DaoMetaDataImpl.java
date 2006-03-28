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
import org.seasar.dao.SqlCommand;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.dao.handler.MapBasicProcedureHandler;
import org.seasar.dao.handler.ObjectBasicProcedureHandler;
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
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.TextUtil;

/**
 * @author higa
 * 
 */
public class DaoMetaDataImpl implements DaoMetaData {

	private static final Pattern startWithOrderByPattern = 
			Pattern.compile("(/\\*[^*]+\\*/)*order by",Pattern.CASE_INSENSITIVE);
	private static final Pattern startWithSelectPattern	=
			Pattern.compile("^\\s*select\\s",Pattern.CASE_INSENSITIVE);
	
	private static final String NOT_SINGLE_ROW_UPDATED = "NotSingleRowUpdated";

	protected Class daoClass_;

	protected Class daoInterface_;

	protected BeanDesc daoBeanDesc_;

	protected DataSource dataSource_;
	
	protected DaoAnnotationReader annotationReader_;
	
	protected StatementFactory statementFactory_;

	protected ResultSetFactory resultSetFactory_;
	
	protected AnnotationReaderFactory annotationReaderFactory_;

	protected Dbms dbms_;

	protected Class beanClass_;

	protected BeanMetaData beanMetaData_;

	protected Map sqlCommands_ = new HashMap();
    
    protected static String[] daoSuffixes_ = new String[]{"Dao"};
    
    protected static String[] insertPrefixes_ = new String[] { "insert","create", "add" };
    
    protected static String[] updatePrefixes_ = new String[] { "update","modify", "store" };
    
    protected static String[] deletePrefixes_ = new String[] { "delete","remove" };

	public DaoMetaDataImpl(Class daoClass, DataSource dataSource,
			StatementFactory statementFactory,
			ResultSetFactory resultSetFactory) {
		this(daoClass,dataSource,statementFactory,
				resultSetFactory,new FieldAnnotationReaderFactory());
	}
	public DaoMetaDataImpl(Class daoClass, DataSource dataSource,
			StatementFactory statementFactory,
			ResultSetFactory resultSetFactory,
			AnnotationReaderFactory annotationReaderFactory) {
		daoClass_ = daoClass;
		daoBeanDesc_ = BeanDescFactory.getBeanDesc(daoClass);
		daoInterface_ = getDaoInterface(daoClass);
		annotationReaderFactory_ = annotationReaderFactory;
		annotationReader_ = annotationReaderFactory.createDaoAnnotationReader(daoBeanDesc_);
		beanClass_ = annotationReader_.getBeanClass();
		dataSource_ = dataSource;
		statementFactory_ = statementFactory;
		resultSetFactory_ = resultSetFactory;
		Connection con = DataSourceUtil.getConnection(dataSource_);
		try {
			DatabaseMetaData dbMetaData = ConnectionUtil.getMetaData(con);
			dbms_ = DbmsManager.getDbms(dbMetaData);
	
			beanMetaData_ = new BeanMetaDataImpl(beanClass_, dbMetaData, dbms_,
					annotationReaderFactory);
		} finally {
			ConnectionUtil.close(con);
		}
		setupSqlCommand();
	}
    public static void setDaoSuffixes(String[] daoSuffixes_) {
        DaoMetaDataImpl.daoSuffixes_ = daoSuffixes_;
    }
    
    public static void setInsertPrefixes(String[] insertPrefixes_) {
        DaoMetaDataImpl.insertPrefixes_ = insertPrefixes_;
    }
    
    public static void setUpdatePrefixes(String[] updatePrefixes_) {
        DaoMetaDataImpl.updatePrefixes_ = updatePrefixes_;
    }
    
    public static void setDeletePrefixes(String[] deletePrefixes_) {
        DaoMetaDataImpl.deletePrefixes_ = deletePrefixes_;
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
		if(procedureName != null){
			Class returnType = method.getReturnType();
			if(returnType.isAssignableFrom(Map.class)){
				sqlCommands_.put(method.getName(),
					new StaticStoredProcedureCommand(
						new MapBasicProcedureHandler(dataSource_,procedureName)));
			}else{
				sqlCommands_.put(method.getName(),
					new StaticStoredProcedureCommand(
						new ObjectBasicProcedureHandler(dataSource_,procedureName)));				
			}
		}
	}
	
	protected void setupMethodBySqlFile(Class daoInterface, Method method) {
		String base = daoInterface.getName().replace('.', '/') + "_" + method.getName();
		String dbmsPath = base + dbms_.getSuffix() + ".sql";
		String standardPath = base + ".sql";
		if (ResourceUtil.isExist(dbmsPath)) {
			String sql = TextUtil.readText(dbmsPath);
			setupMethodByManual(method, sql);
		} else if (ResourceUtil.isExist(standardPath)) {
			String sql = TextUtil.readText(standardPath);
			setupMethodByManual(method, sql);
		}
	}
	
	protected void setupMethodByInterfaces(Class daoInterface, Method method) {
		Class[] interfaces = daoInterface.getInterfaces();
		if (interfaces == null) {
			return;
		}
		for (int i = 0; i < interfaces.length; i++) {
			Method interfaceMethod = getSameSignatureMethod(interfaces[i], method);
			if (interfaceMethod != null) {
				setupMethod(interfaces[i], interfaceMethod);
			}
		}
	}

	protected void setupMethodBySuperClass(Class daoInterface, Method method) {
		Class superDaoClass = daoInterface.getSuperclass();
		if (superDaoClass != null && !Object.class.equals(superDaoClass)) {
			Method superClassMethod = getSameSignatureMethod(superDaoClass, method);
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

	protected SelectDynamicCommand createSelectDynamicCommand(ResultSetHandler rsh) {
		return new SelectDynamicCommand(dataSource_, statementFactory_, rsh, resultSetFactory_);
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
		if (List.class.isAssignableFrom(method.getReturnType())) {
			return new BeanListMetaDataResultSetHandler(beanMetaData_);
		} else if (isBeanClassAssignable(method.getReturnType())) {
			return new BeanMetaDataResultSetHandler(beanMetaData_);
		} else if (Array.newInstance(beanClass_, 0).getClass()
				.isAssignableFrom(method.getReturnType())) {
			return new BeanArrayMetaDataResultSetHandler(beanMetaData_);
		} else {
			return new ObjectResultSetHandler();
		}
	}
	
	protected boolean isBeanClassAssignable(Class clazz) {
		return beanClass_.isAssignableFrom(clazz) ||
			clazz.isAssignableFrom(beanClass_);
	}

	protected void setupUpdateMethodByManual(Method method, String sql) {
		UpdateDynamicCommand cmd = new UpdateDynamicCommand(dataSource_, statementFactory_);
		cmd.setSql(sql);
		String[] argNames = annotationReader_.getArgNames(method);
		if (argNames.length == 0 && isUpdateSignatureForBean(method)) {
			argNames = new String[] { StringUtil.decapitalize(ClassUtil
					.getShortClassName(beanClass_)) };
		}
		cmd.setArgNames(argNames);
		cmd.setArgTypes(method.getParameterTypes());
		cmd.setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
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
		SqlCommand cmd = null;
		if (isUpdateSignatureForBean(method)) {
			cmd = new InsertAutoStaticCommand(dataSource_, statementFactory_, beanMetaData_,
					propertyNames);
		} else {
			cmd = new InsertBatchAutoStaticCommand(dataSource_, statementFactory_, beanMetaData_,
					propertyNames);
		}
		sqlCommands_.put(method.getName(), cmd);
	}

	protected void setupUpdateMethodByAuto(Method method) {
		checkAutoUpdateMethod(method);
		String[] propertyNames = getPersistentPropertyNames(method);
		AbstractSqlCommand cmd = null;
		if (isUpdateSignatureForBean(method)) {
			cmd = new UpdateAutoStaticCommand(dataSource_, statementFactory_, beanMetaData_,
					propertyNames);
		} else {
			cmd = new UpdateBatchAutoStaticCommand(dataSource_, statementFactory_, beanMetaData_,
					propertyNames);
		}
		sqlCommands_.put(method.getName(), cmd);
	}

	protected void setupDeleteMethodByAuto(Method method) {
		checkAutoUpdateMethod(method);
		String[] propertyNames = getPersistentPropertyNames(method);
		SqlCommand cmd = null;
		if (isUpdateSignatureForBean(method)) {
			cmd = new DeleteAutoStaticCommand(dataSource_, statementFactory_, beanMetaData_,
					propertyNames);
		} else {
			cmd = new DeleteBatchAutoStaticCommand(dataSource_, statementFactory_, beanMetaData_,
					propertyNames);
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
		//TODO ‚Ç‚¤‚·‚é‚©—vŒŸ“¢
		if(dtoClass.isPrimitive()){
			return sql;
		}
		DtoMetaData dmd = new DtoMetaDataImpl(dtoClass,
				annotationReaderFactory_.createBeanAnnotationReader(dtoClass));
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
		return daoClass_;
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

	public static Class getDaoInterface(Class clazz) {
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
}