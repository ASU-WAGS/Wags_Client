package wags.logical.view;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconRotate;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;

import wags.LogicalProblem;
import wags.logical.Evaluate;
import wags.logical.Node;
import wags.logical.NodeCollection;
import wags.logical.NodeDragController;
import wags.logical.NodeDropController;
import wags.logical.PanelDropController;
import wags.logical.RadixState;
import wags.logical.view.LogicalPanelUi.Color;
import wags.logical.view.LogicalProblem.LogicalProblemUiBinder;

public class LogicalProblemCreator {

	protected AbsolutePanel canvasContain;
	private static LogicalProblem problem;
	public static NodeCollection nc;
	public static ArrayList<Column> cols = new ArrayList<Column>();
	public static LinkedHashSet<Node> trav;
	
	public LogicalProblemCreator() {
		
	}
	
	public LogicalPanel makeProblem(LogicalProblem problem) {
		
		this.problem = problem;
		return new LogicalPanel(
				problem,
				getCanvasContain(),
				getNodes()
		);
		
	}
	
	private AbsolutePanel getCanvasContain() {
		return canvasContain;
	}
	
	public static NodeCollection getNodes() {
		return nc;
	}
	
	private void setNodes(NodeCollection nc) {
		this.nc = nc;
	}
	
	public static void buildHashingPanel(Container hashingBoxes, LogicalProblem logProb, ArrayList<Column> grid, AbsolutePanel dragPanel) {
		hashingBoxes.removeFromParent();
		Row row = new Row();
		for (int i = 0; i < Integer.parseInt(logProb.arguments.split(",")[0]); i++) {
			if (i % 12 == 0) {
				row = new Row();
				row.setStyleName("hashing_row");
				row.setVisible(true);
				hashingBoxes.add(row);
			}
			Column col = new Column(ColumnSize.MD_1, new Label("" + i));
			col.setStyleName("hashing_column");
			row.add(col);
			SimplePanel dropPanel = new SimplePanel();   // drop target for each cell
			dropPanel.setPixelSize(40, 40);
			dropPanel.getElement().getStyle()
				.setProperty("margin", "2.5px"); // account for 40x40 node in 50x50 col
			
			// This line adds the dropPanel to the cell, making the cell appear to be a drop zone
			col.add(dropPanel);
			// Add column to grid ArrayList, for later evaluation
			grid.add(col);
			
			// set drop controller to dropPanel
			PanelDropController dropController = new PanelDropController(dropPanel);
			NodeDragController.getInstance().registerDropController(dropController);
		}
		dragPanel.add(hashingBoxes);
		hashingBoxes.setVisible(true);
	}
	
	public static void buildRadixPanel(AbsolutePanel dragPanel, AbsolutePanel radixDrop, AbsolutePanel dequeueDrop) {
		
			LogicalPanelUi.state = new RadixState();
			PanelDropController[] radixDrops = LogicalPanelUi.getRadixDrops();
			PanelDropController[] dequeueDrops = LogicalPanelUi.getDequeueDrops();
			Container radixContain = LogicalPanelUi.radixContain;
			
			radixContain.removeFromParent();
			int tenth = (int)((Window.getClientWidth() * .47) * .1);
			for (int i = 0; i < 10; i++) {
				Label tmp = new Label("" + i);         // column number
				tmp.getElement().getStyle().setProperty("margin", "0"); // fix centering issues
				Column col = new Column(ColumnSize.LG_1, tmp);
				radixDrop.add(col);
				col.setStyleName("radix_column");
				col.setVisible(true);
				
				for (int j = 0; j < 9; j++) {
					
					// set how much space is necessary for alignment - tenth of panel (not counting node)
					int alignOffset = (tenth > 40) ? ((tenth - 40) / 2) : 60; 
					
					int left = (int)((tenth * i) + alignOffset);
					
					// if the droptarget is for the dequeuing operation rather than enqueuing
					if (j == 0) {
						SimplePanel dropPanel = new SimplePanel();
						dropPanel.setPixelSize(40, 40);
						dequeueDrop.add(dropPanel, left, 5);
						PanelDropController dropController = new PanelDropController(dropPanel);
						// we don't register dequeuing drop controllers, however; wait until it's
						// time to dequeue
						LogicalPanelUi.dequeueLocs[i] = dropPanel;
						dequeueDrops[i] = dropController;
						radixDrops[i * 9] = dropController;
					}
					
					SimplePanel dropPanel = new SimplePanel();
					dropPanel.setPixelSize(40, 40);
						
					// for the next line, we add the dropTarget to the dragPanel at a location of 
					// 1/10th the width of the total dragPanel and a 1/4th offset (to centre the node)
					radixDrop.add(dropPanel, left, (j * 50));
					PanelDropController dropController = new PanelDropController(dropPanel);
					NodeDragController.getInstance().registerDropController(dropController);
					int loc = (i * 9) + j;
					radixDrops[loc] = dropController;
			
				}
				
			}
			
			radixContain.add(dequeueDrop);
			radixContain.add(radixDrop);
			dragPanel.add(radixContain);
			radixContain.setVisible(true);
			
			LogicalPanelUi.radixCounter.setText("Current Position: Ones");
			LogicalPanelUi.radixCounter.setVisible(true);
		}
	
