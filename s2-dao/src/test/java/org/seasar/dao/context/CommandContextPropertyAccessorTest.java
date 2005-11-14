package org.seasar.dao.context;

import junit.framework.TestCase;
import ognl.Ognl;
import ognl.OgnlRuntime;

import org.seasar.dao.CommandContext;
import org.seasar.dao.context.CommandContextImpl;
import org.seasar.dao.context.CommandContextPropertyAccessor;

/**
 * @author higa
 *
 */
public class CommandContextPropertyAccessorTest extends TestCase {

	/**
	 * Constructor for InvocationImplTest.
	 * @param arg0
	 */
	public CommandContextPropertyAccessorTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(CommandContextPropertyAccessorTest.class);
	}

	protected void tearDown() throws Exception {
		OgnlRuntime.setPropertyAccessor(CommandContext.class, null);
	}

	public void testGetProperty() throws Exception {
		CommandContext ctx = new CommandContextImpl();
		ctx.addArg("aaa", "111", String.class);
		OgnlRuntime.setPropertyAccessor(
			CommandContext.class,
			new CommandContextPropertyAccessor());
		assertEquals("1", "111", Ognl.getValue("aaa", ctx));
		String s = "ELSEhogeEND";
		System.out.println(s.substring(4, s.length() - 3));
	}
}