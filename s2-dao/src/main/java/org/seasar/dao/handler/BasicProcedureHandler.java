/*
 * Copyright 2004-2005 the Seasar Foundation and the Others.
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
package org.seasar.dao.handler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.seasar.framework.exception.EmptyRuntimeException;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.exception.SRuntimeException;
import org.seasar.framework.util.ResultSetUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * @author higa
 *  
 */
public class BasicProcedureHandler implements ProcedureHandler{
	private boolean initialised = false;
	
	private int returnColumnNum_ = -1;
	
	private Integer[] columnTypes_;
	
	private DataSource dataSource_;

	private String procedureName_;
	
	private String sql_;

	private StatementFactory statementFactory_ = BasicStatementFactory.INSTANCE;

	public BasicProcedureHandler() {
	}

	public BasicProcedureHandler(DataSource ds, String procedureName) {
		this(ds, procedureName, BasicStatementFactory.INSTANCE);
	}

	public BasicProcedureHandler(DataSource ds, String procedureName,
			StatementFactory statementFactory) {

		setDataSource(ds);
		setProcedureName(procedureName);
		setStatementFactory(statementFactory);
	}

	public DataSource getDataSource() {
		return dataSource_;
	}

	public void setDataSource(DataSource dataSource) {
		dataSource_ = dataSource;
	}
	public String getProcedureName() {
		return procedureName_;
	}
	public void setProcedureName(String procedureName) {
		this.procedureName_ = procedureName;
	}

	public StatementFactory getStatementFactory() {
		return statementFactory_;
	}

	public void setStatementFactory(StatementFactory statementFactory) {
		statementFactory_ = statementFactory;
	}

	protected Connection getConnection() {
		if (dataSource_ == null) {
			throw new EmptyRuntimeException("dataSource");
		}
		return DataSourceUtil.getConnection(dataSource_);
	}

	protected CallableStatement prepareCallableStatement(Connection connection) {
		if (sql_ == null) {
			throw new EmptyRuntimeException("sql");
		}
		return statementFactory_.createCallableStatement(connection,
				sql_);
	}
	public Object execute(Object[] args) throws SQLRuntimeException {
		Connection connection = getConnection();
		try {
			return execute(connection, args);
		} finally {
			ConnectionUtil.close(connection);
		}
	}
	protected void initTypes(Connection connection) throws SQLException{
		StringBuffer buff = new StringBuffer();
		buff.append("{ call ");
		buff.append(procedureName_);
		buff.append("(");
		ArrayList dataType = new ArrayList();
		DatabaseMetaData dmd = ConnectionUtil.getMetaData(connection);
		ResultSet rs = null;
		try{
			rs = dmd.getProcedureColumns(null,null,procedureName_,null);
			int pos = 1;
			while(rs.next()){
				int columnType = rs.getInt(5);
				if(columnType == DatabaseMetaData.procedureColumnReturn 
						){
					if(returnColumnNum_>0){
						throw new SRuntimeException("EDAO0010");
					}
					returnColumnNum_ = pos;
					buff.setLength(0);
					buff.append("{? = call ");
					buff.append(procedureName_);
					buff.append("(");
				}else if(columnType == DatabaseMetaData.procedureColumnIn ||
						columnType == DatabaseMetaData.procedureColumnOut){
					buff.append("?,");
				}else{
					throw new SRuntimeException("EDAO0010",new Object[]{procedureName_});
				}
				dataType.add(rs.getObject(6));
				pos++;
			}			
		}finally{
			ResultSetUtil.close(rs);
		}
		buff.setLength(buff.length() - 1 );
		buff.append(")}");
		sql_ = buff.toString();
		columnTypes_ = (Integer[]) 
			dataType.toArray(new Integer[dataType.size()]);
	}
	protected Object execute(Connection connection, Object[] args) {

		CallableStatement cs = null;
		try {
			if(!initialised){
				initTypes(connection);
				initialised = true;
			}
			cs = prepareCallableStatement(connection);
			bindArgs(cs, args);
			cs.execute();
			return (returnColumnNum_>0)?cs.getObject(returnColumnNum_):null;
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			StatementUtil.close(cs);
		}
	}

	protected void bindArgs(CallableStatement ps, Object[] args
			) throws SQLException {

		if (args == null) {
			return;
		}
		int argPos = 0;
		for (int i = 0; i < columnTypes_.length;i++) {
			if(returnColumnNum_ == i + 1){
				ps.registerOutParameter(i+1,columnTypes_[i].intValue());
			}else{
				ps.setObject(i+1,args[argPos++],columnTypes_[i].intValue());
			}
		}
	}

	protected String getCompleteSql(Object[] args) {
		if (args == null || args.length == 0) {
			return sql_;
		}
		StringBuffer buf = new StringBuffer(200);
		int pos = 0;
		int pos2 = 0;
		int index = 0;
		while (true) {
			pos = sql_.indexOf('?', pos2);
			if (pos > 0) {
				buf.append(sql_.substring(pos2, pos));
				buf.append(getBindVariableText(args[index++]));
				pos2 = pos + 1;
			} else {
				buf.append(sql_.substring(pos2));
				break;
			}
		}
		return buf.toString();
	}

	protected String getBindVariableText(Object bindVariable) {
		if (bindVariable instanceof String) {
			return "'" + bindVariable + "'";
		} else if (bindVariable instanceof Number) {
			return bindVariable.toString();
		} else if (bindVariable instanceof Timestamp) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
			return "'" + sdf.format((java.util.Date) bindVariable) + "'";
		} else if (bindVariable instanceof java.util.Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return "'" + sdf.format((java.util.Date) bindVariable) + "'";
		} else if (bindVariable instanceof Boolean) {
			return bindVariable.toString();
		} else if (bindVariable == null) {
			return "null";
		} else {
			return "'" + bindVariable.toString() + "'";
		}
	}
    
    protected ValueType getValueType(Class clazz) {
        return ValueTypes.getValueType(clazz);
    }
}