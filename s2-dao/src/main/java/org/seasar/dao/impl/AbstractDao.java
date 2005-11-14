package org.seasar.dao.impl;

import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.EntityManager;

/**
 * @author higa
 *
 */
public abstract class AbstractDao {

	private EntityManager entityManager_;
	
	public AbstractDao(DaoMetaDataFactory daoMetaDataFactory) {
		entityManager_ = new EntityManagerImpl(daoMetaDataFactory.getDaoMetaData(getClass()));
	}

	public EntityManager getEntityManager() {
		return entityManager_;
	}
}
