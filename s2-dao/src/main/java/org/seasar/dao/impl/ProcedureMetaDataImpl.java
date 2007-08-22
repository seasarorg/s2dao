/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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

import java.util.ArrayList;
import java.util.List;

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureParameterType;

/**
 * @author taedium
 *
 */
public class ProcedureMetaDataImpl implements ProcedureMetaData {

    private String procedureName;

    private List parameterTypes = new ArrayList();

    private List inParameterTypes = new ArrayList();

    private List inOutParameterTypes = new ArrayList();

    private List outParameterTypes = new ArrayList();

    private ProcedureParameterType returnParameterType;

    public ProcedureMetaDataImpl(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public int getParameterTypeSize() {
        return parameterTypes.size();
    }

    public ProcedureParameterType getParameterType(int index) {
        return (ProcedureParameterType) parameterTypes.get(index);
    }

    public ProcedureParameterType getInOutParameterType(int index) {
        return (ProcedureParameterType) inOutParameterTypes.get(index);
    }

    public int getInOutParameterTypeSize() {
        return inOutParameterTypes.size();
    }

    public ProcedureParameterType getInParameterType(int index) {
        return (ProcedureParameterType) inParameterTypes.get(index);
    }

    public int getInParameterTypeSize() {
        return inParameterTypes.size();
    }

    public ProcedureParameterType getOutParameterType(int index) {
        return (ProcedureParameterType) outParameterTypes.get(index);
    }

    public int getOutParameterTypeSize() {
        return outParameterTypes.size();
    }

    public boolean hasReturnParameterType() {
        return returnParameterType != null;
    }

    public ProcedureParameterType getReturnParameterType() {
        return returnParameterType;
    }

    public void addInParameterType(ProcedureParameterType inParameterType) {
        parameterTypes.add(inParameterType);
        inParameterTypes.add(inParameterType);
    }

    public void addInOutParameterType(ProcedureParameterType inOutParameterType) {
        parameterTypes.add(inOutParameterType);
        inOutParameterTypes.add(inOutParameterType);
    }

    public void addOutParameterType(ProcedureParameterType outParameterType) {
        parameterTypes.add(outParameterType);
        outParameterTypes.add(outParameterType);
    }

    public void setReturnParameterType(
            ProcedureParameterType returnParameterType) {
        parameterTypes.add(returnParameterType);
        this.returnParameterType = returnParameterType;
    }

}
