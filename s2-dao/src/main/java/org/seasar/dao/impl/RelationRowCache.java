package org.seasar.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author higa
 *
 */
public class RelationRowCache {

	private List rowMapList_;
	/**
	 * 
	 */
	public RelationRowCache(int size) {
		rowMapList_ = new ArrayList();
		for (int i = 0; i < size; ++i) {
			rowMapList_.add(new HashMap());
		}
	}
	
	public Object getRelationRow(int relno, RelationKey key) {
		return getRowMap(relno).get(key);
	}
	
	public void addRelationRow(int relno, RelationKey key, Object row) {
		getRowMap(relno).put(key, row);
	}

	protected Map getRowMap(int relno) {
		return (Map) rowMapList_.get(relno);
	}
}
