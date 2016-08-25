package wags.logical;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;

import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalProblem;

public class NodeCollection implements IsSerializable, Iterable<Node>
{	

	public static final int PREORDER = 0;
	public static final int INORDER = 1;
	public static final int POSTORDER = 2;
	public static final int LATERAL = 3;
	public String traversalString;
	
	private Node[] heap;
	private ArrayList<String> heapTravs;
	private int heapPtr;
	
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
			for (int i = 0; i < nodes.size(); i++) {
				panel.add(nodes.get(i).getLabel(), 5 + (50 * i), 5);
			}
		} else {
			for (int i = 0; i < nodes.size(); i++) {
				if (!LogicalPanelUi.isDraggable()) {
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
		clearTraversal(edgeList);
		for (int i =0; i < edgeList.size(); i++) {
			
			int n1Pos = edgeList.get(i).getN1().getTop();
			int n2Pos = edgeList.get(i).getN2().getTop();
			
			if (n1Pos > n2Pos) {
				edgeList.get(i).getN2().setChild(edgeList.get(i).getN1());
				edgeList.get(i).getN1().setParent(edgeList.get(i).getN2());
			}
			else {
				edgeList.get(i).getN1().setChild(edgeList.get(i).getN2());
				edgeList.get(i).getN2().setParent(edgeList.get(i).getN1());
			}
		}
		
		switch (traversalType) {
		case PREORDER:
			for (int i = 0; i < nodes.size(); i++) {
				if (getNode(i).getTop() < root.getTop() && getNode(i).hasChildren())
					root = getNode(i);
			}
			preorder(root);
			break;
		case INORDER:
			for (int i = 0; i < nodes.size(); i++) {
				if (getNode(i).getTop() < root.getTop() && getNode(i).hasChildren())
					root = getNode(i);
			}
			inorder(root);
			break;
		case POSTORDER:
			for (int i = 0; i < nodes.size(); i++) {
				if (getNode(i).getTop() < root.getTop() && getNode(i).hasChildren())
					root = getNode(i);
			}
			postorder(root);
			break;
		case LATERAL:
			if (!root.hasChildren())
				root = getNode(1);
			for (int i = 0; i < nodes.size(); i++) {
				if (getNode(i).getTop() < root.getTop() && getNode(i).hasChildren()) {
					root = getNode(i);
				}
			}
			lateral(root);
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
	
	public void lateral(Node root) {		
		Queue<Node> queue = new LinkedList<Node>();
		if (root != null) {
			queue.offer(root);
			while (!queue.isEmpty()) {
				Node node = queue.poll();
				traversalString += node.getLabel().getText();
				if (node.getLeftChild() != null) {
					queue.offer(node.getLeftChild());
				}
				if (node.getRightChild() != null) {
					queue.offer(node.getRightChild());
				}
			}
		}
	}
	
	public ArrayList<String> getHeapTraversals() {
		heap = new Node[nodes.size()];
			
		for (int i = 0; i < nodes.size(); i++) {
			heap[i] = nodes.get(i);
		}
			
		heapPtr = heap.length - 1;
			
		heapTravs = new ArrayList<String>();
		
		sort();
		
		return heapTravs;
	}
	
	private void sort() {
		heapify();
		for (int i = heapPtr; i > 0; i--) {
			swap(0, i);
			heapPtr--;
			maxHeap(0);
			
			String trav = "";
			for (int j = 0; j < heap.length; j++) {
				trav += heap[j].toString(); 
			}
			heapTravs.add(trav);
			
		}
	}
	
	private void heapify() {
		for (int i = heapPtr / 2; i >= 0; i--) {
			maxHeap(i);
		}
	}
	
	private void maxHeap(int pivot) {
		int left = 2 * pivot + 1;
		int right = 2 * pivot + 2;
		int largest = pivot;
		if (left <= heapPtr && heap[left].getIntegerValue() > heap[pivot].getIntegerValue())
			largest = left;
		if (right <= heapPtr && heap[right].getIntegerValue() > heap[largest].getIntegerValue())
			largest = right;
		
		if (largest != pivot) {
			swap(pivot, largest);
			maxHeap(largest);
		}
	}
	
	private void swap(int swap1, int swap2) {
		Node temp = heap[swap1];
		heap[swap1] = heap[swap2];
		heap[swap2] = temp;
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
	
	private void clearTraversal(ArrayList<EdgeParent> edges) {
		for (int i = 0; i < edges.size(); i++) {
			edges.get(i).getN1().setChild(null);
			edges.get(i).getN1().setParent(null);
			edges.get(i).getN2().setChild(null);
			edges.get(i).getN2().setParent(null);
		}
	}
	
	public void emptyNodes()
	{
		nodes.clear();
	}
	
	public int size() {
		return nodes.size();
	}

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}

}
