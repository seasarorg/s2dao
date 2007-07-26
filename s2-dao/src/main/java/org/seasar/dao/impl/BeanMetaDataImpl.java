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

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.BeanMetaDataFactory;
import org.seasar.dao.Dbms;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.dao.NoPersistentPropertyTypeRuntimeException;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.TableNaming;
import org.seasar.dao.id.IdentifierGeneratorFactory;
import org.seasar.extension.jdbc.ColumnNotFoundRuntimeException;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.beans.impl.PropertyDescImpl;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.CaseInsensitiveMap;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author higa
 * @author manhole
 * @author jflute
 * @author azusa
 */
public class BeanMetaDataImpl extends DtoMetaDataImpl implements BeanMetaData {

    private static Logger logger = Logger.getLogger(BeanMetaDataImpl.class);

    private String tableName;

    private Map propertyTypesByColumnName = new CaseInsensitiveMap();

    private List relationPropertyTypes = new ArrayList();

    private PropertyType[] primaryKeys = new PropertyType[0];

    private String autoSelectList;

    private boolean isStopRelationCreation;

    private IdentifierGenerator identifierGenerator;

    private String versionNoPropertyName;

    private String timestampPropertyName;

    private Dbms dbms;

    private DatabaseMetaData databaseMetaData;

    private BeanMetaDataFactory beanMetaDataFactory;

    private int relationNestLevel;

    private ModifiedPropertySupport modifiedPropertySupport;

    private TableNaming tableNaming;

    public BeanMetaDataImpl() {
    }

    public void initialize() {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(getBeanClass());
        setupTableName(beanDesc);
        setupProperty(beanDesc, databaseMetaData, dbms);
        setupDatabaseMetaData(beanDesc, databaseMetaData, dbms);
        setupPropertiesByColumnName();
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

    protected void setupProperty(BeanDesc beanDesc,
            DatabaseMetaData dbMetaData, Dbms dbms) {

        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            PropertyType pt = null;
            if (beanAnnotationReader.hasRelationNo(pd)) {
                if (!isStopRelationCreation) {
                    RelationPropertyType rpt = createRelationPropertyType(
                            beanDesc, pd, dbMetaData, dbms);
                    addRelationPropertyType(rpt);
                }
            } else {
                pt = createPropertyType(beanDesc, pd);
                addPropertyType(pt);
            }
            if (identifierGenerator == null) {
                String idAnnotation = beanAnnotationReader.getId(pd, dbms);
                if (idAnnotation != null) {
                    identifierGenerator = IdentifierGeneratorFactory
                            .createIdentifierGenerator(pd.getPropertyName(),
                                    dbms, idAnnotation);
                    primaryKeys = new PropertyType[] { pt };
                    pt.setPrimaryKey(true);
                }
            }
        }
    }

    protected void setupDatabaseMetaData(BeanDesc beanDesc,
            DatabaseMetaData dbMetaData, Dbms dbms) {
        setupPropertyPersistentAndColumnName(beanDesc, dbMetaData);
        setupPrimaryKey(dbMetaData, dbms);
    }

    protected void setupPrimaryKey(DatabaseMetaData dbMetaData, Dbms dbms) {
        if (identifierGenerator == null) {
            List pkeyList = new ArrayList();
            Set primaryKeySet = DatabaseMetaDataUtil.getPrimaryKeySet(
                    dbMetaData, tableName);
            for (int i = 0; i < getPropertyTypeSize(); ++i) {
                PropertyType pt = getPropertyType(i);
                if (primaryKeySet.contains(pt.getColumnName())) {
                    pt.setPrimaryKey(true);
                    pkeyList.add(pt);
                } else {
                    pt.setPrimaryKey(false);
                }
            }
            primaryKeys = (PropertyType[]) pkeyList
                    .toArray(new PropertyType[pkeyList.size()]);
            identifierGenerator = IdentifierGeneratorFactory
                    .createIdentifierGenerator(null, dbms);
        }
    }

