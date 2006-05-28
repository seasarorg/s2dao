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

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.id.IdentifierGeneratorFactory;
import org.seasar.extension.jdbc.ColumnNotFoundRuntimeException;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.PropertyNotFoundRuntimeException;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.CaseInsensitiveMap;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.StringUtil;

/**
 * @author higa
 * 
 */
public class BeanMetaDataImpl extends DtoMetaDataImpl implements BeanMetaData {

    private static Logger logger_ = Logger.getLogger(BeanMetaDataImpl.class);

    private String tableName_;

    private Map propertyTypesByColumnName_ = new CaseInsensitiveMap();

    private List relationPropertyTypes_ = new ArrayList();

    private String[] primaryKeys_ = new String[0];

    private String autoSelectList_;

    private boolean relation_;

    private IdentifierGenerator identifierGenerator_;

    private String versionNoPropertyName_ = "versionNo";

    private String timestampPropertyName_ = "timestamp";

    private AnnotationReaderFactory annotationReaderFactory_;

    private Dbms dbms_;

    private DatabaseMetaData databaseMetaData_;

    public BeanMetaDataImpl() {
    }

    /**
     * @deprecated
     */
    public BeanMetaDataImpl(Class beanClass, DatabaseMetaData dbMetaData,
            Dbms dbms) {
        this(beanClass, dbMetaData, dbms, new FieldAnnotationReaderFactory(),
                false);
    }

    /**
     * @deprecated
     */
    public BeanMetaDataImpl(Class beanClass, DatabaseMetaData dbMetaData,
            Dbms dbms, AnnotationReaderFactory annotationReaderFactory) {
        this(beanClass, dbMetaData, dbms, annotationReaderFactory, false);
    }

    /**
     * @deprecated
     */
    public BeanMetaDataImpl(Class beanClass, DatabaseMetaData dbMetaData,
            Dbms dbms, AnnotationReaderFactory annotationReaderFactory,
            boolean relation) {
        setBeanClass(beanClass);
        setAnnotationReaderFactory(annotationReaderFactory);
        setRelation(relation);
        setValueTypeFactory(new ValueTypeFactoryImpl());
        initialize();
    }

    protected AnnotationReaderFactory getAnnotationReaderFactory() {
        return annotationReaderFactory_;
    }

    public void setAnnotationReaderFactory(
            AnnotationReaderFactory annotationReaderFactory) {
        annotationReaderFactory_ = annotationReaderFactory;
    }

    public void initialize() {
        beanAnnotationReader_ = getAnnotationReaderFactory()
                .createBeanAnnotationReader(getBeanClass());
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(getBeanClass());
        setupTableName(beanDesc);
        setupVersionNoPropertyName(beanDesc);
        setupTimestampPropertyName(beanDesc);
        setupProperty(beanDesc, databaseMetaData_, dbms_);
        setupDatabaseMetaData(beanDesc, databaseMetaData_, dbms_);
        setupPropertiesByColumnName();
    }