	public static void buildSimplePartitionPanel(Container sortBoxes, LogicalProblem logProb, AbsolutePanel dragPanel) {
		sortBoxes.removeFromParent();
		sortBoxes.getElement().getStyle().setProperty("margin-top", "20px");
		dragPanel.add(sortBoxes);
		Row row = new Row();
		String[] nodes = logProb.nodes.split("\\s");
		
		for (int i = 0; i < nodes.length; i++) {
			row.setStyleName("partition_row");
			row.setVisible(true);
			sortBoxes.add(row);
			Label colnum = new Label("" + i);
			colnum.getElement().getStyle().setProperty("margin", "0 0 0 1.5em");
			Column col = new Column(ColumnSize.MD_1, colnum);
			col.setStyleName("partition_column");
			row.add(col);
			SimplePanel cell = new SimplePanel();
			cell.setPixelSize(60, 40);
			cell.getElement().getStyle()
				.setProperty("margin", "1em 0px 0px .5em");
			Label node = new Label(nodes[i]);
			node.setStyleName("node");
			cell.add(node);
			cell.setVisible(true);
			col.add(cell);
			
			Icon selected = new Icon(IconType.ARROW_UP);
			selected.setSize(IconSize.THREE_TIMES);
			
			// gets rid of weird shadow, not sure why Bootstrap does that with its icons
			selected.getElement().getStyle().setProperty("background-image", "none"); 
			
			selected.getElement().getStyle().setProperty("margin", "70% 0 0 20%");
			if (i == 0) {
				selected.getElement().getStyle().setProperty("color", "blue");
				col.add(selected);
			} else if (i == nodes.length - 1) {
				selected.getElement().getStyle().setProperty("color", "red");
				col.add(selected);
			}
			cols.add(col);
		}
		
		addLeftRightButtons(dragPanel);
		
		sortBoxes.setVisible(true);
		
	}
	
