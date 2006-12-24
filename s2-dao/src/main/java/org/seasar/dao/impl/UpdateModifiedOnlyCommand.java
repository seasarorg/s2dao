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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.NoUpdatePropertyTypeRuntimeException;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author manhole
 * @author jflute
 */
public class UpdateModifiedOnlyCommand extends UpdateAutoDynamicCommand {

    public UpdateModifiedOnlyCommand(final DataSource dataSource,
            final StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    protected PropertyType[] createUpdatePropertyTypes(final BeanMetaData bmd,
            final Object bean, final String[] propertyNames) {

        final Set modifiedPropertyNames = getBeanMetaData()
                .getModifiedPropertyNames(bean);
        final List types = new ArrayList();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyNames.length; ++i) {
            final PropertyType pt = bmd.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey() == false) {
                final String propertyName = pt.getPropertyName();
                if (propertyName.equalsIgnoreCase(timestampPropertyName)
                        || propertyName.equalsIgnoreCase(versionNoPropertyName)
                        || modifiedPropertyNames.contains(propertyName)) {
                    types.add(pt);
                }
            }
        }
        if (types.isEmpty()) {
            throw new NoUpdatePropertyTypeRuntimeException();
        }
        final PropertyType[] propertyTypes = (PropertyType[]) types
                .toArray(new PropertyType[types.size()]);
        return propertyTypes;
    }
}