    public void setDbms(Dbms dbms) {
        dbms_ = dbms;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getTableName()
     */
    public String getTableName() {
        return tableName_;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getVersionNoPropertyType()
     */
    public PropertyType getVersionNoPropertyType()
            throws PropertyNotFoundRuntimeException {

        return getPropertyType(versionNoPropertyName_);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getTimestampPropertyType()
     */
    public PropertyType getTimestampPropertyType()
            throws PropertyNotFoundRuntimeException {

        return getPropertyType(timestampPropertyName_);
    }

    public String getVersionNoPropertyName() {
        return versionNoPropertyName_;
    }

    public String getTimestampPropertyName() {
        return timestampPropertyName_;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getPropertyTypeByColumnName(java.lang.String)
     */
    public PropertyType getPropertyTypeByColumnName(String columnName)
            throws ColumnNotFoundRuntimeException {

        PropertyType propertyType = (PropertyType) propertyTypesByColumnName_
                .get(columnName);
        if (propertyType == null) {
            throw new ColumnNotFoundRuntimeException(tableName_, columnName);
        }
        return propertyType;
    }

    public PropertyType getPropertyTypeByAliasName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return getPropertyTypeByColumnName(alias);
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            throw new ColumnNotFoundRuntimeException(tableName_, alias);
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            throw new ColumnNotFoundRuntimeException(tableName_, alias);
        }
        RelationPropertyType rpt = getRelationPropertyType(relno);
        if (!rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName)) {
            throw new ColumnNotFoundRuntimeException(tableName_, alias);
        }
        return rpt.getBeanMetaData().getPropertyTypeByColumnName(columnName);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#hasPropertyTypeByColumnName(java.lang.String)
     */
    public boolean hasPropertyTypeByColumnName(String columnName) {
        return propertyTypesByColumnName_.get(columnName) != null;
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
        return hasPropertyType(versionNoPropertyName_);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#hasTimestampPropertyType()
     */
    public boolean hasTimestampPropertyType() {
        return hasPropertyType(timestampPropertyName_);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#convertFullColumnName(java.lang.String)
     */
    public String convertFullColumnName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return tableName_ + "." + alias;
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            throw new ColumnNotFoundRuntimeException(tableName_, alias);
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            throw new ColumnNotFoundRuntimeException(tableName_, alias);
        }
        RelationPropertyType rpt = getRelationPropertyType(relno);
        if (!rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName)) {
            throw new ColumnNotFoundRuntimeException(tableName_, alias);
        }
        return rpt.getPropertyName() + "." + columnName;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getRelationPropertyTypeSize()
     */
    public int getRelationPropertyTypeSize() {
        return relationPropertyTypes_.size();
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getRelationPropertyType(int)
     */
    public RelationPropertyType getRelationPropertyType(int index) {
        return (RelationPropertyType) relationPropertyTypes_.get(index);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getRelationPropertyType(java.lang.String)
     */
    public RelationPropertyType getRelationPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException {

        for (int i = 0; i < getRelationPropertyTypeSize(); i++) {
            RelationPropertyType rpt = (RelationPropertyType) relationPropertyTypes_
                    .get(i);
            if (rpt != null
                    && rpt.getPropertyName().equalsIgnoreCase(propertyName)) {
                return rpt;
            }
        }
        throw new PropertyNotFoundRuntimeException(getBeanClass(), propertyName);
    }

    protected void setupTableName(BeanDesc beanDesc) {
        String ta = beanAnnotationReader_.getTableAnnotation();
        if (ta != null) {
            tableName_ = ta;
        } else {
            tableName_ = ClassUtil.getShortClassName(getBeanClass());
        }
    }

    protected void setupVersionNoPropertyName(BeanDesc beanDesc) {
        String vna = beanAnnotationReader_.getVersionNoProteryNameAnnotation();
        if (vna != null) {
            versionNoPropertyName_ = vna;
        }
    }

    protected void setupTimestampPropertyName(BeanDesc beanDesc) {
        String tsa = beanAnnotationReader_.getTimestampPropertyName();
        if (tsa != null) {
            timestampPropertyName_ = tsa;
        }
    }

    protected void setupProperty(BeanDesc beanDesc,
            DatabaseMetaData dbMetaData, Dbms dbms) {

        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            PropertyType pt = null;
            if (beanAnnotationReader_.hasRelationNo(pd)) {
                if (!relation_) {
                    RelationPropertyType rpt = createRelationPropertyType(
                            beanDesc, pd, dbMetaData, dbms);
                    addRelationPropertyType(rpt);
                }
            } else {
                pt = createPropertyType(beanDesc, pd);
                addPropertyType(pt);
            }
            if (identifierGenerator_ == null) {
                String idAnnotation = beanAnnotationReader_.getId(pd);
                if (idAnnotation != null) {
                    identifierGenerator_ = IdentifierGeneratorFactory
                            .createIdentifierGenerator(pd.getPropertyName(),
                                    dbms, idAnnotation);
                    primaryKeys_ = new String[] { pt.getColumnName() };
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
        if (identifierGenerator_ == null) {
            List pkeyList = new ArrayList();
            Set primaryKeySet = DatabaseMetaDataUtil.getPrimaryKeySet(
                    dbMetaData, tableName_);
            for (int i = 0; i < getPropertyTypeSize(); ++i) {
                PropertyType pt = getPropertyType(i);
                if (primaryKeySet.contains(pt.getColumnName())) {
                    pt.setPrimaryKey(true);
                    pkeyList.add(pt.getColumnName());
                } else {
                    pt.setPrimaryKey(false);
                }
            }
            primaryKeys_ = (String[]) pkeyList.toArray(new String[pkeyList
                    .size()]);
            identifierGenerator_ = IdentifierGeneratorFactory
                    .createIdentifierGenerator(null, dbms);
        }
    }

    protected void setupPropertyPersistentAndColumnName(BeanDesc beanDesc,
            DatabaseMetaData dbMetaData) {

        Set columnSet = DatabaseMetaDataUtil.getColumnMap(dbMetaData,
                tableName_).keySet();
        if (columnSet.isEmpty()) {
            logger_.log("WDAO0002", new Object[] { tableName_ });
        }
        for (Iterator i = columnSet.iterator(); i.hasNext();) {

            String columnName = (String) i.next();
            String columnName2 = StringUtil.replace(columnName, "_", "");
            for (int j = 0; j < getPropertyTypeSize(); ++j) {
                PropertyType pt = getPropertyType(j);
                if (pt.getColumnName().equalsIgnoreCase(columnName2)) {
                    final PropertyDesc pd = pt.getPropertyDesc();
                    if (beanAnnotationReader_.getColumnAnnotation(pd) == null) {
                        pt.setColumnName(columnName);
                    }
                    break;
                }
            }
        }
        String[] props = beanAnnotationReader_.getNoPersisteneProps();
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
            propertyTypesByColumnName_.put(pt.getColumnName(), pt);
        }
    }

    protected RelationPropertyType createRelationPropertyType(
            BeanDesc beanDesc, PropertyDesc propertyDesc,
            DatabaseMetaData dbMetaData, Dbms dbms) {

        String[] myKeys = new String[0];
        String[] yourKeys = new String[0];
        int relno = beanAnnotationReader_.getRelationNo(propertyDesc);
        String relkeys = beanAnnotationReader_.getRelationKey(propertyDesc);
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
        Class beanClass = propertyDesc.getPropertyType();
        BeanMetaDataImpl beanMetaData = new BeanMetaDataImpl();
        beanMetaData.setBeanClass(beanClass);
        beanMetaData.setDatabaseMetaData(dbMetaData);
        beanMetaData.setDbms(dbms);
        beanMetaData.setAnnotationReaderFactory(getAnnotationReaderFactory());
        beanMetaData.setValueTypeFactory(getValueTypeFactory());
        beanMetaData.setRelation(true);
        beanMetaData.initialize();
        RelationPropertyType rpt = new RelationPropertyTypeImpl(propertyDesc,
                relno, myKeys, yourKeys, beanMetaData);
        return rpt;
    }

    protected void addRelationPropertyType(RelationPropertyType rpt) {
        for (int i = relationPropertyTypes_.size(); i <= rpt.getRelationNo(); ++i) {
            relationPropertyTypes_.add(null);
        }
        relationPropertyTypes_.set(rpt.getRelationNo(), rpt);
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getPrimaryKeySize()
     */
    public int getPrimaryKeySize() {
        return primaryKeys_.length;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getPrimaryKey(int)
     */
    public String getPrimaryKey(int index) {
        return primaryKeys_[index];
    }

    public IdentifierGenerator getIdentifierGenerator() {
        return identifierGenerator_;
    }

    /**
     * @see org.seasar.dao.BeanMetaData#getAutoSelectList()
     */
    public synchronized String getAutoSelectList() {
        if (autoSelectList_ != null) {
            return autoSelectList_;
        }
        setupAutoSelectList();
        return autoSelectList_;
    }

    protected void setupAutoSelectList() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("SELECT ");
        for (int i = 0; i < getPropertyTypeSize(); ++i) {
            PropertyType pt = getPropertyType(i);
            if (pt.isPersistent()) {
                buf.append(tableName_);
                buf.append(".");
                buf.append(pt.getColumnName());
                buf.append(", ");
            }
        }
        for (int i = 0; i < getRelationPropertyTypeSize(); ++i) {
            RelationPropertyType rpt = getRelationPropertyType(i);
            BeanMetaData bmd = rpt.getBeanMetaData();
            for (int j = 0; j < bmd.getPropertyTypeSize(); ++j) {
                PropertyType pt = bmd.getPropertyType(j);
                if (pt.isPersistent()) {
                    String columnName = pt.getColumnName();
                    buf.append(rpt.getPropertyName());
                    buf.append(".");
                    buf.append(columnName);
                    buf.append(" AS ");
                    buf.append(pt.getColumnName()).append("_").append(
                            rpt.getRelationNo());
                    buf.append(", ");
                }
            }
        }
        buf.setLength(buf.length() - 2);
        autoSelectList_ = buf.toString();
    }

    /**
     * @see org.seasar.dao.BeanMetaData#isRelation()
     */
    public boolean isRelation() {
        return relation_;
    }

    public void setRelation(boolean relation) {
        relation_ = relation;
    }

    public void setDatabaseMetaData(DatabaseMetaData databaseMetaData) {
        databaseMetaData_ = databaseMetaData;
    }

}
