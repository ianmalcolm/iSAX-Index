/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ian.ISAXIndex;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author ian
 */
public class Leaf extends Node {

    ArrayList<Long> children = new ArrayList<Long>();

    Leaf(ISAX word) {
        super(word);
    }

    @SuppressWarnings("unchecked")
	Leaf(Leaf leaf) {
        this(leaf.load);
        children = (ArrayList<Long>) leaf.children.clone();
    }
    
    public ArrayList<Long> getIDList(){
        return children;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public int numChildren() {
        return children.size();
    }

    public void add(long position) {
        children.add(position);
    }

    public void remove(long position) {
        for (int i = 0; i < numChildren(); i++) {
            if (children.get(i).equals(position)) {
                children.remove(i);
                break;
            }
        }
    }

    public long getID(int i) {
        assert i >= 0 && i < numChildren();
        return children.get(i);
    }
    
    public String getLoad(){
    	return load.toString();
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public void add(Node n) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void remove(Node n) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void split() {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public boolean needsSplit(int maxCard) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public ArrayList<Node> merge() {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public boolean needsMerge(int minCap) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public long integrityCheck() {
        int levelCount = 1;
        {
            Node n = this;
            while (!n.isRoot()) {
                n = n.parent;
                levelCount++;
            }
        }
        System.out.println("Leaf Level:\t" + levelCount + "\tIndex:\t" + parent.indexOf(this) + "\tNumber of children:\t" + numChildren());
        return numChildren();
    }
    
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return children.iterator();
	}
}
