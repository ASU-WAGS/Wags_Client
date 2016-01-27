package wags.logical;

import java.util.ArrayList;

import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalProblem;
import wags.logical.view.LogicalPanelUi.Color;

import com.google.gwt.user.client.Window;

/**
 * 
 * Idea here is that each Node gets its own click state, dependent on how the clicks are interpreted - necessary due to the nature of browser
 * 
 *  double clicks, id est a browser registers a double click as {(single click) (delta t < 500 ms) (single click) (double click)}, so 
 * 
 * GWT would always interpret single clicks immediately and never reach the double click handler.
 *
 */
public class NodeState extends NodeCollection {

	public static int state;
	public static boolean manual = false;
	public static boolean swap = false;
	private Node node;
	private NodeCollection nc;
	
	final static int NOT_CLICKED = 0;
	final static int CLICKED = 1;
	final static int DOUBLE_CLICKED = 2;
	final static int ADD_EDGE_CLICK = 3;
	final static int SWAP_NODES_CLICK = 4;

	public NodeState(Node node) {
		this.node = node;
		state = NOT_CLICKED;
		nc = node.getNodeCollection();
	}
	
	public void notClicked() {
		node.deselected(node.getLabel());
		state = NOT_CLICKED;
	}
	
	public void click() {
		if (manual) {
			node.selected(node.getLabel());
			state = DOUBLE_CLICKED;
			LogicalPanelUi.setMessage("Click the second node of the edge to add", Color.Notification);
			toggleAdd();
			for (int i = 0; i < nc.size(); i++)
				NodeDragController.getInstance().makeNotDraggable(nc.getNode(i).getLabel());
		} else if (swap) {
			Node swapNode = findNodeWithState(SWAP_NODES_CLICK);
			if (swapNode == null) {
				node.selected(node.getLabel());
				state = SWAP_NODES_CLICK;
				LogicalPanelUi.setMessage("Click the second node to swap with", Color.Notification);
				for (int i = 0; i < nc.size(); i++)
					NodeDragController.getInstance().makeNotDraggable(nc.getNode(i).getLabel());
			} else if (!swapNode.equals(node)) {
				toggleSwap();
				state = NOT_CLICKED;
				swapNodes(swapNode);
			} else {
				LogicalPanelUi.setMessage("Can't swap a node with itself; please click another node.", Color.Warning); 
			}
		} else {
			Node drawNode = findNodeWithState(DOUBLE_CLICKED);
			if (drawNode != null && !drawNode.equals(node)) {
				state = ADD_EDGE_CLICK;
				addEdgeClick(drawNode);
			}
			else if (drawNode.equals(node)) {
				LogicalPanelUi.setMessage("You cannot create an edge to the same node; please click a different node", Color.Warning);
				drawNode.getState().notClicked();
				state = CLICKED;
				for (int i = 0; i < nc.size(); i++)
					NodeDragController.getInstance().makeDraggable(nc.getNode(i).getLabel());
			}
			else {
				state = CLICKED;
				LogicalPanelUi.setMessage("", Color.None);
			}
		
		}
	}
	
	public void doubleClick() {
		Node resetNode = findNodeWithState(DOUBLE_CLICKED);
		if (resetNode != null) {
			resetNode.getState().notClicked();
			LogicalPanelUi.setMessage("", Color.None);
		}
		else {
			node.selected(node.getLabel());
			for (int i = 0; i < nc.size(); i++)
				NodeDragController.getInstance().makeNotDraggable(nc.getNode(i).getLabel());
			state = DOUBLE_CLICKED;
			LogicalPanelUi.setMessage("", Color.None);
		}
	}
	
	public void addEdgeClick(Node toDraw) {
		boolean parent = (toDraw.getTop() > node.getTop()) ? toDraw.hasParent() : node.hasParent();
		boolean children = (toDraw.getTop() < node.getTop()) ? toDraw.hasChildren() : node.hasChildren();
		if (toDraw.edgeExists(node)) {
			LogicalPanelUi.setMessage("Edge already exists between these two nodes", Color.Warning);
			toDraw.getState().notClicked();
			node.getState().notClicked();
		}
		else if (parent) {
			LogicalPanelUi.setMessage("Cannot add edge, this node already has a parent", Color.Error);
			toDraw.getState().notClicked();
			node.getState().notClicked();
		}
		else if (children) {
			LogicalPanelUi.setMessage("Cannot add edge, this node already has its maximum of two children", Color.Error);
			toDraw.getState().notClicked();
			node.getState().notClicked();
		}
		else {
			LogicalPanelUi.setMessage("", Color.None);
			Node node = findNodeWithState(ADD_EDGE_CLICK);
			toDraw.getState().notClicked();
			node.getState().notClicked();
			toDraw.drawEdge(node);
		}
		for (int i = 0; i < nc.size(); i++)
			NodeDragController.getInstance().makeDraggable(nc.getNode(i).getLabel());
	}
	
	public void swapNodes(Node toSwap) {
		LogicalPanelUi.setMessage("Swapping nodes: " + node.getLabel().getText() 
				+ " and " + toSwap.getLabel().getText(), Color.Notification);
		state = NOT_CLICKED;
	}
	
	public static void toggleAdd() {
		manual = !manual;
		if (!manual) {
			state = NOT_CLICKED;
			NodeCollection temp = LogicalProblem.getNodeCollection();
			for (int i = 0; i < temp.size(); i++) {
				NodeDragController.getInstance().makeDraggable(temp.getNode(i).getLabel());
			}
		}
	}
	
	public static void toggleSwap() {
		swap = !swap;
		if (!swap) {
			state = NOT_CLICKED;
			NodeCollection temp = LogicalProblem.getNodeCollection();
			for (int i = 0; i < temp.size(); i++) {
				NodeDragController.getInstance().makeDraggable(temp.getNode(i).getLabel());
			}
		}
	}
	
	public Node findNodeWithState(int state) {
		Node stateNode = null;
		for (int i = 0; i < nc.size(); i++) {
			if (nc.getNode(i).getState().state == state) {
				stateNode = nc.getNode(i);
			}
		}
		return stateNode;
	}
	
}
