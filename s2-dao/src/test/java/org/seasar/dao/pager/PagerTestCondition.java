package org.seasar.dao.pager;

import java.util.Date;

public class PagerTestCondition extends DefaultPagerCondition {

    private static final long serialVersionUID = 1L;

    private Date startDate;

    private String sortKey;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
}
