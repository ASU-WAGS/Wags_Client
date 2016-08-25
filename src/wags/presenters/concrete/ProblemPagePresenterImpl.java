package wags.presenters.concrete;
import java.util.List;

import wags.ProxyFacilitator;
import wags.Common.Model;
import wags.Common.Tokens;
import wags.ProxyFramework.AbstractServerCall;
import wags.ProxyFramework.GetMMGroupsCommand;
import wags.ProxyFramework.LoadAssignedProblemsCommand;
import wags.magnet.view.ProblemPageModel;
import wags.presenters.interfaces.ProblemPagePresenter;
import wags.views.elements.ProblemButton;
import wags.views.interfaces.ProblemPageView;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Legend;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.UIObject;

import sun.awt.AWTAccessor.WindowAccessor;

/**
 * @author Dakota Murray
 * 
 * Presenter implementation for the problems page. The problems page is reached through
 * the "Problems" navbar option and displays all problems assigned to a student. 
 *
 */

public class ProblemPagePresenterImpl implements ProblemPagePresenter {
	
	public static final String EMPTY_LABEL = "No Magnet Exercises Assigned!";

	private ProblemPageView view;
	protected ProblemPageModel model;
	private List<String> serverData;
	private boolean wasLoaded = false;
	private int globalPageState;

	/**
	 * Basic constructor for the presenter. The presenter initializes a new model, 
	 * and then sends a server request to get the data necessary to populate the
	 * list. The model then updates the presenter with the list of problems.
	 * 
	 * @param view	The ProblemPageView bound to this presenter.
	 */
	public ProblemPagePresenterImpl(ProblemPageView view)
	{
		this.view = view;
		model = new ProblemPageModel();
		model.registerObserver(this, false);
		
		AbstractServerCall cmd = new LoadAssignedProblemsCommand(model);
		cmd.sendRequest();
	}
	
