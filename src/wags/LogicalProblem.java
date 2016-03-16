package wags;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;

import wags.logical.AddEdgeRules;
import wags.logical.DSTConstants;
import wags.logical.Evaluation;
import wags.logical.Node;

public class LogicalProblem{
	
	public int id, group;
	public String title, directions, nodes, xPositions, 
		yPositions, insertMethod, edges, arguments, nodeType, 
		genre;
	public boolean edgesRemovable, nodesDraggable;
	public Evaluation evaluation;
	public AddEdgeRules edgeRules;
	
	public LogicalProblem(int id, String title, String problemText, String nodes,
			String xPositions, String yPositions, String insertMethod,
			String edges, String arguments, int evaluation, Integer edgeRules,
			Integer edgesRemovable, int nodesDraggable, String nodeType, String genre,
			int group) {

		this.id = id;
		this.title = title;
		this.directions = problemText.replace("\\", "");
		this.nodes = nodes;
		this.xPositions = xPositions;
		this.yPositions = yPositions;
		this.insertMethod = insertMethod;
		this.edges = edges;
		this.arguments = arguments;
		this.evaluation = DSTConstants.getEvaluation(evaluation);
		this.edgeRules = DSTConstants.getEdgeRules(edgeRules);
		this.edgesRemovable = (edgesRemovable == 1) ? true : false;
		this.nodesDraggable = (nodesDraggable == 1) ? true : false;
		this.nodeType = nodeType;
		this.genre = genre;
		this.group = group;
	}
	
}
