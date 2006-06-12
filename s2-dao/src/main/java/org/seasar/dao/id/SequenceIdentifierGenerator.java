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
package org.seasar.dao.id;

import javax.sql.DataSource;

import org.seasar.dao.Dbms;

/**
 * @author higa
 * 
 */
public class SequenceIdentifierGenerator extends AbstractIdentifierGenerator {

    private String sequenceName;

    /**
     * @param propertyName
     * @param dbms
     */
    public SequenceIdentifierGenerator(String propertyName, Dbms dbms) {
        super(propertyName, dbms);
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public void setIdentifier(Object bean, DataSource ds) {
        Object value = executeSql(ds, getDbms().getSequenceNextValString(
                sequenceName), null);
        setIdentifier(bean, value);
    }

    public boolean isSelfGenerate() {
        return getDbms().isSelfGenerate();
    }

}
