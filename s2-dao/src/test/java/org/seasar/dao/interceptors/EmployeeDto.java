package org.seasar.dao.interceptors;

public class EmployeeDto extends Employee {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String dname;

    /**
     * @return Returns the dname.
     */
    public String getDname() {
        return dname;
    }

    /**
     * @param dname The dname to set.
     */
    public void setDname(String dname) {
        this.dname = dname;
    }

}
