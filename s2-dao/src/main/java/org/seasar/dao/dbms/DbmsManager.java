package org.seasar.dao.dbms;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ResourceUtil;

/**
 * @author higa
 *  
 */
public final class DbmsManager {
	
	private static Map dbmses_ = new HashMap();

	static {
		Properties dbmsClassNames = ResourceUtil.getProperties("dbms.properties");
		for (Iterator i = dbmsClassNames.keySet().iterator(); i.hasNext(); ) {
			String productName = (String) i.next();
			Dbms dbms = (Dbms) ClassUtil.newInstance(dbmsClassNames.getProperty(productName));
			dbmses_.put(productName, dbms);
		}
	}
			

	private DbmsManager() {
	}
	
	public static Dbms getDbms(DataSource dataSource) {
		Dbms dbms = null;
		Connection con = DataSourceUtil.getConnection(dataSource);
		try {
			DatabaseMetaData dmd = ConnectionUtil.getMetaData(con);
			dbms = getDbms(dmd);
		} finally {
			ConnectionUtil.close(con);
		}
		return dbms;
	}
	
	public static Dbms getDbms(DatabaseMetaData dmd) {
		return getDbms(DatabaseMetaDataUtil.getDatabaseProductName(dmd));
	}

	public static Dbms getDbms(String productName) {
		Dbms dbms = (Dbms) dbmses_.get(productName);
		if (dbms == null) {
			dbms = (Dbms) dbmses_.get("");
		}
		return dbms;
	}
}