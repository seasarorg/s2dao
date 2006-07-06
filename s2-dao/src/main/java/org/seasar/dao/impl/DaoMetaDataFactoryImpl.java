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

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.DaoMetaData;
import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

/**
 * @author higa
 * @author manhole
 */
public class DaoMetaDataFactoryImpl implements DaoMetaDataFactory, Disposable {

    protected Map daoMetaDataCache = new HashMap();

    protected DataSource dataSource;

    protected StatementFactory statementFactory;

    protected ResultSetFactory resultSetFactory;

    protected AnnotationReaderFactory annotationReaderFactory;

    protected ValueTypeFactory valueTypeFactory;

    protected String sqlFileEncoding;

    protected String[] daoSuffixes;

    protected String[] insertPrefixes;

    protected String[] deletePrefixes;

    protected String[] updatePrefixes;

    public DaoMetaDataFactoryImpl(DataSource dataSource,
            StatementFactory statementFactory,
            ResultSetFactory resultSetFactory,
            AnnotationReaderFactory readerFactory) {

        DisposableUtil.add(this);
        this.dataSource = dataSource;
        this.statementFactory = statementFactory;
        this.resultSetFactory = resultSetFactory;
        this.annotationReaderFactory = readerFactory;
    }

    public void setSqlFileEncoding(String encoding) {
        this.sqlFileEncoding = encoding;
    }

    public void setDaoSuffixes(String[] suffixes) {
        this.daoSuffixes = suffixes;
    }

    public void setInsertPrefixes(String[] prefixes) {
        this.insertPrefixes = prefixes;
    }

    public void setDeletePrefixes(String[] prefixes) {
        this.deletePrefixes = prefixes;
    }

    public void setUpdatePrefixes(String[] prefixes) {
        this.updatePrefixes = prefixes;
    }

    public synchronized DaoMetaData getDaoMetaData(Class daoClass) {
        String key = daoClass.getName();
        DaoMetaData dmd = (DaoMetaData) daoMetaDataCache.get(key);
        if (dmd != null) {
            return dmd;
        }
        DaoMetaData dmdi = createDaoMetaData(daoClass);
        daoMetaDataCache.put(key, dmdi);
        return dmdi;
    }

    protected DaoMetaData createDaoMetaData(Class daoClass) {
        DaoMetaDataImpl daoMetaData = new DaoMetaDataImpl();
        daoMetaData.setDaoClass(daoClass);
        daoMetaData.setDataSource(dataSource);
        daoMetaData.setStatementFactory(statementFactory);
        daoMetaData.setResultSetFactory(resultSetFactory);
        daoMetaData.setAnnotationReaderFactory(annotationReaderFactory);
        daoMetaData.setValueTypeFactory(valueTypeFactory);
        if (sqlFileEncoding != null) {
            daoMetaData.setSqlFileEncoding(sqlFileEncoding);
        }
        if (daoSuffixes != null) {
            daoMetaData.setDaoSuffixes(daoSuffixes);
        }
        if (insertPrefixes != null) {
            daoMetaData.setInsertPrefixes(insertPrefixes);
        }
        if (updatePrefixes != null) {
            daoMetaData.setUpdatePrefixes(updatePrefixes);
        }
        if (deletePrefixes != null) {
            daoMetaData.setDeletePrefixes(deletePrefixes);
        }
        daoMetaData.initialize();
        return daoMetaData;
    }

    public void setValueTypeFactory(ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    public void dispose() {
        daoMetaDataCache.clear();
    }

}
