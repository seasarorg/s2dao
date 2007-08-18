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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.dao.NoPersistentPropertyTypeRuntimeException;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.RelationPropertyTypeFactory;
import org.seasar.dao.TableNaming;
import org.seasar.dao.id.IdentifierGeneratorFactory;
import org.seasar.extension.jdbc.ColumnNotFoundRuntimeException;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.CaseInsensitiveMap;
import org.seasar.framework.util.ClassUtil;

/**
 * @author higa
 * @author manhole
 * @author jflute
 * @author azusa
 */
public class BeanMetaDataImpl extends DtoMetaDataImpl implements BeanMetaData {

    private String tableName;

    private Map propertyTypesByColumnName = new CaseInsensitiveMap();

    private List relationPropertyTypes = new ArrayList();

    private PropertyType[] primaryKeys;

    private String autoSelectList;

    private IdentifierGenerator[] identifierGenerators;

    private Map identifierGeneratorsByPropertyName = new HashMap();

    private String versionNoPropertyName;

    private String timestampPropertyName;

    private Dbms dbms;

    private ModifiedPropertySupport modifiedPropertySupport;

    private TableNaming tableNaming;

    private RelationPropertyTypeFactory relationPropertyTypeFactory;

    public BeanMetaDataImpl() {
    }

    public void initialize() {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(getBeanClass());
        setupTableName(beanDesc);
        setupProperty();
        setupPrimaryKey();
        setupPropertiesByColumnName();
        setupIdentityGeneratorsByPropertyName();
    }