	public static void buildTraversalPanel(final boolean eval, NodeCollection nodes) {
		nc = nodes;
		trav = new LinkedHashSet<Node>(nc.size());
		for (Node node : nc) {
			node.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent e) {
					Label clicked = (Label) e.getSource();
					if (clicked.getStyleName().equals("node")) {
						clicked.removeStyleName("node");
						clicked.addStyleName("selected_node");
						trav.add(nc.getNodeByLabel(clicked));
						String progress = "";
						for (Node element : trav)
							progress += element.toString() + " ";
						LogicalPanelUi.setMessage(progress, Color.Notification);
					} else {
						clicked.removeStyleName("selected_node");
						clicked.addStyleName("node");
						trav.remove(nc.getNodeByLabel(clicked));
						String progress = "";
						for (Node element : trav)
							progress += element.toString() + " ";
						LogicalPanelUi.setMessage(progress, Color.Notification);
					}
					if (eval) 
						LogicalPanelUi.evaluateButton.click();
				}
			});
		}
	}

	
	private static void addLeftRightButtons(AbsolutePanel dragPanel) {
		Icon leftIcon = new Icon(IconType.ARROW_UP), rightIcon = new Icon(IconType.ARROW_UP);
		
		leftIcon.setSize(IconSize.FIVE_TIMES);
		rightIcon.setSize(IconSize.FIVE_TIMES);
		
		leftIcon.setRotate(IconRotate.ROTATE_90);
		rightIcon.setRotate(IconRotate.ROTATE_270);
		
		leftIcon.getElement().getStyle().setProperty("background-image", "none");
		rightIcon.getElement().getStyle().setProperty("background-image", "none");
		rightIcon.getElement().getStyle().setProperty("float", "right");
		rightIcon.getElement().getStyle().setProperty("margin-right", "10%");
		leftIcon.getElement().getStyle().setProperty("color", "blue");
		rightIcon.getElement().getStyle().setProperty("color", "red");
		leftIcon.getElement().getStyle().setProperty("margin-bottom", "50px");
		rightIcon.getElement().getStyle().setProperty("margin-bottom", "50px");
		
		// create clickHandler for moving arrows left and right;
		// no clickHandler for icons, so we'll make our own
		leftIcon.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int i = 0;
				for (Column col : cols) {
					if (col.getWidgetCount() > 3) {
						// feel free to do something here, idk
						break;
					} else if (col.getWidgetCount() > 2) {
						Widget arrow = col.getWidget(2);
						arrow.removeFromParent();
						cols.get(i + 1).add(arrow);
						if (cols.get(i + 1).getWidgetCount() > 3) {
							cols.get(i + 1).getWidget(2).getElement().getStyle().setProperty("margin", "70% 0 0 0");
							cols.get(i + 1).getWidget(3).getElement().getStyle().setProperty("margin", "70% 0 0 0");
						}
						break;
					}
					i++;
				}
			}
		}, ClickEvent.getType());
		
		Button swapButton = new Button("Swap nodes", new ClickHandler() {
			public void onClick(ClickEvent event) {
				int swap1 = -1;
				for (int i = 0; i < cols.size(); i++) {
					if (cols.get(i).getWidgetCount() > 3) {
						break;
					} else if (cols.get(i).getWidgetCount() > 2) {
						if (swap1 == -1) {
							swap1 = i;
						} else {
							Label label1 = (Label) ((SimplePanel) cols.get(i).getWidget(1)).getWidget();
							Label label2 = (Label) ((SimplePanel) cols.get(swap1).getWidget(1)).getWidget();
							String temp = label1.getText();
							label1.setText(label2.getText());
							label2.setText(temp);
						}
					}
				}
			}
		});
		
		swapButton.setStyleName("btn btn-large btn-info");
		swapButton.getElement().getStyle().setProperty("margin-left", "35%");
		
		// same for right arrow here
		rightIcon.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int k = 0;
				for (int i = cols.size() - 1; i >= 0; i--) {
					if (cols.get(i).getWidgetCount() > 3) {
						break;
					} else if (cols.get(i).getWidgetCount() > 2 ) {
						Widget arrow = cols.get(i).getWidget(2);
						arrow.removeFromParent();
						cols.get(i - 1).add(arrow);
						if (cols.get(i - 1).getWidgetCount() > 3) {
							cols.get(i - 1).getWidget(2).getElement().getStyle().setProperty("margin", "70% 0 0 0");
							cols.get(i - 1).getWidget(3).getElement().getStyle().setProperty("margin", "70% 0 0 0");
						}
						break;
					}
				}
			}
		}, ClickEvent.getType());
		
		dragPanel.add(leftIcon);
		dragPanel.add(swapButton);
		dragPanel.add(rightIcon);
	}
	
	public static SimplePanel[] buildHeapSortPanel(AbsolutePanel dragPanel, Container heapBoxes, LogicalProblem logProb) {
		// make nodes able to be dropped anywhere, as well as on the actual evaluation boxes
		NodeDropController.setFields(dragPanel, null);
		NodeDropController.getInstance();
		Row row = new Row();
		
		SimplePanel[] panels = new SimplePanel[logProb.arguments.split("\\s").length];
		for (int i = 0; i < logProb.arguments.split("\\s").length; i++) {
			if (i % 12 == 0) {
				row = new Row();
				row.setStyleName("heapsort_row");
				row.setVisible(true);
				heapBoxes.add(row);
			}
			Column col = new Column(ColumnSize.MD_1, new Label("" + i));
			col.setStyleName("hashing_column");
			row.add(col);
			SimplePanel dropPanel = new SimplePanel();   // drop target for each cell
			dropPanel.setPixelSize(40, 40);
			dropPanel.getElement().getStyle()
				.setProperty("margin", "2.5px"); // account for 40x40 node in 50x50 col
			
			// This line adds the dropPanel to the cell, making the cell appear to be a drop zone
			col.add(dropPanel);
			// Add column to grid ArrayList, for later evaluation
			//grid.add(col);
			
			panels[i] = dropPanel;
			
			// set drop controller to dropPanel
			PanelDropController dropController = new PanelDropController(dropPanel);
			NodeDragController.getInstance().registerDropController(dropController);
		}
		dragPanel.add(heapBoxes);
		heapBoxes.setVisible(true);
		return panels;
	}
}
