package org.seasar.dao;

/**
 * @author higa
 *
 */
public interface DaoMetaDataFactory {

	public DaoMetaData getDaoMetaData(Class daoClass);
}
