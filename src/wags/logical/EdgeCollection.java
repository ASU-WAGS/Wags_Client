package wags.logical;

import java.util.ArrayList;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;

import wags.logical.TreeProblems.RedBlackProblems.TreeTypeDisplayManager;
import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalPanelUi.Color;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;

public class EdgeCollection implements IsSerializable {
	private NodeCollection nc;
	private String[] nodeSelectionInstructions;
	private ArrayList<EdgeParent> edges;
	private ArrayList<Line> lines;
	private int numNodesSelected;
	private Label firstNodeSelected;
	private ArrayList<EdgeParent> MSTClicked;
	//private EdgeClickListener handler;
	private TreeTypeDisplayManager dm;
	private HandlerRegistration[] edgeHandlers;
	private AddEdgeRules rules;
	private DrawingArea panel;
	private boolean removable;
	private NodeCollection graphNodeCollection = new NodeCollection();

	public EdgeCollection(AddEdgeRules rules,
			String[] nodeSelectionInstructions, boolean removable) {
		this.rules = rules;
		this.nodeSelectionInstructions = nodeSelectionInstructions;
		this.removable = removable;
		edges = new ArrayList<EdgeParent>();
		lines = new ArrayList<Line>();
		MSTClicked = new ArrayList<EdgeParent>();
		//handler = new EdgeClickListener();
		numNodesSelected = 0;
	}

	// I plan to remove this, just give me time to replace it
	public void setDisplayManager(DisplayManager dm) {
		this.dm = (TreeTypeDisplayManager) dm;
	}
	
	public void setCanvas(DrawingArea panel) {
		this.panel = panel;
	}
	
	public DisplayManager getDisplayManager() {
		return this.dm;
	}

//	public void addNextEdge() {
//		dm.setEdgeNodeSelectionInstructions(nodeSelectionInstructions[0]);
//		class EdgeNodeSelectionHandler implements ClickHandler {
//			@Override
//			public void onClick(ClickEvent event) {
//				Label selectedNode = (Label) event.getSource();
//				if (numNodesSelected == 0) {
//					Node n = getNodeByLabel(selectedNode);
//					selectFirstNodeOfEdge(n.getLabel());
//				} else if (selectedNode != firstNodeSelected) {
//					Node n1 = getNodeByLabel(firstNodeSelected);
//					Node n2 = getNodeByLabel(selectedNode);
//					if (n2.getTop() < n1.getTop()) {
//						Node temp = n2;
//						n2 = n1;
//						n1 = temp;
//					}
//					dm.setEdgeParentAndChildren();
//					String check = rules.checkSecondNode(n1, n2, dm.getNodes(),
//							dm.getEdges());
//
//					if (check.equalsIgnoreCase(DSTConstants.CORRECT)) {
//						EdgeUndirected eu = new EdgeUndirected(n1, n2,
//								getInstance(), handler, removable);
//						eu.drawEdge();
//						edges.add(eu);
//						dm.addEdgeCancel();
//						dm.resetRemoveEdgeButton();
//					} else {
//						showEdgeAdditionError(check);
//					}
//				}
//			}
//		}
//
//		EdgeNodeSelectionHandler eh = new EdgeNodeSelectionHandler();
//
//		ArrayList<Node> nodes = dm.getNodes();
//		edgeHandlers = new HandlerRegistration[nodes.size()];
//
//		for (int i = 0; i < nodes.size(); i++) {
//			Node n = nodes.get(i);
//			Label l = n.getLabel();
//			edgeHandlers[i] = l.addClickHandler(eh);
//		}
//	}

	private EdgeCollection getInstance() {
		return this;
	}

	private void showEdgeAdditionError(String error) {
		TextArea t = new TextArea();
		t.setCharacterWidth(30);
		t.setReadOnly(true);
		t.setVisibleLines(5);
		t.setText(error);
		dm.addToPanel(t, DSTConstants.PROMPT_X, DSTConstants.PROMPT_Y);

		Button ok = new Button("Ok");
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dm.resetEdgeStyles();
				dm.removeWidgetsFromPanel();
			}
		});
		int yOffset = DSTConstants.PROMPT_Y + t.getOffsetHeight() + 1;
		dm.addToPanel(ok, DSTConstants.PROMPT_X, yOffset);
	}

	public void selectFirstNodeOfEdge(Label node) {
		dm.setEdgeParentAndChildren();
		String check = rules.checkFirstNode(getNodeByLabel(node),
				dm.getNodes(), dm.getEdges());
		if (check.equals(DSTConstants.CORRECT)) {
			firstNodeSelected = node;
			numNodesSelected++;
			dm.setEdgeNodeSelectionInstructions(nodeSelectionInstructions[1]);
			if (node.getOffsetHeight() < 38) { // Check to see if regular node
												// or string node
				node.setStyleName("selected_string_node");
			} else {
				node.addStyleName("selected_node");
			}
		} else {
			showEdgeAdditionError(check);
		}
	}

	public void insertEdges(String[] edgePairs, NodeCollection nc) {
		this.nc = nc;
		EdgeUndirected eu;
		
		for (int x = 0; x < edgePairs.length; x++) {
			
			// edgePairs is already split by nodes to have a line drawn between them, 
			// now split the two node labels into separate Strings
			String[] temp = edgePairs[x].split(" ");
			
			eu = new EdgeUndirected(nc.getNodeByLabelText(temp[0]), nc.getNodeByLabelText(temp[1]), this, removable);
			if (LogicalPanelUi.getGenre() == "mst") 
				eu.setWeight(temp[2]);
			eu.drawEdge();
			
		}
		
	}
	
