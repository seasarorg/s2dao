package org.seasar.dao.impl;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.DaoMetaData;
import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.ResultSetFactory;

/**
 * @author higa
 *  
 */
public class DaoMetaDataFactoryImpl implements DaoMetaDataFactory {

	protected Map daoMetaDataCache_ = new HashMap();

	protected DataSource dataSource_;
	
	protected StatementFactory statementFactory_;

	protected ResultSetFactory resultSetFactory_;
	
	protected AnnotationReaderFactory readerFactory_;

	public DaoMetaDataFactoryImpl(DataSource dataSource,
			StatementFactory statementFactory,
			ResultSetFactory resultSetFactory,
			AnnotationReaderFactory readerFactory) {
		
		dataSource_ = dataSource;
		statementFactory_ = statementFactory;
		resultSetFactory_ = resultSetFactory;
		readerFactory_ = readerFactory;
	}

	public synchronized DaoMetaData getDaoMetaData(Class daoClass) {
		String key = daoClass.getName();
		DaoMetaData dmd = (DaoMetaData) daoMetaDataCache_.get(key);
		if (dmd != null) {
			return dmd;
		}
		dmd = new DaoMetaDataImpl(daoClass, dataSource_,
				statementFactory_, resultSetFactory_,readerFactory_);
		daoMetaDataCache_.put(key, dmd);
		return dmd;
	}

}