	@Override
	public void go(HasWidgets container) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public boolean bound() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Populates the list of logical, database, and magnet problems available for the
	 * student. A title, id, problem type, and status are required to create a problem
	 * button. The steps that must be taken are as follows:
	 * 	
	 * 1. Check to see if the data is already loaded (list.size() > 1), if not, populate the list
	 *  2. Parse data from the data list
	 *  3. Create a new "ProblemButton" object, add it to the appropriate panel depending on type
	 *  4. Set the visual indicators depending on status: assigned or completed
	 *  5. update the page state, ie: what type of problems we are looking at (magnet, logical, database)
	 *  
	 */
	@Override
	public void update(List<String> data) {
		
		//Window.alert("update");
		serverData = data;
		int pageState = 0;
		if (History.getToken().contains("logical"))
		{
			pageState = 1;
		}
		//When we put database problems back in
		/*
		else if(History.getToken().contains("database"))
		{
			pageState = 2;
		}*/
		
		
		// load the panels we need
		ComplexPanel magnets = view.getMagnetPanel();
		ComplexPanel logical = view.getLogicalPanel();
		
		ComplexPanel magnetsDuePane = view.getMagnetDuePanel();
		ComplexPanel magnetsCompletedPane = view.getMagnetCompletedPanel();
		ComplexPanel magnetsReviewPane = view.getMagnetReviewPanel();
		
		ComplexPanel logicalDuePane = view.getLogicalDuePanel();
		ComplexPanel logicalCompletedPane = view.getLogicalCompletedPanel();
		ComplexPanel logicalReviewPane = view.getLogicalReviewPanel();
		
		ListBox subjectList = view.getListBox();
		ListBox logicalList = view.getlogicalListBox();
		
		if (wasLoaded == false) {
			subjectList.addItem("All Magnet Problems");
			logicalList.addItem("All Logical Problems");
			wasLoaded = true;
		}
		
		//Only execute if problems have not already been loaded
		if( data.size() > 1) {
			boolean magnetDue = false;
			boolean logicalDue = false;
			boolean duplicate = false;
			
			String[] ids = data.get(1).split("&");
			String[] titles = data.get(2).split("&");
			String[] statuses = data.get(3).split("&");
			String[] types = data.get(4).split("&");
			String[] groups = data.get(5).split("&");
			String[] uniqueGroups = new String[groups.length];
			
			//Strips duplicate groups out of the array 
			//And then adds them to the listbox
			uniqueGroups[0] = groups[0];
			int uniqueIndex = 0;
			
			for (int i = 0; i < groups.length; i++) {
				for (int j = i+1; j < groups.length; j++) {
					if (groups[i].equals(groups[j])) {
						duplicate = true;
					}
				}
				if (!duplicate) {
					uniqueGroups[uniqueIndex] = groups[i];
					uniqueIndex++;
				}
				duplicate = false;
			}
			//This definetly should be done statically like this
			//If a new logical problem category was added it would end up in the wrong box
			//Works for now though.
			for(int it = 0; it < uniqueGroups.length; it++) {
				if (uniqueGroups[it] != null) {
					switch(uniqueGroups[it]) {
					case "Binary Trees":
						logicalList.addItem(uniqueGroups[it]);
						break;
					case "Heaps":
						logicalList.addItem(uniqueGroups[it]);
						break;
					case "Hashing":
						logicalList.addItem(uniqueGroups[it]);
						break;
					case "Graphs":
						logicalList.addItem(uniqueGroups[it]);
						break;
					case "RadixSort":
						logicalList.addItem(uniqueGroups[it]);
						break;
					case "Quicksort":
						logicalList.addItem(uniqueGroups[it]);
						break;
					case "Created":
						logicalList.addItem(uniqueGroups[it]);
						break;
					default:
						subjectList.addItem(uniqueGroups[it]);
					}
					
				}
			}
			//take this out of the loop for efficiency
			int magnetType = ProblemType.TypeToVal(ProblemType.MAGNET_PROBLEM);
			for(int i = 0; i < ids.length; i++) {
				int id = new Integer(ids[i]);
				String title = titles[i];
				int status = new Integer(statuses[i]);
				int type = new Integer(types[i]);
				String group = groups[i];
				//subjectList.addItem(group);
				if (type == magnetType) {
					if ( status == 0) {
						magnetDue = true;
						magnetsDuePane.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
						magnetsDuePane.setVisible(true);
					}
					else if(status == 1) {
						magnetsCompletedPane.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
						magnetsCompletedPane.setVisible(true);
					}
					
					else {
					magnetsReviewPane.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
					magnetsReviewPane.setVisible(true);
					}
				} else { 	
					//Is a logical problem
					if (status == 0)  {
						logicalDue = true;
						logicalDuePane.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
						logicalDuePane.setVisible(true);
					}
					else if(status == 1) {
						logicalCompletedPane.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
						logicalCompletedPane.setVisible(true);
					}
					else {
						logicalReviewPane.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
						logicalReviewPane.setVisible(true);
					}
					
				}
				// Expand these statements for database problems at a later date
			}
//			if (magnetDue) {
//				view.getMagnetCategory().setIcon(IconType.EXCLAMATION);
//				view.getMagnetCategory().addStyleName("problem_due");
//				
//			} else {
//				view.getMagnetCategory().addStyleName("problem_complete");
//			}
//			if (logicalDue) {
//				view.getLogicalCategory().setIcon(IconType.EXCLAMATION);
//				view.getLogicalCategory().addStyleName("problem_due");
//			} else {
//				view.getLogicalCategory().addStyleName("problem_category");
//			}
		}
		
		// Update page state
		globalPageState = pageState;
		setPageState(pageState);
	}

	@Override
	public void onMagnetCategoryClick() {
		if (globalPageState != 0) {
			History.newItem(Tokens.CODE);
			//model.setPageState(MAGNET_STATE, true);
			setPageState(0);
			globalPageState = 0;
		}
	}

	@Override
	public void onLogicalCategoryClick() {
		if (globalPageState != 1) {
			History.newItem(Tokens.LOGICAL);
			//model.setPageState(LOGICAL_STATE, true);
			setPageState(1);
			globalPageState = 1;
		}
	}

