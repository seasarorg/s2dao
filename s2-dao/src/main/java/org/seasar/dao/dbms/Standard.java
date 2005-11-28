package org.seasar.dao.dbms;

import java.util.HashMap;
import java.util.Map;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.RelationPropertyType;

/**
 * @author higa
 *  
 */
public class Standard implements Dbms {

	private Map autoSelectFromClauseCache_ = new HashMap();
	
	/**
	 * @see org.seasar.dao.Dbms#getSuffix()
	 */
	public String getSuffix() {
		return "";
	}

	/**
	 * @see org.seasar.dao.Dbms#getAutoSelectSql(org.seasar.dao.BeanMetaData)
	 */
	public String getAutoSelectSql(BeanMetaData beanMetaData) {
		StringBuffer buf = new StringBuffer(100);
		buf.append(beanMetaData.getAutoSelectList());
		buf.append(" ");
		String beanName = beanMetaData.getBeanClass().getName();
		synchronized (autoSelectFromClauseCache_) {
			String fromClause = (String) autoSelectFromClauseCache_.get(beanName);
			if (fromClause == null) {
				fromClause = createAutoSelectFromClause(beanMetaData);
				autoSelectFromClauseCache_.put(beanName, fromClause);
			}
			buf.append(fromClause);
		}
		return buf.toString();
	}

	protected String createAutoSelectFromClause(BeanMetaData beanMetaData) {
		StringBuffer buf = new StringBuffer(100);
		buf.append("FROM ");
		String myTableName = beanMetaData.getTableName();
		buf.append(myTableName);
		for (int i = 0; i < beanMetaData.getRelationPropertyTypeSize(); ++i) {
			RelationPropertyType rpt = beanMetaData.getRelationPropertyType(i);
			BeanMetaData bmd = rpt.getBeanMetaData();
			buf.append(" LEFT OUTER JOIN ");
			buf.append(bmd.getTableName());
			buf.append(" ");
			String yourAliasName = rpt.getPropertyName();
			buf.append(yourAliasName);
			buf.append(" ON ");
			for (int j = 0; j < rpt.getKeySize(); ++j) {
				buf.append(myTableName);
				buf.append(".");
				buf.append(rpt.getMyKey(j));
				buf.append(" = ");
				buf.append(yourAliasName);
				buf.append(".");
				buf.append(rpt.getYourKey(j));
				buf.append(" AND ");
			}
			buf.setLength(buf.length() - 5);

		}
		return buf.toString();
	}
	
	public String getIdentitySelectString() {
		return null;
	}
	
	public String getSequenceNextValString(String sequenceName) {
		return null;
	}
    
    public boolean isSelfGenerate() {
        return true;
    }
}