package wags.logical;

import java.util.ArrayList;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalPanelUi.Color;

public class NodeDragController extends PickupDragController implements IsSerializable
{
	private static NodeDragController dc;
	private static AbsolutePanel boundaryPanel; 
	private static EdgeCollection ec;
	private static boolean allowDroppingOnBoundaryPanel;
	private static ArrayList<DropController> drops;
	private Widget currentDropController;
	
	public static NodeDragController getInstance()
	{
		if (dc == null)
			dc = new NodeDragController(boundaryPanel, allowDroppingOnBoundaryPanel);
		if (drops == null)
			drops = new ArrayList<DropController>();
			
		dc.setBehaviorDragStartSensitivity(1); // make double clicking work
		
		return dc;
	}
	
	private NodeDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel)
	{
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
		drops = new ArrayList<DropController>();
	}
	
	public static void setFields(AbsolutePanel theBoundaryPanel,
			boolean allowDrop, EdgeCollection theEc) 
	{
		boundaryPanel = theBoundaryPanel;
		ec = theEc;
		allowDroppingOnBoundaryPanel = allowDrop;
		dc = new NodeDragController(boundaryPanel, allowDroppingOnBoundaryPanel);
	}
	
	@Override
	public void previewDragEnd() throws VetoDragException {
		super.previewDragEnd();
	}
	
	@Override 
	public void registerDropController(DropController dropController) {
		super.registerDropController(dropController);
		drops.add(dropController);
	}
	
	@Override 
	public void unregisterDropController(DropController dropController) {
		super.unregisterDropController(dropController);
		drops.remove(dropController);
	}
	
	@Override
	public void unregisterDropControllers() {
		super.unregisterDropControllers();
		drops.clear();
	}
	
	@Override
	public void dragStart() {
		currentDropController = context.draggable.getParent();	
		super.dragStart();
	}
	
	@Override
	public void dragEnd()
	{
		super.dragEnd();
		LogicalPanelUi.setMessage("", Color.None);
	}
	
	public static ArrayList<DropController> getDropControllers() {
		return drops;
	}
	
	public Widget getLastDropController() {
		return currentDropController;
	}
	
	public void reset() {
		dc = null;		
	}
}
