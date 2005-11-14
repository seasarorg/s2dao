package org.seasar.dao.impl;

/**
 * @author higa
 *
 */
public final class RelationKey {

	private Object[] values_;
	private int hashCode_;
	
	public RelationKey(Object[] values) {
		values_ = values;
		for (int i = 0; i < values.length; ++i) {
			hashCode_ += values[i].hashCode();
		}
	}
	
	public Object[] getValues() {
		return values_;
	}
	
	public int hashCode() {
		return hashCode_;
	}

	public boolean equals(Object o) {
		if (!(o instanceof RelationKey)) {
			return false;
		}
		Object[] otherValues = ((RelationKey) o).values_;
		if (values_.length != otherValues.length) {
			return false;
		}
		for (int i = 0; i < values_.length; ++i) {
			if (!values_[i].equals(otherValues[i])) {
				return false;
			}
		}
		return true;
	}
}
