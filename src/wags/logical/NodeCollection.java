package wags.logical;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.thirdparty.guava.common.collect.BinaryTreeTraverser;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;

import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalProblem;

public class NodeCollection implements IsSerializable
{	

	public static final int PREORDER = 0;
	public static final int INORDER = 1;
	public static final int POSTORDER = 2;
	public String traversalString;
	
	protected ArrayList<Node> nodes;
	
	public NodeCollection()
	{
		nodes = new ArrayList<Node>();
	}
	
	public void addNode(Node n)
	{
		nodes.add(n);
	}
	
	public void addNodesToPanel(AbsolutePanel panel) {
		if (!LogicalPanelUi.hasPositions())
		{	
			//Window.alert("IF");
			for (int i = 0; i < nodes.size(); i++) {
				panel.add(nodes.get(i).getLabel(), 5 + (50 * i), 5);
			}
		} else {
			//Window.alert("ELSE");
			for (int i = 0; i < nodes.size(); i++) {
				if (!LogicalPanelUi.isDraggable()) {
					//Window.alert(nodes.get(i).getLabel().toString());
					panel.add(nodes.get(i).getLabel(), 
							nodes.get(i).getLeft(), nodes.get(i).getTop());
				}
			}
		}
	}
	
	public ArrayList<Node> getNodes()
	{
		return nodes;
	}
	
	public Node getNode(int index)
	{
		if(index >= 0 && index < nodes.size())
		{
			return nodes.get(index);
		}
		else
			throw new IndexOutOfBoundsException();
	}
	
	public Node getNodeByLabel(Label l)
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			if(nodes.get(i).getLabel().getText() == l.getText())
			{
				return nodes.get(i);
			}
		}
		throw new NoSuchElementException();
	}
	
	public Node getNodeByLabelText(String labelText) {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getLabel().getText() == labelText) {
				return nodes.get(i);
			}
		}
		throw new NoSuchElementException();
	}
	
	public String getTraversal(int traversalType, ArrayList<EdgeParent> edgeList) {
		String[] traversal = new String[nodes.size()];
		traversalString = "";
		Node root = getNode(0);
		for (int i =0; i < edgeList.size(); i++) {
			int n1Pos = edgeList.get(i).getN1().getTop();
			int n2Pos = edgeList.get(i).getN2().getTop();
			
			if (n1Pos > n2Pos) {
				edgeList.get(i).getN2().setChild(edgeList.get(i).getN1());
				edgeList.get(i).getN1().setParent(edgeList.get(i).getN2());
				//Window.alert("Parent: " + edgeList.get(i).getN2().getParent() + " Left Child: " + edgeList.get(i).getN2().getLeftChild() + "Right Child: " + edgeList.get(i).getN2().getRightChild() + " n1: "+ edgeList.get(i).getN1() + " n2: " + edgeList.get(i).getN2());
			}
			else {
				edgeList.get(i).getN1().setChild(edgeList.get(i).getN2());
				edgeList.get(i).getN2().setParent(edgeList.get(i).getN1());
				//Window.alert("Parent: " + edgeList.get(i).getN1().getParent() + " Left Child: " + edgeList.get(i).getN1().getLeftChild() + " Right Child: " + edgeList.get(i).getN1().getRightChild() + " n1: "+ edgeList.get(i).getN1() + " n2: " + edgeList.get(i).getN2());
			}
			
		}
		switch (traversalType) {
		case PREORDER:
			for (int i = 0; i < nodes.size(); i++) {
				if (getNode(i).getTop() < root.getTop())
					root = getNode(i);
			}
			preorder(root);
//			traversal[0] = root.getLabel().getText();
//			for (int j = 0; j < traversal.length; j++) {
//				traversalString += traversal[j];
//			}
			break;
		case INORDER:
			for (int i = 0; i < nodes.size(); i++) {
				if (getNode(i).getTop() < root.getTop())
					root = getNode(i);
			}
			inorder(root);
//			traversal[0] = root.getLabel().getText();
//			for (int j = 0; j < traversal.length; j++) {
//				traversalString += traversal[j];
//			}
			break;
		case POSTORDER:
			for (int i = 0; i < nodes.size(); i++) {
				if (getNode(i).getTop() < root.getTop())
					root = getNode(i);
			}
			postorder(root);
			break;
		default:
			traversalString = "Incorrect traversal type; location: wags.logical.NodeCollection:getTraversal";
			break;
		}
		
		return traversalString;
	}
	
	public void preorder(Node root) {
		if (root != null) {
			traversalString += root.getLabel().getText();
			preorder(root.getLeftChild());
			preorder(root.getRightChild());
		}
	}
	
	public void inorder(Node root) {
		if (root != null) {
			inorder(root.getLeftChild());
			traversalString += root.getLabel().getText();
			inorder(root.getRightChild());
		}
	}
	
	public void postorder(Node root) {
		if (root != null) {
			postorder(root.getLeftChild());
			postorder(root.getRightChild());
			traversalString += root.getLabel().getText();
		}
	}
	
	public void resetNodes() {
		for (int i = 0; i < nodes.size(); i++) {
			LogicalProblem.getDragPanel().remove(nodes.get(i).getLabel());
		}
		addNodesToPanel(LogicalProblem.getDragPanel());
	}
	
	public void resetNodes(AbsolutePanel panel) {
		for (int i = 0; i < nodes.size(); i++) {
			panel.remove(nodes.get(i).getLabel());
		}
		addNodesToPanel(panel);
	}
	
	public void resetNodeStyles(String nodeType)
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			if(nodeType.equals(DSTConstants.NODE_STRING_DRAGGABLE)){
				nodes.get(i).getLabel().setStyleName("string_node");
			}
			else{
				nodes.get(i).getLabel().setStyleName("node");
			}
		}
	}
	
	public void removeSelectedState() {
		for(int i = 0; i < nodes.size(); i++){
			nodes.get(i).getLabel().removeStyleName("selected_node");
		}
		
	}
			
	public void makeNodesDraggable(DragController dc)
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			dc.makeDraggable(nodes.get(i).getLabel());
		}
	}
	
	public void makeNodesNotDraggable(DragController dc)
	{
		try
		{
			for(int i = 0; i < nodes.size(); i++)
			{
				dc.makeNotDraggable(nodes.get(i).getLabel());
			}
		}
		catch(Exception e)
		{
			System.out.println("Still ok");
		}
	}
	
	public void emptyNodes()
	{
		nodes.clear();
	}
	
	public int size() {
		return nodes.size();
	}

}
