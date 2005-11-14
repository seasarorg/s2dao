package examples.dao;

public class EmployeeSearchCondition {

	public static final String dname_COLUMN = "dname_0";
	private String job;
	private String dname;
	
	public String getDname() {
		return dname;
	}
	
	public void setDname(String dname) {
		this.dname = dname;
	}
	
	public String getJob() {
		return job;
	}
	
	public void setJob(String job) {
		this.job = job;
	}
}
