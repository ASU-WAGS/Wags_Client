package wags.logical;

import java.util.ArrayList;

import org.gwtbootstrap3.client.ui.Column;

import com.google.gwt.user.client.Window;

import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalPanelUi.Color;

public class Evaluate {

	private final String CORRECT = "Correct! Please click reset if you'd like to try again.";
	private String[] args;
	
	public Evaluate(String[] arguments) {
		args = arguments;
	}
	
	public boolean hashingEvaluate(NodeCollection nc, ArrayList<Column> grid) {
		boolean correct = true;
		for (int i = 1; i < args.length; i++) {
			String[] arguments = args[i].split("\\s");   // arguments[0] is label, arguments[1] is cell number
			int[] cellPos = {grid.get(Integer.parseInt(arguments[1])).getWidget(1).getAbsoluteLeft(), 
					grid.get(Integer.parseInt(arguments[1])).getWidget(1).getAbsoluteTop()};
			int[] labelPos = {nc.getNodeByLabelText(arguments[0]).getLabel().getAbsoluteLeft(), 
					nc.getNodeByLabelText(arguments[0]).getLabel().getAbsoluteTop()};
			if (labelPos[0] != cellPos[0] || labelPos[1] != cellPos[1]) {
				correct = false;
			}
		}		
		if (correct) {
			LogicalPanelUi.setMessage(CORRECT, Color.Success);
		} else {
			LogicalPanelUi.setMessage("Incorrect; not all of the nodes were in the correct positions.", Color.Error);
		}
		return correct;
	}
	
	public boolean heapEvaluate(NodeCollection nc, EdgeCollection ec) {
		boolean incorrect = true;
		
		String lateralResult = nc.getTraversal(3, ec.getEdges());
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replace(" ", "");
			if (args[i].equalsIgnoreCase(lateralResult)) {
				incorrect = false;
			}
		}
		if (incorrect) {
			LogicalPanelUi.setMessage("Incorrect; make sure your edges and nodes are in the correct positions.", Color.Error);
		} else {
			LogicalPanelUi.setMessage(CORRECT, Color.Success);
		}
		return !incorrect;
	}
	
	public boolean traversalEvaluate(NodeCollection nc, EdgeCollection ec) {
		boolean incorrect = true;
		String preorderResult = nc.getTraversal(0, ec.getEdges());
		String inorderResult = nc.getTraversal(1, ec.getEdges());
		String postorderResult = nc.getTraversal(2, ec.getEdges());
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replace(" ", "");
			if (args[i].equalsIgnoreCase(preorderResult)) { 
				LogicalPanelUi.setMessage(CORRECT, Color.Success);
				incorrect = false;
				break;
			}
			else if(args[i].equalsIgnoreCase(inorderResult)) {
				LogicalPanelUi.setMessage(CORRECT, Color.Success);
				incorrect = false;
				break;
			}
			else if(args[i].equalsIgnoreCase(postorderResult)) {
				LogicalPanelUi.setMessage(CORRECT, Color.Success);
				incorrect = false;
				break;
			}
		}
		if (incorrect) {
			LogicalPanelUi.setMessage("Incorrect! Your preorder traversal was: " + preorderResult + " and your inorder traversal was: " + inorderResult +"",Color.Error);
		}
		else {
			LogicalPanelUi.setMessage(CORRECT, Color.Success);
		}
		return !incorrect;
	}	
	
	public boolean mstEvaluate(NodeCollection nc, EdgeCollection ec) {
		boolean correct = true;
		ArrayList<EdgeParent> clickedEdges = ec.getMSTClicked();
		String edgeLabel = "";
		
		for (int i = 0; i < args.length; i++) {
			
			try { 
				edgeLabel = clickedEdges.get(i).getWeightedEdge();
			} catch (Exception e) {
				LogicalPanelUi.setMessage("Your MST is incorrect; make sure you're reaching every node", Color.Error);
				return false;
			}
			
			if (args[i] != edgeLabel) {
				LogicalPanelUi.setMessage("Your MST is incorrect; make sure you're reaching every node", Color.Error);
				return false;
			}
		}
	
		LogicalPanelUi.setMessage(CORRECT, Color.Success);
		return correct;
	}
}
