package org.seasar.dao.handler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.StatementUtil;

public class MapBasicProcedureHandler extends AbstractBasicProcedureHandler {

	public MapBasicProcedureHandler(DataSource ds, String procedureName) {
		this(ds, procedureName, BasicStatementFactory.INSTANCE);
	}

	public MapBasicProcedureHandler(DataSource ds, String procedureName, StatementFactory statementFactory) {
		setDataSource(ds);
		setProcedureName(procedureName);
		setStatementFactory(statementFactory);
		initTypes();
	}
	protected Object execute(Connection connection, Object[] args){
		CallableStatement cs = null;
		try {
			cs = prepareCallableStatement(connection);
			bindArgs(cs, args);
			cs.execute();
			Map result = new HashMap();
			for (int i = 0; i < columnInOutTypes_.length; i++) {
				if(isOutputColum(columnInOutTypes_[i].intValue())){
					result.put(columnNames_[i],cs.getObject(i+1));
				}
			}
			return result;
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			StatementUtil.close(cs);
		}
	}
}
