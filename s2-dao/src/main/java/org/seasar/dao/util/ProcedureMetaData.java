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
package org.seasar.dao.util;

/**
 * @author manhole
 */
public class ProcedureMetaData {

    private String procedureCat;

    private String procedureSchem;

    private String procedureName;

    private short procedureType;

    public String getProcedureCat() {
        return procedureCat;
    }

    public void setProcedureCat(String procedureCat) {
        this.procedureCat = procedureCat;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getProcedureSchem() {
        return procedureSchem;
    }

    public void setProcedureSchem(String procedureSchem) {
        this.procedureSchem = procedureSchem;
    }

    public short getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(short procedureType) {
        this.procedureType = procedureType;
    }

}
