package examples.dao;

public class NoPkTable {

    private String aaa;

    private Integer bbb;

    public String getAaa() {
        return aaa;
    }

    public void setAaa(String aaa) {
        this.aaa = aaa;
    }

    public Integer getBbb() {
        return bbb;
    }

    public void setBbb(Integer bbb) {
        this.bbb = bbb;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(aaa).append(", ");
        buf.append(bbb);
        return buf.toString();
    }
}