    protected void setupPropertyPersistentAndColumnName(BeanDesc beanDesc,
            DatabaseMetaData dbMetaData) {

        Set columnSet = DatabaseMetaDataUtil
                .getColumnMap(dbMetaData, tableName).keySet();
        if (columnSet.isEmpty()) {
            logger.log("WDAO0002", new Object[] { tableName });
        }
        for (Iterator i = columnSet.iterator(); i.hasNext();) {

            String columnName = (String) i.next();
            String columnName2 = StringUtil.replace(columnName, "_", "");
            for (int j = 0; j < getPropertyTypeSize(); ++j) {
                PropertyType pt = getPropertyType(j);
                if (pt.getColumnName().equalsIgnoreCase(columnName2)) {
                    final PropertyDesc pd = pt.getPropertyDesc();
                    if (beanAnnotationReader.getColumnAnnotation(pd) == null) {
                        pt.setColumnName(columnName);
                    }
                    break;
                }
            }
        }
        String[] props = beanAnnotationReader.getNoPersisteneProps();
        if (props != null) {
            for (int i = 0; i < props.length; ++i) {
                PropertyType pt = getPropertyType(props[i].trim());
                pt.setPersistent(false);
            }
        }
        for (int i = 0; i < getPropertyTypeSize(); ++i) {
            PropertyType pt = getPropertyType(i);
            if (!columnSet.contains(pt.getColumnName())) {
                pt.setPersistent(false);
            }
        }
    }

    protected void setupPropertiesByColumnName() {
        for (int i = 0; i < getPropertyTypeSize(); ++i) {
            PropertyType pt = getPropertyType(i);
            propertyTypesByColumnName.put(pt.getColumnName(), pt);
        }
    }

    protected RelationPropertyType createRelationPropertyType(
            BeanDesc beanDesc, PropertyDesc propertyDesc,
            DatabaseMetaData dbMetaData, Dbms dbms) {

        String[] myKeys = new String[0];
        String[] yourKeys = new String[0];
        int relno = beanAnnotationReader.getRelationNo(propertyDesc);
        String relkeys = beanAnnotationReader.getRelationKey(propertyDesc);
        if (relkeys != null) {
            StringTokenizer st = new StringTokenizer(relkeys, " \t\n\r\f,");
            List myKeyList = new ArrayList();
            List yourKeyList = new ArrayList();
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                int index = token.indexOf(':');
                if (index > 0) {
                    myKeyList.add(token.substring(0, index));
                    yourKeyList.add(token.substring(index + 1));
                } else {
                    myKeyList.add(token);
                    yourKeyList.add(token);
                }
            }
            myKeys = (String[]) myKeyList.toArray(new String[myKeyList.size()]);
            yourKeys = (String[]) yourKeyList.toArray(new String[yourKeyList
                    .size()]);
        }
        final BeanMetaData beanMetaData = createRelationBeanMetaData(propertyDesc
                .getPropertyType());
        final PropertyDescImpl enhancedPd = new PropertyDescImpl(propertyDesc
                .getPropertyName(), beanMetaData.getBeanClass(), propertyDesc
                .getReadMethod(), propertyDesc.getWriteMethod(), beanDesc);
        final RelationPropertyType rpt = new RelationPropertyTypeImpl(
                enhancedPd, relno, myKeys, yourKeys, beanMetaData);
        return rpt;
    }

    protected BeanMetaData createRelationBeanMetaData(
            final Class relationBeanClass) {
        return beanMetaDataFactory.createBeanMetaData(databaseMetaData,
                relationBeanClass, relationNestLevel + 1);
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

    public IdentifierGenerator getIdentifierGenerator() {
        return identifierGenerator;
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

    /**
     * @see org.seasar.dao.BeanMetaData#isStopRelationCreation()
     */
    public boolean isStopRelationCreation() {
        return isStopRelationCreation;
    }

    public void setStopRelationCreation(boolean isStopRelationCreation) {
        this.isStopRelationCreation = isStopRelationCreation;
    }

    public void setDatabaseMetaData(DatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }

    public void setBeanMetaDataFactory(BeanMetaDataFactory beanMetaDataFactory) {
        this.beanMetaDataFactory = beanMetaDataFactory;
    }

    public void setRelationNestLevel(int relationNestLevel) {
        this.relationNestLevel = relationNestLevel;
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

    public static interface ModifiedPropertySupport {

        Set getModifiedPropertyNames(Object bean);

    }

    public TableNaming getTableNaming() {
        return tableNaming;
    }

    public void setTableNaming(TableNaming tableNaming) {
        this.tableNaming = tableNaming;
    }

}
