package wags.logical;

import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalPanelUi.Color;
import wags.logical.view.LogicalProblem;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;

public class NodeDropController extends AbsolutePositionDropController implements IsSerializable {

	private static NodeDropController dc;
	private static EdgeCollection ec;
	private static AbsolutePanel boundaryPanel;
	
	public static NodeDropController getInstance()
	{
		if(dc == null)
			dc = new NodeDropController(boundaryPanel);
		return dc;
	}
	
	private NodeDropController(AbsolutePanel boundaryPanel)
	{
		super(boundaryPanel);
	}
		
	public static void setFields(AbsolutePanel theBoundaryPanel, EdgeCollection theEc)
	{
		boundaryPanel = theBoundaryPanel;
		ec = theEc;
		dc = new NodeDropController(boundaryPanel);
	}
 
	@Override
	public void onDrop(DragContext context)
	{
		NodeCollection nc = LogicalProblem.getNodeCollection();
		super.onDrop(context);
		if (ec != null) {
			if (context.draggable.getStylePrimaryName() == "node") { //is a node, and can be treated as one
				Node n = nc.getNodeByLabel((Label) context.draggable);
				Label l = (Label) context.draggable;
				n.setLeft(l.getElement().getAbsoluteLeft() - l.getElement().getOffsetParent().getAbsoluteLeft());
				n.setTop(l.getElement().getAbsoluteTop() - l.getElement().getOffsetParent().getAbsoluteTop());
			}
			ec.updateEdgeDrawings();
			LogicalPanelUi.setMessage("", Color.None);
		}
	}
	
	public void reset() {
		dc = null;
	}
	
}
