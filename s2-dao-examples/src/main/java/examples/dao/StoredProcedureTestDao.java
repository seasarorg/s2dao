package examples.dao;

public interface StoredProcedureTestDao {
	public Class BEAN = Employee.class;
	public String getSalesTax_PROCEDURE = "sales_tax";
	public double getSalesTax(double subtotal);
	public String getSalesTax2_PROCEDURE = "sales_tax2";
	public double getSalesTax2(double subtotal);
}
