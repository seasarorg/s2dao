package org.seasar.dao;


/**
 * @author higa
 *
 */
public interface Node {

	public int getChildSize();
	
	public Node getChild(int index);
	
	public void addChild(Node node);

	public void accept(CommandContext ctx);

}
