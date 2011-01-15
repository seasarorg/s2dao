/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dao.id;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;
import org.seasar.extension.jdbc.PropertyType;

/**
 * @author higa
 * 
 */
public class AssignedIdentifierGenerator extends AbstractIdentifierGenerator {

    public AssignedIdentifierGenerator(PropertyType propertyType, Dbms dbms) {
        super(propertyType, dbms);
    }

    /**
     * @see org.seasar.dao.IdentifierGenerator#setIdentifier(java.lang.Object,
     *      javax.sql.DataSource)
     */
    public void setIdentifier(Object bean, DataSource ds) {
    }

    /**
     * @see org.seasar.dao.IdentifierGenerator#isSelfGenerate()
     */
    public boolean isSelfGenerate() {
        return true;
    }
}