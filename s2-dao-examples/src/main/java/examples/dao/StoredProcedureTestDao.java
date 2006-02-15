package examples.dao;


public interface StoredProcedureTestDao {
	public Class BEAN = Employee.class;
	public String getSalesTax_PROCEDURE = "sales_tax";
	public double getSalesTax(double subtotal);
	public String getSalesTax2_PROCEDURE = "sales_tax2";
	public double getSalesTax2(double subtotal);
	public String getSalesTax3_PROCEDURE = "sales_tax3";
	public double getSalesTax3(double subtotal);
//	public String getSalesTax4_PROCEDURE = "sales_tax4";
//	public Map getSalesTax4(double subtotal);
}
