package wags.logical;

import java.util.ArrayList;

import com.google.gwt.core.client.Duration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;

import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalPanelUi.Color;

public class Node {
	
	public static final int CLICKTIME = 100;
	private static final int OFFSET = 20;
	private long lastTime = 0;				// Default value, so that first click won't throw error - shows no previous click
	protected String value;
	protected Label label;
	protected Label n1 = null;
	protected Label n2 = null;
	public EdgeCollection ec;
	protected NodeCollection nc;
	protected NodeDragController drag;
	private NodeState ns;
	private boolean visited = false;
	private int top = 0;
	private int left = 0;
	private Node parent;
	private Node leftChild;
	private Node rightChild;
	
	public Node(String value, Label label) {
		this.value = value;
		this.label = label;
	}
	
	public void drawEdge(Node node) {
		EdgeUndirected eu = new EdgeUndirected(this, node, ec, true);
		eu.drawEdge();
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setTop(int top) {
		this.top = top + OFFSET; // add in offset to account for center of node
	}
	
	public void setLeft(int left) {
		this.left = left + OFFSET; // add in offset to account for center of node
	}
	
	@Override
	public boolean equals(Object otherNode) {
		if (this == null || otherNode == null) {
			return false;
		}
		return (this.getLabel().getText() == ((Node)otherNode).getLabel().getText());
	}
	
	public boolean edgeExists(Node node) {
		ArrayList<EdgeParent> edges = ec.getEdges();
		for (int i = 0; i < edges.size(); i++) {
			if ((edges.get(i).getN1() == this && edges.get(i).getN2() == node)
					|| (edges.get(i).getN1() == node && edges.get(i).getN2() == this)) {
						return true;
					}
		}
		return false;
	}
	
	public ArrayList<EdgeParent> getEdges() {
		ArrayList<EdgeParent> edges = ec.getEdges();
		ArrayList<EdgeParent> nodeEdges = new ArrayList<EdgeParent>();
		for (int i = 0; i < edges.size(); i++) {
			if ((edges.get(i).getN1() == this || edges.get(i).getN2() == this)) {
				nodeEdges.add(edges.get(i));
			}
		}
		return nodeEdges;
	}
	
	public NodeState getState() {
		return ns;
	}
	
	public String getValue() {
		return value;
	}

	public Label getLabel() {
		return label;
	}
	
	public int getTop() {
		if (top == 0) {
			// Looks bad, but isn't: Get the Node's exact pixel position off the screen, subtract the 
			// amount its parent (canvas) is offset from the top of the screen, and add 20 for half the
			// height of the node. Easy.
			return label.getElement().getAbsoluteTop() - label.getElement().getOffsetParent().getAbsoluteTop();
		}
		return top;
	}
	
	public int getLeft() {
		if (left == 0) {
			// See above comment for getTop()
			return label.getElement().getAbsoluteLeft() - label.getElement().getOffsetParent().getAbsoluteLeft();
		}
		return left;
	}
	
	/**
	 * Checks if a binary tree's node has too many children to add more
	 * 
	 * @return true if node already has two or more children, false otherwise
	 */
	public boolean hasChildren() {
		int numChildren = 0;
		ArrayList<EdgeParent> edges = ec.getEdges();
		for (int i = 0; i < edges.size(); i++) {
			// if node 1 is the node that this method is being called on
			if (edges.get(i).getN1().equals(this)) {
				// This line was originally the other way, edges.get(i).getN2().getTop() < getTop())
				// because I'm checking that N2 is a child of N1, same with corresponding line below
				if (edges.get(i).getN2().getTop() > getTop()) {
					numChildren++;
				}
			}
			// if node 2 is the node that this method is being called on
			else if (edges.get(i).getN2().equals(this)) {
				// Same comment as above
				if (edges.get(i).getN1().getTop() > getTop()) {
					numChildren++;
				}
			}
		}
		
		return (numChildren > 1);
	}
	
	public boolean hasParent() {
		ArrayList<EdgeParent> edges = ec.getEdges();
		for (int i = 0; i < edges.size(); i++) {
			if (edges.get(i).getN1().equals(this)) {
				if (edges.get(i).getN2().getTop() < getTop()) {
					return true;
				}
			}
			else if (edges.get(i).getN2().equals(this)) {
				if (edges.get(i).getN1().getTop() < getTop()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void setVisited(boolean v) {
		visited = v;
	}
	
	public boolean getVisited() {
		return visited;
	}
	
	/**
	 * Add default click handler, which uses the NodeState class to mark double clicks
	 */
	public void addClickHandler() {
		label.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//Checking for a double click, with a timeout of 500 ms
				if ((System.currentTimeMillis() - lastTime) < 500 && ns.state == 1) {
					ns.doubleClick();
				}
				else {
					ns.click();
				}
				lastTime = System.currentTimeMillis();
				
			}
		});
	}
	
	/**
	 * Add your own custom click handler to this particular node, enabling specific
	 * behaviour particular to your own application
	 * 
	 * @param c a ClickHandler that defines an action to be performed onClick
	 */
	public void addClickHandler(ClickHandler c) {
		label.addClickHandler(c);
	}
	
	@Override
	public String toString(){
		return this.getValue();
	}
	
	public void setNodeCollection(NodeCollection nc) {
		this.nc = nc;
		ns = new NodeState(this);
	}
	
	public void setEdgeCollection(EdgeCollection ec) {
		this.ec = ec;
	}
	
	public EdgeCollection getEdgeCollection() {
		return ec;
	}
	
	public NodeCollection getNodeCollection() {
		return nc;
	}
	
	public void selected(Label l) {
		l.removeStyleName("node");
		l.addStyleName("selected_node");
	}
	
	public void deselected(Label l) {
		l.removeStyleName("selected_node");
		l.addStyleName("node");
	}

	public Node getRightChild() {
		if (rightChild != null) {
			return rightChild;
		} else return null;
	}
	
	public void setChild(Node childNode) {
		if (childNode != null) {
			if (childNode.getLeft() < this.getLeft()) {
				setLeftChild(childNode);
			}
			else {
				setRightChild(childNode);
			}
		} else {
			resetChildren();
		}
	}
	
	private void resetChildren() {
		setLeftChild(null);
		setRightChild(null);
	}

	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	// Returns true if there is an MST Selected line other than the param line
	public boolean MSTSelected(Line line) {		
		ArrayList<EdgeParent> edges = getEdges();
		
		for (int i = 0; i < edges.size(); i++) {
			if (line != edges.get(i).getLine()) {
				if (edges.get(i).getLine().getStrokeColor().equals("#27f500")) 
					return true;
			}
		}
		
		return false;
	}
}	

