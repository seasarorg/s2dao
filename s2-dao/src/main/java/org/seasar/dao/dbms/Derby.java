package org.seasar.dao.dbms;


/**
 * @author higa
 *
 */
public class Derby extends Standard {

	/**
	 * @see org.seasar.dao.Dbms#getSuffix()
	 */
	public String getSuffix() {
		return "_derby";
	}
	
	public String getIdentitySelectString() {
		return "values IDENTITY_VAL_LOCAL()";
	}

    public String getSequenceNextValString(String sequenceName) {
        return "values IDENTITY_VAL_LOCAL()";
    }

    public boolean isSelfGenerate() {
        return false;
    }

}
