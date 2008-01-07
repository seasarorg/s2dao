/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

import org.seasar.dao.DaoNamingConvention;

/**
 * setterを呼ぶことでカスタマイズ可能な、S2Daoでの命名規約です。
 * 
 * @author manhole
 */
public class DaoNamingConventionImpl implements DaoNamingConvention {

    private String versionNoPropertyName = "versionNo";

    private String timestampPropertyName = "timestamp";

    private String[] daoSuffixes = new String[] { "Dao" };

    private String[] insertPrefixes = new String[] { "insert", "create", "add" };

    private String[] updatePrefixes = new String[] { "update", "modify",
            "store" };

    private String[] deletePrefixes = new String[] { "delete", "remove" };

    private String[] unlessNullSuffixes = new String[] { "UnlessNull" };

    private String[] modifiedOnlySuffixes = new String[] { "ModifiedOnly" };

    private String modifiedPropertyNamesPropertyName = "modifiedPropertyNames";

    public String getModifiedPropertyNamesPropertyName() {
        return modifiedPropertyNamesPropertyName;
    }

    public void setModifiedPropertyNamesPropertyName(
            final String modifiedPropertyNamesPropertyName) {
        this.modifiedPropertyNamesPropertyName = modifiedPropertyNamesPropertyName;
    }

    public String getTimestampPropertyName() {
        return timestampPropertyName;
    }

    public void setTimestampPropertyName(final String timestampPropertyName) {
        this.timestampPropertyName = timestampPropertyName;
    }

    public String getVersionNoPropertyName() {
        return versionNoPropertyName;
    }

    public void setVersionNoPropertyName(final String versionNoPropertyName) {
        this.versionNoPropertyName = versionNoPropertyName;
    }

    public String[] getDaoSuffixes() {
        return daoSuffixes;
    }

    public void setDaoSuffixes(final String[] daoSuffixes) {
        this.daoSuffixes = daoSuffixes;
    }

    public String[] getDeletePrefixes() {
        return deletePrefixes;
    }

    public void setDeletePrefixes(final String[] deletePrefixes) {
        this.deletePrefixes = deletePrefixes;
    }

    public String[] getInsertPrefixes() {
        return insertPrefixes;
    }

    public void setInsertPrefixes(final String[] insertPrefixes) {
        this.insertPrefixes = insertPrefixes;
    }

    public String[] getUnlessNullSuffixes() {
        return unlessNullSuffixes;
    }

    public void setUnlessNullSuffixes(final String[] unlessNullSuffixes) {
        this.unlessNullSuffixes = unlessNullSuffixes;
    }

    public String[] getUpdatePrefixes() {
        return updatePrefixes;
    }

    public void setUpdatePrefixes(final String[] updatePrefixes) {
        this.updatePrefixes = updatePrefixes;
    }

    public String[] getModifiedOnlySuffixes() {
        return modifiedOnlySuffixes;
    }

    public void setModifiedOnlySuffixes(final String[] modifiedOnlySuffixes) {
        this.modifiedOnlySuffixes = modifiedOnlySuffixes;
    }

}
