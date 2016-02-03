package wags.logical;

import java.util.ArrayList;
import java.util.LinkedList;

import org.gwtbootstrap3.client.ui.Column;

import com.google.gwt.user.client.Window;

import wags.logical.RadixState;
import wags.logical.RadixState.State;
import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalPanelUi.Color;

public class Evaluate {

	private final String CORRECT = "Correct! Please click reset if you'd like to try again.";
	private final String INCDEQUEUE = "You have dequeued in the wrong order. "
			+ "Remember to dequeue the buckets from lowest number to highest number, top to bottom.";
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
	
	public boolean mstEvaluate(NodeCollection nc, EdgeCollection ec) {
		boolean correct = true;
		ArrayList<EdgeParent> clickedEdges = ec.getMSTClicked();
		String edgeLabel = "";
		if (clickedEdges.size() != args.length) { // if not same number of args, don't bother checking
			LogicalPanelUi.setMessage("Your MST is incorrect; make sure you're reaching every node", Color.Error);
			return false;
		}
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
	
	public boolean radixEvaluate(NodeCollection nc, int[] positions, RadixState state) {
		
		LinkedList<Node>[] nodes = (LinkedList<Node>[]) new LinkedList<?>[10];
		
		for (int i = 0; i < nc.size(); i++) {
			for (int j = 0; j < positions.length; j++) {
				if (nc.getNode(i).getLabel().getAbsoluteLeft() == positions[j]) {
					if (nodes[j] == null) 
						nodes[j] = new LinkedList<Node>();
					nodes[j].add(nc.getNode(i));
					break;
				}
			}
		}
		
		switch (state.getCurrentState()) {
		case Ones:
			state.advance(checkRadix(3, nc, positions));
			break;
		case DOnes:
			if (state.advance(checkDequeue(3, nc, positions))) {
				LogicalPanelUi.incrementRadixCounter();
			}
			break;
		case Tens:
			state.advance(checkRadix(4, nc, positions));
			break;
		case DTens:
			if (state.advance(checkDequeue(4, nc, positions))) {
				LogicalPanelUi.incrementRadixCounter();
			}
			break;
		case Hundreds:
			state.advance(checkRadix(5, nc, positions));
			break;
		case DHundreds:
			if (state.advance(checkDequeue(5, nc, positions))) {
				LogicalPanelUi.setMessage(CORRECT, Color.Success);
				return true;
			}
			break;
		case End:
			LogicalPanelUi.setMessage(CORRECT, Color.Success);
			return true;
		}
		
		return false;
	}
	
	private boolean checkDequeue(int argNum, NodeCollection nc, int[] positions) {
		String[] nodes = args[argNum].split("\\s");
		
		for (int i = 0; i < nodes.length; i++) {
			if (nc.getNodeByLabelText(nodes[i]).getLabel().getAbsoluteLeft() != positions[i]) {
				LogicalPanelUi.setMessage(INCDEQUEUE, Color.Error);
				return false;
			}
		}
		
		LogicalPanelUi.setMessage("Correct! Proceed to the next stage of the radix sort.", Color.Notification);
		return true;
	}
	
	private boolean checkRadix(int argNum, NodeCollection nc, int[] positions) {
		String[] checkPos = args[argNum].split(" ");
		int radix = 1;
		
		if (argNum == 4) 
			radix = 10;
		else if (argNum == 5)
			radix = 100;
		
		for (int i = 0; i < nc.size(); i++) {
			int num = Integer.parseInt(nc.getNode(i).toString());
			num = ((num / radix) % 10);
			if (nc.getNode(i).getLabel().getAbsoluteLeft() != positions[num]) {
				LogicalPanelUi.setMessage("The node " + nc.getNode(i).toString()
						+ " is not in the correct bucket.", Color.Error);
				return false;
			} 
		}
		
		// Logic for checking position inside of buckets
		for (int i = 0; i < 10; i++) {
			ArrayList<String> withNum = new ArrayList<String>();
			for (String str : checkPos) {
				if (((Integer.parseInt(str) / radix) % 10) == i) {
					withNum.add(str);
				}
			}
			for (int j = 0; j < withNum.size() - 1; j++) {
				if (nc.getNodeByLabelText(withNum.get(j)).getLabel().getAbsoluteTop() >
						nc.getNodeByLabelText(withNum.get(j + 1)).getLabel().getAbsoluteTop()) {
					LogicalPanelUi.setMessage("The node " + withNum.get(j) + " is not in the correct place in its column. Check the order " 
					+ "of each radix column and make sure you place the nodes as you come to them.", Color.Error);
					return false;
					
				}
			}
		}
		
		LogicalPanelUi.setMessage("Correct!  Now dequeue the nodes in the correct order.", Color.Notification);
		return true;
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
}
