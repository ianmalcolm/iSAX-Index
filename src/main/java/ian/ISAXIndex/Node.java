/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ian.ISAXIndex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ian
 */
public class Node implements Comparable<Node>, java.io.Serializable {

	protected Node parent = null;
	protected ArrayList<Node> children = null;
	protected ISAX load;
	private static final Logger logger = Logger.getLogger(Node.class.getName());

	Node(ISAX word) {
		load = new ISAX(word);
		children = new ArrayList<Node>();
	}

	// for root node
	Node(int dimensionality) {
		this.setParent(null);
		children = new ArrayList<Node>();
		load = new ISAX(dimensionality);
	}

	Node(Node n, int _width) {
		this.setParent(n.parent);
		children = new ArrayList<Node>();
		load = new ISAX(n.load, _width);
	}

	// fake node constructor
	Node(ISAX o, int _width) {
		load = new ISAX(o, _width);
	}

	public boolean equals(ISAX o) {
		return load.equals(o);
	}

	public boolean isEmpty() {
		return numChildren() == 0;
	}

	protected int indexOf(Node n) {
		return children.indexOf(n);
	}

	public long integrityCheck() {
		long idCount = 0;
		int levelCount = 1;
		{
			Node n = this;
			while (!n.isRoot()) {
				n = n.parent;
				levelCount++;
			}
		}
		if (!isRoot()) {
			System.out.println("Node Level:\t" + levelCount + "\tIndex:\t"
					+ parent.indexOf(this) + "\tNumber of children:\t"
					+ numChildren());
		} else {
			System.out.println("Node Level:\t" + levelCount + "\tIndex:\t"
					+ "Root" + "\tNumber of children:\t" + numChildren());
		}
		for (Node n : children) {
			assert n.parent == this;
			idCount += n.integrityCheck();
		}

		return idCount;
	}

	public String dispLoad() {
		return load.disp();
	}

	static void setLoggerLevel(Level level) {
		logger.setLevel(level);
	}

	public boolean isLeaf() {
		return false;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public int numChildren() {
		return children.size();
	}

	public void split() {
		ArrayList<Node> tempChildList = new ArrayList<Node>();
		for (Node n : children) {
			if (n.isLeaf()) {
				tempChildList.add(n);
			}
		}
		if (tempChildList.size() > 0) {
			removeAll(tempChildList);
		}

		Node fakeGrandparent = new Node(this, width());
		// each child must be a leaf
		for (Node child : tempChildList) {
			Node tempParent = fakeGrandparent.findPath(child);
			if (tempParent == null) {
				tempParent = new Node(child, width() + 1);
				fakeGrandparent.add(tempParent);
			}
			tempParent.add(child);
		}

		// for (Node n:children){
		// for (Node newN:fakeGrandparent.children){
		// assert newN
		// }
		// }
		addAll(fakeGrandparent.children);

	}

	public void setParent(Node n) {
		parent = n;
	}

	public boolean needsSplit(int maxCap) {
		int count = 0;
		for (int i = 0; i < numChildren(); i++) {
			if (children.get(i).isLeaf()) {
				count++;
			}
		}
		return count > maxCap;
	}

	public Node findPath(ISAX o) {
		assert o.width() > this.width();
		Node fakeNode = new Node(o, o.width());
		int position = Collections.binarySearch(children, fakeNode);
		// System.out.println();
		if (position >= 0) {
			return getNode(position);
		} else {
			return null;
		}
	}

	private Node findPath(Node n) {
		assert n.width() > this.width();
		int position = Collections.binarySearch(children, n);
		if (position >= 0) {
			return getNode(position);
		} else {
			return null;
		}
	}

	public boolean covers(ISAX o) {
		if (isRoot()) {
			return true;
		} else {
			assert o.width() >= this.width();
			return this.load.compareTo(o) == 0;
		}
	}

	public Node getNode(int i) {
		assert i >= 0 && i < children.size();
		return children.get(i);
	}

	public ArrayList<Node> merge() {
		for (Node n : children) {
			assert n.isLeaf();
		}
		return children;
	}

	public ArrayList<Node> getNodeList() {
		return children;
	}

	public boolean needsMerge(int minCap) {
		int count = 0;
		for (int i = 0; i < numChildren(); i++) {
			if (getNode(i).isLeaf()) {
				count++;
			}
		}
		return count == numChildren() && count < minCap;
	}

	protected int width() {
		return load.width();
	}

	public int compareTo(Node o) {
		Node a = this;
		Node b = o;
		if (a.parent != null) {
			if (a.width() > a.parent.width() + 1) {
				a = new Node(this, a.parent.width() + 1);
			}
		} else if (b.parent != null) {
			if (b.width() > b.parent.width() + 1) {
				b = new Node(o, b.parent.width() + 1);
			}
		}

		int coarse = a.load.compareTo(b.load);
		if (coarse > 0) {
			return 1;
		} else if (coarse < 0) {
			return -1;
		} else if (this.width() == a.width() && o.width() == b.width()) {
			return 0;
		} else {
			return this.load.compareTo(o.load);
		}
	}

	public double minDist(ISAX o) {
		return load.minDist(o);
	}

	public void add(Node n) {
		int position = Collections.binarySearch(children, n);
		assert position < 0;
		children.add(-1 * position - 1, n);
		n.setParent(this);
	}

	public void remove(Node n) {
		children.remove(n);
	}

	public void addAll(ArrayList<Node> nodeList) {
		for (Node n : nodeList) {
			n.setParent(this);
			add(n);
		}
	}

	public void removeAll(ArrayList<Node> nodeList) {
		children.removeAll(nodeList);
	}

}
