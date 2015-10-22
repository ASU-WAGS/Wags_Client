package wags.logical;


import java.util.ArrayList;

import org.vaadin.gwtgraphics.client.Line;

import wags.logical.view.LogicalProblem;
import wags.logical.TreeProblems.TreeDisplayManager;
import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalPanelUi.Color;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Label;


public class EdgeUndirected extends EdgeParent implements IsSerializable
{
	
	public boolean removable;
	public Label weightLabel;
	private static final int OFFSET = 20;
	
	public void addWeightLabel(){
		// Get weight location by average of tops and lefts, minus an offset of 20px for size of node 
		int midVert = ((n1.getTop() + n2.getTop()) / 2) - OFFSET;
		int midHoriz = ((n1.getLeft() + n2.getLeft()) / 2) - OFFSET;
		
		weightLabel.setStyleName("edge_weight");
		ec.addWeightLabel(weightLabel, midHoriz, midVert, this);
	}
	public EdgeUndirected(){super(null,null,null,null,false);}
	
	public EdgeUndirected(Node n1, Node n2, EdgeCollection ec, ClickHandler ch, boolean removable)
	{
		super(n1, n2, ec, ch, removable);
	}
	
	public EdgeUndirected(Node n1, Node n2, EdgeCollection ec, boolean removable) {
		super(n1, n2, ec, null, removable);	
	}
	
	public EdgeUndirected(Node n1, Node n2, EdgeCollection ec)
	{
		super(n1, n2, ec, null, false);
	}
	public EdgeUndirected(Node n1, Node n2, EdgeCollection ec, ClickHandler ch, boolean removable, int weight)
	{
		super(n1, n2, ec, ch, removable, weight);
	}
	
	public EdgeUndirected(EdgeCollection ec, ClickHandler ch, boolean removable) {
		super(null,null,ec,ch,removable);
	}

	public EdgeUndirected(EdgeCollection ec, boolean removable) {
		super(null,null,ec,null,removable);
	}
	
	@Override
	public void drawEdge() {

		if(n1 != null && n2 != null) {
		line = new Line(n1.getLeft(), 
						n1.getTop(),
						n2.getLeft(), 
						n2.getTop());
		
		
		if (LogicalPanelUi.edgesRemovable()) {
			line.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Line selected = (Line)event.getSource();
					selected.setStrokeColor("yellow");
					//REMOVEEDGES
					if(Window.confirm("Would you like to remove this edge?")) { 
						ec.removeEdgeFromCanvas(selected);
						if (n1 != null && n2 != null) {
							if (n1.getTop() > n2.getTop()) {
								if (n1.getLeftChild().equals(n2))
									n1.setLeftChild(null);
								else 
									n1.setRightChild(null);
							} else {
								if (n2.getLeftChild().equals(n1))
									n2.setLeftChild(null);
								else 
									n2.setRightChild(null);
							}
						}
					}
					else
						selected.setStrokeColor("#444");
						
				}
			});
		}

		
		if (LogicalPanelUi.getGenre() == "mst") {			
			addWeightLabel();
					
			// Add ClickHandler to change selected/not selected edges
			line.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Line selected = (Line)event.getSource();
					EdgeParent selectedEdge = ec.getEdgeByLine(selected);
					
					// Change colour to selected only if not yet selected
					if (selected.getStrokeColor().equals("#444")) { 
						selected.setStrokeColor("#27f500");
						 
						
						ec.setMSTClicked(selectedEdge);
						
						getN1().getLabel().setStyleName("immobilized_node");
						getN2().getLabel().setStyleName("immobilized_node");
					    
					    weightLabel.setStyleName("selected_edge_weight");
					    writeMST();
					} else {
						selected.setStrokeColor("#444");
						
						ec.removeMSTClicked(selectedEdge);
						
						if (!getN1().MSTSelected(line))
							getN1().getLabel().setStyleName("node");
						if (!getN2().MSTSelected(line))
							getN2().getLabel().setStyleName("node");
						
						weightLabel.setStyleName("edge_weight");
						writeMST();
					}
				}
			});
			
			// Call ClickHandler above^ when edge label is clicked
			weightLabel.addClickHandler(new ClickHandler() {	
				public void onClick(ClickEvent event) {
					line.fireEvent(event);
				}});
		}
		
		line.setStrokeColor("#444");
		line.setStrokeWidth(3);
		super.setLine(line);
		ec.addEdge(this);
		ec.addLine(line);
		ec.addEdgeToCanvas(line);
		}	
	}
	
	public void setWeight(String weight) {
		super.setWeight(Integer.parseInt(weight));
		weightLabel = new Label(weight);
	}
	
	@Override
	public String getWeightedEdge() {
		return weightLabel.getText();
	}
	
	/**@Override
	public void drawEdges(int[][] lineDims) {		
		
		//Create edges using 2D array of line dimensions passed in by EdgeCollection
		for (int i = 0; i < lineDims.length; i++) {
			line = new Line(lineDims[i][0],
							lineDims[i][1],
							lineDims[i][2],
							lineDims[i][3]);
			switch (LogicalPanelUi.getGenre()) {
			case "traversal":
			case "heapInsert":
				if (removable) {
					line.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							Line selected = (Line)event.getSource();
							selected.setStrokeColor("yellow");
							if(Window.confirm("Would you like to remove this edge?")) 
								LogicalPanelUi.getPanel().remove(selected);
							else
								selected.setStrokeColor("#444");
						}
					});
				}
				break;
			}
			line.setStrokeColor("#444");
			line.setStrokeWidth(3);
			super.setLine(line);
			//addWeightLabel();
			ec.addEdgeToCanvas(line);
		}
	}*/
	
	private void writeMST() {
		ArrayList<EdgeParent> MSTClicked = ec.getMSTClicked();
		String toWrite = "Current traversal:\t";
		
		for (int i = 0; i < MSTClicked.size(); i++) {
			toWrite += MSTClicked.get(i).getWeightedEdge();
			if (i < MSTClicked.size() - 1) {
				toWrite  += ", ";
			}
		}
		if (MSTClicked.size() > 0) 
			LogicalPanelUi.setMessage(toWrite, Color.Notification);
		else
			LogicalPanelUi.setMessage("", Color.None);
	}
}
