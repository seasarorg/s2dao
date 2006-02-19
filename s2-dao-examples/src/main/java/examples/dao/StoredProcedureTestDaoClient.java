/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

public class StoredProcedureTestDaoClient {

	private static final String PATH = "examples/dao/StoredProcedureTestDao.dicon";

	public static void main(String[] args) {
		S2Container container = S2ContainerFactory.create(PATH);
		container.init();
		try {
			StoredProcedureTestDao dao = (StoredProcedureTestDao) container
					.getComponent(StoredProcedureTestDao.class);
			System.out.println("SALES_TAX(1000) =" + dao.getSalesTax(1000));
			System.out.println("SALES_TAX2(1000) =" + dao.getSalesTax2(1000));
			System.out.println("SALES_TAX3(1000) =" + dao.getSalesTax3(1000));
			System.out.println("SALES_TAX4(1000) =" + dao.getSalesTax4(1000));
		} finally {
			container.destroy();
		}

	}
}