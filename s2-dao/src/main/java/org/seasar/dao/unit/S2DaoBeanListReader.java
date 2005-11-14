package org.seasar.dao.unit;

import java.sql.DatabaseMetaData;
import java.util.List;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.dao.impl.BeanMetaDataImpl;

/**
 * @author higa
 *  
 */
public class S2DaoBeanListReader extends S2DaoBeanReader {

	/**
	 * @param map
	 */
	public S2DaoBeanListReader(List list, DatabaseMetaData dbMetaData) {
		Dbms dbms = DbmsManager.getDbms(dbMetaData);
		BeanMetaData beanMetaData = new BeanMetaDataImpl(
				list.get(0).getClass(), dbMetaData, dbms);
		setupColumns(beanMetaData);
		for (int i = 0; i < list.size(); ++i) {
			setupRow(beanMetaData, list.get(i));
		}
	}

}