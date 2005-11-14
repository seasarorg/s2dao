package org.seasar.dao.impl;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.seasar.dao.BeanMetaData;

public class BeanArrayMetaDataResultSetHandler extends
		BeanListMetaDataResultSetHandler {

	public BeanArrayMetaDataResultSetHandler(BeanMetaData beanMetaData) {
		super(beanMetaData);
	}

	/**
	 * @see org.seasar.extension.jdbc.ResultSetHandler#handle(java.sql.ResultSet)
	 */
	public Object handle(ResultSet rs) throws SQLException {
		List list = (List) super.handle(rs);
		return list.toArray((Object[]) Array.newInstance(getBeanMetaData()
				.getBeanClass(), list.size()));
	}
}