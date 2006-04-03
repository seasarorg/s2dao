CREATE FUNCTION SALES_TAX(IN sales real,OUT tax real) AS $$
BEGIN
    tax := sales * 0.2;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION SALES_TAX2(IN sales real) RETURNS real AS $$
BEGIN
    RETURN sales * 0.2;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION SALES_TAX3(INOUT sales real) $$
BEGIN
    sales = sales * 0.2;
END;
$$ LANGUAGE plpgsql;

