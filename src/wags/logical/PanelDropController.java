package wags.logical;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalPanelUi.Color;

/**
 * Generic drop controller that binds to a SimplePanel, allowing you to customize
 * the size, shape, and locations of drop targets
 * 
 * @author James Corsi
 *
 */
public class PanelDropController extends SimpleDropController {

		private final SimplePanel dropTarget;
		
		public PanelDropController(SimplePanel dropTarget) {
			super(dropTarget);
			this.dropTarget = dropTarget;
			
		}
		
		/**
		 * Dummy constructor for onDrop below
		 */
		public PanelDropController() {
			super(null);
			this.dropTarget = null;
		}
		
		@Override
		public void onDrop(DragContext context) {
			if (dropTarget.getWidget() != null) {
				((SimplePanel) NodeDragController.getInstance().getLastDropController())
					.setWidget((Label)((SimplePanel)context.dropController.getDropTarget()).getWidget());
				dropTarget.setWidget(context.draggable);
			} else {
				dropTarget.setWidget(context.draggable);
			}
		}
		
		@Override
		public void onPreviewDrop(DragContext context) throws VetoDragException {
			// Potential to swap nodes, but only if a drop controller exists for the dragged node, 
			// otherwise we throw a VetoDragException to return node to its previous position
			if (dropTarget.getWidget() != null) {
				try {
					SimplePanel lastDrop = (SimplePanel) NodeDragController.getInstance().getLastDropController();
				} catch (Exception e) {
					throw new VetoDragException();
				}
			}
			super.onPreviewDrop(context);
		}
		
		/**
		 * Set the border on enter of a drop target...
		 */
		@Override
		public void onEnter(DragContext context) {
			dropTarget.getElement().getStyle().setProperty("border", "2px dashed #1e90ff");
		}
		
		/**
		 * ...and remove the border on leave.
		 */
		@Override
		public void onLeave(DragContext context) {
			dropTarget.getElement().getStyle().setProperty("border", "none");
		}
		
		@Override
		public SimplePanel getDropTarget() {
			return dropTarget;
		}
	
}
