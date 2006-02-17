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
package org.seasar.dao.impl;

import org.seasar.dao.impl.UpdateDynamicCommand;
import org.seasar.extension.jdbc.impl.BasicStatementFactory;
import org.seasar.extension.unit.S2TestCase;

public class UpdateDynamicCommandTest extends S2TestCase {

	public UpdateDynamicCommandTest(String arg0) {
		super(arg0);
	}

	public void testExecuteTx() throws Exception {
		UpdateDynamicCommand cmd = new UpdateDynamicCommand(getDataSource(),
				BasicStatementFactory.INSTANCE);
		cmd
				.setSql("UPDATE emp SET ename = /*employee.ename*/'HOGE' WHERE empno = /*employee.empno*/1234");
		cmd.setArgNames(new String[] { "employee" });

		Employee emp = new Employee();
		emp.setEmpno(7788);
		emp.setEname("SCOTT");
		Integer count = (Integer) cmd.execute(new Object[] { emp });
		assertEquals("1", new Integer(1), count);
	}

	public void setUp() {
		include("j2ee.dicon");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(UpdateDynamicCommandTest.class);
	}

}