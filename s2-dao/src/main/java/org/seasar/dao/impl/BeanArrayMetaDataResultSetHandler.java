/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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