//	public void insertGraphEdges(String[] edgePairs, ArrayList<Node> nodes){
//		for(String edgePair: edgePairs){
//			String[]splitEdges = edgePair.split(" ");
//			Node n1 = null;
//			Node n2 = null;
//			int weight = Integer.parseInt(splitEdges[2]);
//			for(int i=0;i<nodes.size();i++){
//				if(nodes.get(i).getValue().equals(splitEdges[0])){
//					n1 = nodes.get(i); 
//				}
//				if(nodes.get(i).getValue().equals(splitEdges[1])){
//					n2 = nodes.get(i);
//				}
//			}
//			EdgeUndirected eu = new EdgeUndirected(n1, n2, getInstance(), handler, removable, weight);
//			eu.drawEdge();	
//			
//			edges.add(eu);
//		}
//		for(EdgeParent eu: edges){
//			((EdgeUndirected)eu).addWeightLabel();
//		}
//	}

	public void clearEdgeNodeSelections() {
		numNodesSelected = 0;
		for (int i = 0; i < edgeHandlers.length; i++) {
			edgeHandlers[i].removeHandler();
		}
	}

	public void addEdgeToCanvas(Line line) {
		line.setStrokeWidth(10);
		panel.add(line);
		
	}

	public void removeEdgeFromCanvas(Line line) {
		panel.remove(line);
		lines.remove(line);
		for (int i = 0; i < edges.size(); i++) {
			if (edges.get(i).getLine().equals(line)) {
				edges.remove(i);
			}
		}
	}

	public void updateEdgeDrawings() {
		for (int i = 0; i < lines.size(); i++) {
			panel.remove(lines.get(i));
			lines.get(i).setX1(edges.get(i).getN1().getLeft());
			lines.get(i).setX2(edges.get(i).getN2().getLeft());
			lines.get(i).setY1(edges.get(i).getN1().getTop());
			lines.get(i).setY2(edges.get(i).getN2().getTop());
			addEdgeToCanvas(lines.get(i));
		}
	}

	private Node getNodeByLabel(Label l) {
		ArrayList<Node> nodes = dm.getNodes();
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getLabel() == l) {
				return nodes.get(i);
			}
		}
		return null;
	}

	public EdgeParent getEdgeByLine(Line line) {
		for (int i = 0; i < edges.size(); i++) {
			if (edges.get(i).getLine().equals(line)) {
				return edges.get(i);
			}
		}
		return null;
	}

	public void resetEdgeColor() {
		for (int i = 0; i < edges.size(); i++) {
			edges.get(i).getLine().setStrokeColor("black");
		}
	}

	public ArrayList<EdgeParent> getEdges() {
		return edges;
	}

	/**
	 * Method: setParentAndChildNodes()
	 * 
	 * Goes through the current set of edges and moves the parent node of the
	 * edge to N1 and the child to N2. This simplifies evaluation and edge
	 * addition.
	 */
	public void setParentAndChildNodes() {
		for (int i = 0; i < edges.size(); i++) {
			EdgeParent edge = edges.get(i);
			if (edge.getN2().getTop() < edge.getN1().getTop()) {
				Node temp = edge.getN2();
				edge.setN2(edge.getN1());
				edge.setN1(temp);
			}
		}
	}

	public void emptyEdges() {
		for (int i = 0; i < lines.size(); i++) {
			panel.remove(lines.get(i));
		}
		lines.clear();
		edges.clear();
	}
//	
//	class EdgeClickListener implements ClickHandler {
//		private Line currElement = null;
//
//		@Override
//		public void onClick(ClickEvent event) {
//			switch (LogicalPanelUi.getGenre()) {
//			case "mst": 
//				if (currElement != null)
//					currElement.setStrokeColor("black");
//				
//				currElement = (Line) event.getSource();
//				currElement.setStrokeColor("#27f500");
//				
//			    EdgeParent currentEdge = getEdgeByLine(currElement);
//			    currentEdge.getN1().getLabel().setStyleName("immobilized_node");
//			    currentEdge.getN2().getLabel().setStyleName("immobilized_node");
//				
//				break;
//			case "traversal":
//				if (currElement != null)
//					currElement.setStrokeColor("black");
//
//				currElement = (Line) event.getSource();
//				currElement.setStrokeColor("yellow");
//
//				Boolean delete = Window.confirm("Do you want to delete this edge?");
//				if (delete == true){	
//						panel.remove(currElement);
//						LogicalPanelUi.setMessage("", Color.None);
//				}
//				break;
//			}
//		}
//	}

	public int getNumNodesSelected() {
		return numNodesSelected;
	}

	public String getSecondInstructions() {
		return nodeSelectionInstructions[1];
	}
	public void addWeightLabel(Label l, int x, int y, EdgeUndirected edge){
		AbsolutePanel toAdd = (AbsolutePanel)panel.getParent();
		
		toAdd.add(l, x, y);
		//graphNodeCollection.addNode(new Node(l.getText(),l));
	}
	public NodeCollection getGraphNodeCollection(){
		return graphNodeCollection;
	}
	public void clearGraphNodeCollection(){
		graphNodeCollection.emptyNodes();
		for(EdgeParent ep: edges){
			((EdgeUndirected)ep).addWeightLabel();
		}
	}
	
	public ArrayList<Line> getLines() {
		return lines;
	}
	
	public void addLine(Line line) {
		lines.add(line);
	}
	
	public void addEdge(EdgeParent edge) {
		edges.add(edge);
	}
	
	public ArrayList<EdgeParent> getMSTClicked() {
		return MSTClicked;
	}
	
	public void removeMSTClicked(EdgeParent toRemove) {
		for (int i = 0; i < MSTClicked.size(); i++) {
			if (toRemove.equals(MSTClicked.get(i))) {
				MSTClicked.remove(i);
				return;
			}
		}
	}
	
	public void setMSTClicked(EdgeParent clicked) {
		MSTClicked.add(clicked);
	}

}