package org.seasar.dao.pager;

public interface CustomerDao {
    public static final Class BEAN = Customer.class;

    public static final String getPagedRow_QUERY = "ORDER BY PRIORITY";

    public Customer[] getPagedRow(DefaultPagerCondition condition);

    public Customer[] getPagedRow2(PagerTestCondition condition);

}