	@Override
	public void onDatabaseCategoryClick() {
		if (globalPageState != 2) {
			History.newItem(Tokens.DBMAIN);
			//model.setPageState(DATABASE_STATE, true);
			setPageState(2);
			globalPageState = 2;
		}
	}

	/**
	 * Changes the problem state ie: which category of problems is currently
	 * being viewed (magnet, logical, or database).
	 * 
	 * TODO: Currently database problems are not implemented, so if selected will 
	 * 		 default to magnet problems
	 */
	@Override
	public void setPageState(int pageState) {
		Button logicalButton = view.getLogicalCategory();
		Button magnetButton = view.getMagnetCategory();
		if (pageState == 1)
		{
			pageState = 1;
			logicalButton.setEnabled(false);
			magnetButton.setEnabled(true);
		}
		else {
			magnetButton.setEnabled(false);
			logicalButton.setEnabled(true);
		}
		//Window.alert("setPageState");
		ComplexPanel magnets = view.getMagnetPanel();
		ComplexPanel logical = view.getLogicalPanel();
		ListBox subjectList = view.getListBox();
		ListBox logicalList = view.getlogicalListBox();
		
		UIObject magnetCategory = view.getMagnetCategory();
		UIObject logicalCategory = view.getLogicalCategory();
		
		Legend logicalLegend = view.getLogicalLegend();
		Legend magnetLegend = view.getMagnetLegend();
		
		//begin by setting everything to invisible to avoid visual mishaps and make it easy to 
		//update the tab (see end of this function
		magnets.setVisible(false);
		logical.setVisible(false);
		subjectList.setVisible(false);
		logicalList.setVisible(false);
		logicalLegend.setVisible(false);
		magnetLegend.setVisible(false);
		
		magnetCategory.removeStyleName("category_selected");
		logicalCategory.removeStyleName("category_selected");
		
		switch (pageState) {
		case MAGNET_STATE:
			magnets.setVisible(true);
			subjectList.setVisible(true);
			magnetCategory.addStyleName("category_selected");
			magnetLegend.setVisible(true);
			//magnetCategory.getElement().getStyle().setProperty("border", "2px solid black !important");
			break;
		case LOGICAL_STATE:
			logical.setVisible(true);
			logicalList.setVisible(true);
			logicalCategory.addStyleName("category_selected");
			logicalLegend.setVisible(true);
			break;
		case DATABASE_STATE: //fall through
		default:
			magnets.setVisible(true);
			subjectList.setVisible(true);
			magnetCategory.addStyleName("category_selected");
			magnetLegend.setVisible(true);
		}
		
	}

	@Override
	public void listboxClick() {
		ListBox subjectList = view.getListBox();
		String title = subjectList.getValue(subjectList.getSelectedIndex());
		filterByGroupName("magnet", title);
	}



	@Override
	public void logicalListboxClick() {
		ListBox logicalList = view.getlogicalListBox();
		String title = logicalList.getValue(logicalList.getSelectedIndex());
		filterByGroupName("logical", title);
	}
	
