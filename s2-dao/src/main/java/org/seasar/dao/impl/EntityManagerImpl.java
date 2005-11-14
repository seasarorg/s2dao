package org.seasar.dao.impl;

import java.util.List;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.EntityManager;
import org.seasar.dao.SqlCommand;

/**
 * @author higa
 *  
 */
public class EntityManagerImpl implements EntityManager {

	private static Object[] EMPTY_ARGS = new Object[0];

	private DaoMetaData daoMetaData_;

	public EntityManagerImpl(DaoMetaData daoMetaData) {
		daoMetaData_ = daoMetaData;
	}

	public DaoMetaData getDaoMetaData() {
		return daoMetaData_;
	}

	/**
	 * @see org.seasar.dao.EntityManager#find(java.lang.String)
	 */
	public List find(String query) {
		return find(query, EMPTY_ARGS);
	}

	/**
	 * @see org.seasar.dao.EntityManager#find(java.lang.String,
	 *      java.lang.Object)
	 */
	public List find(String query, Object arg1) {
		return find(query, new Object[] { arg1 });
	}

	/**
	 * @see org.seasar.dao.EntityManager#find(java.lang.String,
	 *      java.lang.Object, java.lang.Object)
	 */
	public List find(String query, Object arg1, Object arg2) {
		return find(query, new Object[] { arg1, arg2 });
	}

	/**
	 * @see org.seasar.dao.EntityManager#find(java.lang.String,
	 *      java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public List find(String query, Object arg1, Object arg2, Object arg3) {
		return find(query, new Object[] { arg1, arg2, arg3 });
	}

	/**
	 * @see org.seasar.dao.EntityManager#find(java.lang.String,
	 *      java.lang.Object[])
	 */
	public List find(String query, Object[] args) {
		SqlCommand cmd = daoMetaData_.createFindCommand(query);
		return (List) cmd.execute(args);
	}

	/**
	 * @see org.seasar.dao.EntityManager#findArray(java.lang.String)
	 */
	public Object[] findArray(String query) {
		return findArray(query, EMPTY_ARGS);
	}

	/**
	 * @see org.seasar.dao.EntityManager#findArray(java.lang.String,
	 *      java.lang.Object)
	 */
	public Object[] findArray(String query, Object arg1) {
		return findArray(query, new Object[] { arg1 });
	}

	/**
	 * @see org.seasar.dao.EntityManager#findArray(java.lang.String,
	 *      java.lang.Object, java.lang.Object)
	 */
	public Object[] findArray(String query, Object arg1, Object arg2) {
		return findArray(query, new Object[] { arg1, arg2 });
	}

	/**
	 * @see org.seasar.dao.EntityManager#findArray(java.lang.String,
	 *      java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public Object[] findArray(String query, Object arg1, Object arg2,
			Object arg3) {

		return findArray(query, new Object[] { arg1, arg2, arg3 });
	}

	/**
	 * @see org.seasar.dao.EntityManager#findArray(java.lang.String,
	 *      java.lang.Object[])
	 */
	public Object[] findArray(String query, Object[] args) {
		SqlCommand cmd = daoMetaData_.createFindArrayCommand(query);
		return (Object[]) cmd.execute(args);
	}

	/**
	 * @see org.seasar.dao.EntityManager#findBean(java.lang.String)
	 */
	public Object findBean(String query) {
		return findBean(query, EMPTY_ARGS);
	}

	/**
	 * @see org.seasar.dao.EntityManager#findBean(java.lang.String,
	 *      java.lang.Object)
	 */
	public Object findBean(String query, Object arg1) {
		return findBean(query, new Object[] { arg1 });
	}

	/**
	 * @see org.seasar.dao.EntityManager#findBean(java.lang.String,
	 *      java.lang.Object, java.lang.Object)
	 */
	public Object findBean(String query, Object arg1, Object arg2) {
		return findBean(query, new Object[] { arg1, arg2 });
	}

	/**
	 * @see org.seasar.dao.EntityManager#findBean(java.lang.String,
	 *      java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public Object findBean(String query, Object arg1, Object arg2, Object arg3) {
		return findBean(query, new Object[] { arg1, arg2, arg3 });
	}

	/**
	 * @see org.seasar.dao.EntityManager#findBean(java.lang.String,
	 *      java.lang.Object[])
	 */
	public Object findBean(String query, Object[] args) {
		SqlCommand cmd = daoMetaData_.createFindBeanCommand(query);
		return cmd.execute(args);
	}

	/*
	 * 
	 * @see org.seasar.dao.EntityManager#findObject(java.lang.String)
	 */
	public Object findObject(String query) {
		return findObject(query, EMPTY_ARGS);
	}

	/*
	 * 
	 * @see org.seasar.dao.EntityManager#findObject(java.lang.String,
	 *      java.lang.Object)
	 */
	public Object findObject(String query, Object arg1) {
		return findObject(query, new Object[]{arg1});
	}

	/*
	 * 
	 * @see org.seasar.dao.EntityManager#findObject(java.lang.String,
	 *      java.lang.Object, java.lang.Object)
	 */
	public Object findObject(String query, Object arg1, Object arg2) {
		return findObject(query, new Object[]{arg1, arg2});
	}

	/*
	 * 
	 * @see org.seasar.dao.EntityManager#findObject(java.lang.String,
	 *      java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public Object findObject(String query, Object arg1, Object arg2, Object arg3) {
		return findObject(query, new Object[]{arg1, arg2, arg3});
	}

	/*
	 * 
	 * @see org.seasar.dao.EntityManager#findObject(java.lang.String,
	 *      java.lang.Object[])
	 */
	public Object findObject(String query, Object[] args) {
		SqlCommand cmd = daoMetaData_.createFindObjectCommand(query);
		return cmd.execute(args);
	}
}