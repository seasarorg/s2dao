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
package org.seasar.dao.unit;

import java.sql.DatabaseMetaData;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.Dbms;
import org.seasar.dao.RelationPropertyType;
import org.seasar.dao.dbms.DbmsManager;
import org.seasar.dao.impl.BeanMetaDataImpl;
import org.seasar.dao.impl.FieldAnnotationReaderFactory;
import org.seasar.dao.impl.ValueTypeFactoryImpl;
import org.seasar.extension.dataset.ColumnType;
import org.seasar.extension.dataset.DataReader;
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.DataSetImpl;
import org.seasar.extension.dataset.states.RowStates;
import org.seasar.extension.dataset.types.ColumnTypes;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.beans.PropertyDesc;

public class S2DaoBeanReader implements DataReader {

    private DataSet dataSet = new DataSetImpl();

    private DataTable table = dataSet.addTable("S2DaoBean");

    protected S2DaoBeanReader() {
    }

    /**
     * @deprecated
     */
    public S2DaoBeanReader(Object bean, DatabaseMetaData dbMetaData) {
        Dbms dbms = DbmsManager.getDbms(dbMetaData);
        BeanMetaDataImpl beanMetaData = new BeanMetaDataImpl();
        beanMetaData.setBeanClass(bean.getClass());
        beanMetaData.setDatabaseMetaData(dbMetaData);
        beanMetaData.setDbms(dbms);
        beanMetaData
                .setAnnotationReaderFactory(new FieldAnnotationReaderFactory());
        beanMetaData.setValueTypeFactory(new ValueTypeFactoryImpl());
        beanMetaData.initialize();
        initialize(bean, beanMetaData);
    }

    public S2DaoBeanReader(Object bean, BeanMetaData beanMetaData) {
        initialize(bean, beanMetaData);
    }

    private void initialize(Object bean, BeanMetaData beanMetaData) {
        setupColumns(beanMetaData);
        setupRow(beanMetaData, bean);
    }

    protected void setupColumns(BeanMetaData beanMetaData) {
        for (int i = 0; i < beanMetaData.getPropertyTypeSize(); ++i) {
            PropertyType pt = beanMetaData.getPropertyType(i);
            Class propertyType = pt.getPropertyDesc().getPropertyType();
            table.addColumn(pt.getColumnName(), ColumnTypes
                    .getColumnType(propertyType));
        }
        for (int i = 0; i < beanMetaData.getRelationPropertyTypeSize(); ++i) {
            RelationPropertyType rpt = beanMetaData.getRelationPropertyType(i);
            for (int j = 0; j < rpt.getBeanMetaData().getPropertyTypeSize(); j++) {
                PropertyType pt = rpt.getBeanMetaData().getPropertyType(j);
                String columnName = pt.getColumnName() + "_"
                        + rpt.getRelationNo();
                Class propertyType = pt.getPropertyDesc().getPropertyType();
                table.addColumn(columnName, ColumnTypes
                        .getColumnType(propertyType));
            }
        }
    }

    protected void setupRow(BeanMetaData beanMetaData, Object bean) {
        DataRow row = table.addRow();
        for (int i = 0; i < beanMetaData.getPropertyTypeSize(); ++i) {
            PropertyType pt = beanMetaData.getPropertyType(i);
            PropertyDesc pd = pt.getPropertyDesc();
            Object value = pd.getValue(bean);
            ColumnType ct = ColumnTypes.getColumnType(pd.getPropertyType());
            row.setValue(pt.getColumnName(), ct.convert(value, null));
        }
        for (int i = 0; i < beanMetaData.getRelationPropertyTypeSize(); ++i) {
            RelationPropertyType rpt = beanMetaData.getRelationPropertyType(i);
            Object relationBean = rpt.getPropertyDesc().getValue(bean);
            if (relationBean == null) {
                continue;
            }
            for (int j = 0; j < rpt.getBeanMetaData().getPropertyTypeSize(); j++) {
                PropertyType pt = rpt.getBeanMetaData().getPropertyType(j);
                String columnName = pt.getColumnName() + "_"
                        + rpt.getRelationNo();
                PropertyDesc pd = pt.getPropertyDesc();
                Object value = pd.getValue(relationBean);
                ColumnType ct = ColumnTypes.getColumnType(pd.getPropertyType());
                row.setValue(columnName, ct.convert(value, null));
            }
        }
        row.setState(RowStates.UNCHANGED);
    }

    public DataSet read() {
        return dataSet;
    }

}
