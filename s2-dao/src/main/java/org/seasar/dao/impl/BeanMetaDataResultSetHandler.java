package org.seasar.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.RelationPropertyType;
import org.seasar.framework.beans.PropertyDesc;

public class BeanMetaDataResultSetHandler extends
		AbstractBeanMetaDataResultSetHandler {

	public BeanMetaDataResultSetHandler(BeanMetaData beanMetaData) {
		super(beanMetaData);
	}

	/**
	 * @see org.seasar.extension.jdbc.ResultSetHandler#handle(java.sql.ResultSet)
	 */
	public Object handle(ResultSet resultSet) throws SQLException {
		if (resultSet.next()) {
			Set columnNames = createColumnNames(resultSet.getMetaData());
			Object row = createRow(resultSet, columnNames);
			for (int i = 0; i < getBeanMetaData().getRelationPropertyTypeSize(); ++i) {
				RelationPropertyType rpt = getBeanMetaData()
						.getRelationPropertyType(i);
				if (rpt == null) {
					continue;
				}
				Object relationRow = createRelationRow(resultSet, rpt,
						columnNames, null);
				if (relationRow != null) {
					PropertyDesc pd = rpt.getPropertyDesc();
					pd.setValue(row, relationRow);
				}
			}
			return row;
		} else {
			return null;
		}
	}
}