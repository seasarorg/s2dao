/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package examples.dao;

import java.util.Map;

public interface StoredProcedureTestDao {
    public Class BEAN = Employee.class;

    public String getSalesTax_PROCEDURE = "SALES_TAX";

    public double getSalesTax(double subtotal);

    public String getSalesTax2_PROCEDURE = "SALES_TAX2";

    public double getSalesTax2(double subtotal);

    public String getSalesTax3_PROCEDURE = "SALES_TAX3";

    public double getSalesTax3(double subtotal);

    public String getSalesTax4_PROCEDURE = "SALES_TAX4";

    public Map getSalesTax4(double subtotal);
}
