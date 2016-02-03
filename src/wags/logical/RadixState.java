package wags.logical;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

import wags.logical.PanelDropController;
import wags.logical.view.LogicalPanelUi;
import wags.logical.view.LogicalProblem;

public class RadixState { 
	
	// State for radix positions and dequeues 
	public static enum State {
		Ones, DOnes, Tens, DTens, Hundreds, DHundreds, End
	}
	
	public boolean complete;
	private State state;
	
	
	public RadixState() {
		state = State.Ones;
		complete = false;
	}
	
	public boolean advance(boolean sorted) {
		if (sorted && !complete) {
			state = State.values()[state.ordinal() + 1];
			PanelDropController[] deq = LogicalPanelUi.getDequeueDrops();
			PanelDropController[] rad = LogicalPanelUi.getRadixDrops();
			
			if (state.ordinal() % 2 == 1) {
				NodeDragController.getInstance().unregisterDropControllers();
				for (PanelDropController deqPan : deq)
					NodeDragController.getInstance().registerDropController(deqPan);

				if (state == State.DOnes) {  // only want to set clickHandler once, otherwise weird things happen
					for (final Node n : LogicalProblem.getNodeCollection().getNodes()) {
						
						// This clickHandler is applied to each node to automatically dequeue when clicked
						n.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent e) {
								for (PanelDropController deqPan : LogicalPanelUi.getDequeueDrops()) {
									// Note: here, getWidget will be null (undefined in JS) if there is no node 
									// bound to the dropPanel, so we'll add the clicked node in the open spot
									if (((SimplePanel) deqPan.getDropTarget()).getWidget() == null) {
										((SimplePanel) deqPan.getDropTarget()).setWidget(n.getLabel());
										break;
									}
								}
							}
						});
					}
				}
			} else {
				NodeDragController.getInstance().unregisterDropControllers();
				for (PanelDropController radPan : rad)
					NodeDragController.getInstance().registerDropController(radPan);
			}
		}
		if (state == State.End) {
			complete = true;
		}
		return sorted;
	}
	
	public void reset(wags.LogicalProblem logProb) {
		String[] args = logProb.arguments.split(",");
		
		PanelDropController[] deq = LogicalPanelUi.getDequeueDrops();
		PanelDropController[] rad = LogicalPanelUi.getRadixDrops();
		
		// reset all radix panels to prevent them from shifting oddly when reset multiple times
		for (PanelDropController radPan : rad)
			radPan.getDropTarget().setWidget(null);
		
		int[] positions = new int[10];
		for (int i = 0; i < 10; i++) 
			positions[i] = deq[i].getDropTarget().getAbsoluteLeft();
		
		int i = 0;
		
		switch (getCurrentState()) {
		case Ones:
			for (Node n : LogicalProblem.getNodeCollection().getNodes()) {
				deq[i].getDropTarget().setWidget(n.getLabel());
				i++;
			}
			break;
		case DOnes:
			for (String str : args[3].split(" ")) {
				i = (Integer.parseInt(str) % 10);
				for (PanelDropController radPan : rad) {
					if ((radPan.getDropTarget().getAbsoluteLeft() == positions[i]) 
							&& (radPan.getDropTarget().getWidget() == null)) {
						radPan.getDropTarget().setWidget(LogicalProblem.getNodeCollection().getNodeByLabelText(str).getLabel());
						break;
					}
				}
			}
			break;
		case Tens:
			for (String str : args[3].split(" ")) {
				deq[i].getDropTarget().setWidget(LogicalProblem.getNodeCollection()
						.getNodeByLabelText(str).getLabel());
				i++;
			}
			break;
		case DTens:
			for (String str : args[4].split(" ")) {
				i = ((Integer.parseInt(str) / 10) % 10);
				for (PanelDropController radPan : rad) {
					if ((radPan.getDropTarget().getAbsoluteLeft() == positions[i]) 
							&& (radPan.getDropTarget().getWidget() == null)) {
						radPan.getDropTarget().setWidget(LogicalProblem.getNodeCollection().getNodeByLabelText(str).getLabel());
						break;
					}
				}
			}
			break;
		case Hundreds:
			for (String str : args[4].split(" ")) {
				deq[i].getDropTarget().setWidget(LogicalProblem.getNodeCollection()
						.getNodeByLabelText(str).getLabel());
				i++;
			}
			break;
		case DHundreds:
			for (String str : args[5].split(" ")) {
				i = ((Integer.parseInt(str) / 100) % 10);
				for (PanelDropController radPan : rad) {
					if ((radPan.getDropTarget().getAbsoluteLeft() == positions[i]) 
							&& (radPan.getDropTarget().getWidget() == null)) {
						radPan.getDropTarget().setWidget(LogicalProblem.getNodeCollection().getNodeByLabelText(str).getLabel());
						break;
					}
				}
			}
			break;
		case End:
			Window.Location.reload();
			break;
		}
	}
	
	public State getCurrentState() {
		return state;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
}
