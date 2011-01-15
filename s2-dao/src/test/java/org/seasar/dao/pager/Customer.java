/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dao.pager;

import java.sql.Timestamp;
import java.util.Date;

public class Customer {
    public static final String TABLE = "CUSTOMER";

    public static final String id_COLUMN = "ID";

    private int id;

    public static final String name_COLUMN = "NAME";

    private String name;

    public static final String city_COLUMN = "CITY";

    private String city;

    public static final String birthday_COLUMN = "DATE_OF_BIRTH";

    private Date birthday;

    public static final String bloodType_COLUMN = "BLOOD_TYPE";

    private String bloodType;

    public static final String joinDate_COLUMN = "DATE_OF_JOIN";

    private Timestamp joinDate;

    public static final String priority_COLUMN = "PRIORITY";

    private int priority;

    public static final String rank_COLUMN = "RANK";

    private Integer rank;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int sortOrder) {
        this.priority = sortOrder;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