    public void setDbms(Dbms dbms) {
        this.dbms = dbms;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getTableName()
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getVersionNoPropertyType()
     */
    public PropertyType getVersionNoPropertyType()
            throws PropertyNotFoundRuntimeException {

        return getPropertyType(getVersionNoPropertyName());
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getTimestampPropertyType()
     */
    public PropertyType getTimestampPropertyType()
            throws PropertyNotFoundRuntimeException {

        return getPropertyType(getTimestampPropertyName());
    }

    public String getVersionNoPropertyName() {
        return versionNoPropertyName;
    }

    public void setVersionNoPropertyName(String versionNoPropertyName) {
        this.versionNoPropertyName = versionNoPropertyName;
    }

    public String getTimestampPropertyName() {
        return timestampPropertyName;
    }

    public void setTimestampPropertyName(String timestampPropertyName) {
        this.timestampPropertyName = timestampPropertyName;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getPropertyTypeByColumnName(java.lang.String)
     */
    public PropertyType getPropertyTypeByColumnName(String columnName)
            throws ColumnNotFoundRuntimeException {

        PropertyType propertyType = (PropertyType) propertyTypesByColumnName
                .get(columnName);
        if (propertyType == null) {
            throw new ColumnNotFoundRuntimeException(tableName, columnName);
        }
        return propertyType;
    }

    public PropertyType getPropertyTypeByAliasName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return getPropertyTypeByColumnName(alias);
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        RelationPropertyType rpt = getRelationPropertyType(relno);
        if (!rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName)) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        return rpt.getBeanMetaData().getPropertyTypeByColumnName(columnName);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#hasPropertyTypeByColumnName(java.lang.String)
     */
    public boolean hasPropertyTypeByColumnName(String columnName) {
        return propertyTypesByColumnName.get(columnName) != null;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#hasPropertyTypeByAliasName(java.lang.String)
     */
    public boolean hasPropertyTypeByAliasName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return true;
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            return false;
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            return false;
        }
        if (relno >= getRelationPropertyTypeSize()) {
            return false;
        }
        RelationPropertyType rpt = getRelationPropertyType(relno);
        return rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#hasVersionNoPropertyType()
     */
    public boolean hasVersionNoPropertyType() {
        return hasPropertyType(getVersionNoPropertyName());
    }

    /**
     * @see org.seasar.dao.BeanMetaData#hasTimestampPropertyType()
     */
    public boolean hasTimestampPropertyType() {
        return hasPropertyType(getTimestampPropertyName());
    }

    /**
     * @see org.seasar.dao.BeanMetaData#convertFullColumnName(java.lang.String)
     */
    public String convertFullColumnName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return tableName + "." + alias;
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        RelationPropertyType rpt = getRelationPropertyType(relno);
        if (!rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName)) {
            throw new ColumnNotFoundRuntimeException(tableName, alias);
        }
        return rpt.getPropertyName() + "." + columnName;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getRelationPropertyTypeSize()
     */
    public int getRelationPropertyTypeSize() {
        return relationPropertyTypes.size();
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getRelationPropertyType(int)
     */
    public RelationPropertyType getRelationPropertyType(int index) {
        return (RelationPropertyType) relationPropertyTypes.get(index);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getRelationPropertyType(java.lang.String)
     */
    public RelationPropertyType getRelationPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException {

        for (int i = 0; i < getRelationPropertyTypeSize(); i++) {
            RelationPropertyType rpt = (RelationPropertyType) relationPropertyTypes
                    .get(i);
            if (rpt != null
                    && rpt.getPropertyName().equalsIgnoreCase(propertyName)) {
                return rpt;
            }
        }
        throw new PropertyNotFoundRuntimeException(getBeanClass(), propertyName);
    }

    protected void setupTableName(BeanDesc beanDesc) {
        String ta = beanAnnotationReader.getTableAnnotation();
        if (ta != null) {
            tableName = ta;
        } else {
            tableName = tableNaming.fromEntityNameToTableName(ClassUtil
                    .getShortClassName(beanDesc.getBeanClass()));
        }
    }

    protected void setupProperty() {
        PropertyType[] propertyTypes = propertyTypeFactory
                .createPropertyTypes(tableName);
        for (int i = 0; i < propertyTypes.length; i++) {
            PropertyType pt = propertyTypes[i];
            addPropertyType(pt);
        }

        RelationPropertyType[] relationPropertyTypes = relationPropertyTypeFactory
                .createRelationPropertyTypes();
        for (int i = 0; i < relationPropertyTypes.length; i++) {
            RelationPropertyType rpt = relationPropertyTypes[i];
            addRelationPropertyType(rpt);
        }
    }

    protected void setupPrimaryKey() {
        List keys = new ArrayList();
        List generators = new ArrayList();
        for (int i = 0; i < getPropertyTypeSize(); ++i) {
            PropertyType pt = getPropertyType(i);
            if (pt.isPrimaryKey()) {
                keys.add(pt);
                PropertyDesc pd = pt.getPropertyDesc();
                String idType = beanAnnotationReader.getId(pd, dbms);
                IdentifierGenerator generator = IdentifierGeneratorFactory
                        .createIdentifierGenerator(pd.getPropertyName(), dbms,
                                idType);
                generators.add(generator);
            }
        }
        primaryKeys = (PropertyType[]) keys.toArray(new PropertyType[keys
                .size()]);
        identifierGenerators = (IdentifierGenerator[]) generators
                .toArray(new IdentifierGenerator[generators.size()]);
    }

    protected void setupPropertiesByColumnName() {
        for (int i = 0; i < getPropertyTypeSize(); ++i) {
            PropertyType pt = getPropertyType(i);
            propertyTypesByColumnName.put(pt.getColumnName(), pt);
        }
    }

    protected void setupIdentityGeneratorsByPropertyName() {
        for (int i = 0; i < getIdentifierGeneratorSize(); ++i) {
            IdentifierGenerator generator = getIdentifierGenerator(i);
            identifierGeneratorsByPropertyName.put(generator.getPropertyName(),
                    generator);
        }
    }

    protected void addRelationPropertyType(RelationPropertyType rpt) {
        for (int i = relationPropertyTypes.size(); i <= rpt.getRelationNo(); ++i) {
            relationPropertyTypes.add(null);
        }
        relationPropertyTypes.set(rpt.getRelationNo(), rpt);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getPrimaryKeySize()
     */
    public int getPrimaryKeySize() {
        return primaryKeys.length;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getPrimaryKey(int)
     */
    public String getPrimaryKey(int index) {
        return primaryKeys[index].getColumnName();
    }

    public int getIdentifierGeneratorSize() {
        return identifierGenerators.length;
    }

    public IdentifierGenerator getIdentifierGenerator(int index) {
        return identifierGenerators[index];
    }

    public IdentifierGenerator getIdentifierGenerator(String propertyName) {
        return (IdentifierGenerator) identifierGeneratorsByPropertyName
                .get(propertyName);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getAutoSelectList()
     */
    public synchronized String getAutoSelectList() {
        if (autoSelectList != null) {
            return autoSelectList;
        }
        setupAutoSelectList();
        return autoSelectList;
    }

    protected void setupAutoSelectList() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("SELECT ");
        boolean first = true;
        for (int i = 0; i < getPropertyTypeSize(); ++i) {
            PropertyType pt = getPropertyType(i);
            if (pt.isPersistent()) {
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(tableName);
                buf.append(".");
                buf.append(pt.getColumnName());
            }
        }
        for (int i = 0; i < getRelationPropertyTypeSize(); ++i) {
            RelationPropertyType rpt = getRelationPropertyType(i);
            BeanMetaData bmd = rpt.getBeanMetaData();
            for (int j = 0; j < bmd.getPropertyTypeSize(); ++j) {
                PropertyType pt = bmd.getPropertyType(j);
                if (pt.isPersistent()) {
                    if (first) {
                        first = false;
                    } else {
                        buf.append(", ");
                    }
                    final String columnName = pt.getColumnName();
                    buf.append(rpt.getPropertyName());
                    buf.append(".");
                    buf.append(columnName);
                    buf.append(" AS ");
                    buf.append(columnName).append("_").append(
                            rpt.getRelationNo());
                }
            }
        }
        if (first) {
            throw new NoPersistentPropertyTypeRuntimeException();
        }
        autoSelectList = buf.toString();
    }

    public ModifiedPropertySupport getModifiedPropertySupport() {
        return modifiedPropertySupport;
    }

    public void setModifiedPropertySupport(
            final ModifiedPropertySupport propertyModifiedSupport) {
        this.modifiedPropertySupport = propertyModifiedSupport;
    }

    public Set getModifiedPropertyNames(final Object bean) {
        return getModifiedPropertySupport().getModifiedPropertyNames(bean);
    }

    public TableNaming getTableNaming() {
        return tableNaming;
    }

    public void setTableNaming(TableNaming tableNaming) {
        this.tableNaming = tableNaming;
    }

    public void setRelationPropertyTypeFactory(
            RelationPropertyTypeFactory relationPropertyTypeFactory) {
        this.relationPropertyTypeFactory = relationPropertyTypeFactory;
    }

    public static interface ModifiedPropertySupport {

        Set getModifiedPropertyNames(Object bean);

    }

}