	public void filterByGroupName(String panelType, String panelGroup) {
		
		Button logicalButton = view.getLogicalCategory();
		Button magnetButton = view.getMagnetCategory();
		int pageState = 0;
		if (History.getToken().contains("logical"))
		{
			pageState = 1;
			logicalButton.setEnabled(false);
			magnetButton.setEnabled(true);
		}
		else {
			magnetButton.setEnabled(false);
			logicalButton.setEnabled(true);
		}
		ComplexPanel panel;
		ComplexPanel duePanel;
		ComplexPanel completedPanel;
		ComplexPanel reviewPanel;
		
		if (panelType.equalsIgnoreCase("magnet")) {
			panel = view.getMagnetPanel();
			duePanel = view.getMagnetDuePanel();
			completedPanel = view.getMagnetCompletedPanel();
			reviewPanel = view.getMagnetReviewPanel();
		}
		else {
			panel = view.getLogicalPanel();
			duePanel = view.getLogicalDuePanel();
			completedPanel = view.getLogicalCompletedPanel();
			reviewPanel = view.getLogicalReviewPanel();
		}
		panel.clear();
		panel.add(duePanel);
		panel.add(completedPanel);
		panel.add(reviewPanel);
		
		Heading h1 = new Heading(5,"Uncompleted Problems");
		Heading h2 = new Heading(5,"Completed Problems");
		Heading h3 = new Heading(5,"Review Problems");
		
		duePanel.clear();
		completedPanel.clear();
		reviewPanel.clear();
		
		duePanel.add(h1);
		completedPanel.add(h2);
		reviewPanel.add(h3);
		
		duePanel.setVisible(false);
		completedPanel.setVisible(false);
		reviewPanel.setVisible(false);
		
		List<String> localData = serverData;
		if( localData.size() > 1) {
			boolean magnetDue = false;
			boolean logicalDue = false;
			boolean duplicate = false;
			
			String[] ids = localData.get(1).split("&");
			String[] titles = localData.get(2).split("&");
			String[] statuses = localData.get(3).split("&");
			String[] types = localData.get(4).split("&");
			String[] groups = localData.get(5).split("&");
			
			int magnetType = ProblemType.TypeToVal(ProblemType.MAGNET_PROBLEM);
			for(int i = 0; i < ids.length; i++) {
				int id = new Integer(ids[i]);
				String title = titles[i];
				int status = new Integer(statuses[i]);
				int type = new Integer(types[i]);
				String group = groups[i];
				if (panelGroup.equalsIgnoreCase("All Magnet Problems") || panelGroup.equalsIgnoreCase("All Logical Problems")) {
					if (type == magnetType && panelType.equalsIgnoreCase("magnet")) {
						if ( status == 0) {
							magnetDue = true;
							duePanel.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
							duePanel.setVisible(true);
						}
						else if (status == 1) {
							completedPanel.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
							completedPanel.setVisible(true);
						}
						else {
							reviewPanel.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
							reviewPanel.setVisible(true);
						}
						//panel.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
					} else if (type == 1 && panelType.equalsIgnoreCase("logical")){
						if ( status == 0) {
							logicalDue = true;
							duePanel.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
							duePanel.setVisible(true);
						}
						else if (status == 1) {
							completedPanel.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
							completedPanel.setVisible(true);
						}
						else {
							reviewPanel.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
							reviewPanel.setVisible(true);
						}
						//panel.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
					}
				}
				else {
					if (group.equalsIgnoreCase(panelGroup)) {
						if (type == magnetType) {
							if ( status == 0) {
								magnetDue = true;
								duePanel.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
								duePanel.setVisible(true);
							}
							else if (status == 1) {
								completedPanel.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
								completedPanel.setVisible(true);
							}
							else {
								reviewPanel.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
								reviewPanel.setVisible(true);
							}
							//panel.add(new ProblemButton(id, title, status, ProblemType.MAGNET_PROBLEM));
						} else {
							if ( status == 0) {
								logicalDue = true;
								duePanel.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
								duePanel.setVisible(true);
							}
							else if (status == 1) {
								completedPanel.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
								completedPanel.setVisible(true);
							}
							else {
								reviewPanel.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
								reviewPanel.setVisible(true);
							}
							//panel.add(new ProblemButton(id, title, status, ProblemType.LOGICAL_PROBLEM));
						}
					}
				}
			}
//			if (magnetDue) {
//				view.getMagnetCategory().setIcon(IconType.EXCLAMATION);
//				view.getMagnetCategory().addStyleName("problem_due");
//			} else {
//				view.getMagnetCategory().addStyleName("problem_complete");
//			}
//			if (logicalDue) {
//				view.getLogicalCategory().setIcon(IconType.EXCLAMATION);
//				view.getLogicalCategory().addStyleName("problem_due");
//			} else {
//				view.getLogicalCategory().addStyleName("problem_category");
//			}
		}
	}
}
