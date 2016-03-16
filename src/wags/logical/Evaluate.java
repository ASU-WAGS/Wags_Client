package wags.logical;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.gwtbootstrap3.client.ui.Column;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import wags.logical.RadixState;
import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalProblemCreator;
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
	
	public boolean traversalEvaluate(NodeCollection nc, EdgeCollection ec, boolean forceEval) {
		LinkedHashSet<Node> trav = LogicalProblemCreator.trav;
		if (trav != null) {
			String[] compare = args[0].split("\\s");
			
			if (forceEval) {
				
				boolean correct = true;
				if (trav.size() == 0) {
					LogicalPanelUi.setMessage("", Color.None);
					return false;
				}
				String fix = "",currTrav = "";
				int i = 0;
				for (Node n : trav) {
					currTrav += n.toString() + " ";
					if (!n.toString().equals(compare[i]))
						correct = false;
					if (!correct) 
						fix += n.toString() + " ";
					i++;
				}
				
				if (!correct) {
					LogicalPanelUi.setMessage("", Color.Warning);
					LogicalPanelUi.message.setHTML("Current Traversal: " + currTrav +  "<br>Node(s) " + fix 
							+ "have been clicked out of order. Deselect the node(s) and try again");
				} else if (trav.size() != compare.length) {
						LogicalPanelUi.setMessage("Current Traversal: " + currTrav, Color.Notification);
						correct = false;
				} else {
					LogicalPanelUi.setMessage(CORRECT, Color.Success);
				}
					
				return correct;
			} else {				
				if (compare.length != trav.size()) {
					LogicalPanelUi.setMessage("Your traversal is incomplete. Every node must be clicked once to complete a traversal,", Color.Error);
					return false;
				}
				
				boolean correct = true;
				String curr = "";
				
				int i = 0;
				for (Node cmp : trav) {
					if (cmp.toString() != compare[i])
						correct = false;
					if (correct)
						curr += cmp.toString() + " ";
					i++;
				}
				
				if (correct)
					LogicalPanelUi.setMessage(CORRECT, Color.Success);
				else if (curr.equals(""))
					LogicalPanelUi.setMessage("Incorrect. For the given traversal, what would be the first visited node?", Color.Error);
				else 
					LogicalPanelUi.setMessage("The nodes are out of order. The portion " + curr + "is correct. Retrace your steps to find your mistake.", Color.Error);
				
				return correct;
			}
		} else {
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
	
	public boolean simplePartitionEvaluate(ArrayList<Column> cols) {
		boolean pointersJoined = false;
		int num = -1;
		
		for (Column col : cols) {
			if (col.getWidgetCount() > 3) 
				pointersJoined = true;
			int numcheck = Integer.parseInt(((Label) ((SimplePanel) col.getWidget(1)).getWidget()).getText());
			
			// if previous one is not negative, this one should not be negative either
			if (num > 0 && numcheck < 0) {
				String error = "Incorrect. Remember that all negatives should be to the left of all positives";
				LogicalPanelUi.setMessage(error, Color.Error);
				return false;
			}
			num = numcheck;
		}
		
		if (pointersJoined) {
			LogicalPanelUi.setMessage(CORRECT, Color.Success);
			return true;
		} else {
			String error = "Remember that partitioning does not finish until the pointers 'cross-over' or become equal";
			LogicalPanelUi.setMessage(error, Color.Error);
			return false;
		}
	}
}
