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
