package org.seasar.dao.handler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.exception.SRuntimeException;
import org.seasar.framework.util.StatementUtil;

public class ObjectBasicProcedureHandler extends AbstractBasicProcedureHandler {

	public ObjectBasicProcedureHandler(DataSource ds, String procedureName) {
		this(ds, procedureName, BasicStatementFactory.INSTANCE);
	}

	public ObjectBasicProcedureHandler(DataSource ds, String procedureName, StatementFactory statementFactory) {
		setDataSource(ds);
		setProcedureName(procedureName);
		setStatementFactory(statementFactory);
		if(initTypes()>1){
			throw new SRuntimeException("EDAO0010");
		}
	}
	protected Object execute(Connection connection, Object[] args){
		CallableStatement cs = null;
		try {
			cs = prepareCallableStatement(connection);
			bindArgs(cs, args);
			cs.execute();
			for (int i = 0; i < columnInOutTypes_.length; i++) {
				if(isOutputColum(columnInOutTypes_[i].intValue())){
					return cs.getObject(i+1);
				}
			}
			return null;
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			StatementUtil.close(cs);
		}
	}
}
