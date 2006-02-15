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
			System.out.println("sales_tax(1000) =" + dao.getSalesTax(1000));
			System.out.println("sales_tax2(1000) =" + dao.getSalesTax2(1000));
			System.out.println("sales_tax3(1000) =" + dao.getSalesTax3(1000));
//			System.out.println("sales_tax4(1000) =" + dao.getSalesTax4(1000));
		} finally {
			container.destroy();
		}

	}
}