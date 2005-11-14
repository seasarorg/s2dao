package org.seasar.dao;

import org.seasar.framework.beans.PropertyDesc;

/**
 * @author uehara keizou
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface BeanAnnotationReader {

	String getColumnAnnotation(PropertyDesc pd);

	String getTableAnnotation();

	String getVersionNoProteryNameAnnotation();

	String getTimestampPropertyName();

	String getId(PropertyDesc pd);

	String[] getNoPersisteneProps();

	boolean hasRelationNo(PropertyDesc pd);

	int getRelationNo(PropertyDesc pd);

	String getRelationKey(PropertyDesc pd);

}
