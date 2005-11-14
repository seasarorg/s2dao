package org.seasar.dao.node;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dao.Node;

/**
 * @author higa
 *
 */
public abstract class AbstractNode implements Node {

	private List children_ = new ArrayList();
	
	public AbstractNode() {
	}

	public int getChildSize() {
		return children_.size();
	}
	
	public Node getChild(int index) {
		return (Node) children_.get(index);
	}
	
	public void addChild(Node node) {
		children_.add(node);
	}
}
