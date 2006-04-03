CREATE OR REPLACE FUNCTION  "SALES_TAX2" 
(sales in NUMBER)
return NUMBER
is
begin
return sales * 0.2;
end;
/
 

CREATE OR REPLACE PROCEDURE  "SALES_TAX" 
(sales IN NUMBER,
tax OUT NUMBER)
is
begin
tax := sales * 0.2;
end;
/
 CREATE OR REPLACE PROCEDURE  "SALES_TAX3" 
(sales IN OUT NUMBER)
is
begin
sales := sales * 0.2;
end;
/
 CREATE OR REPLACE PROCEDURE  "SALES_TAX4" 
(sales IN NUMBER,
tax OUT NUMBER,
total OUT NUMBER)
is
begin
tax := sales * 0.2;
total := sales * 1.2;
end;
/ 