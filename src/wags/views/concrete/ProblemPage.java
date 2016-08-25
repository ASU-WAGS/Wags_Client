package wags.views.concrete;

import wags.Common.Presenter;
import wags.presenters.interfaces.ProblemPagePresenter;
import wags.views.interfaces.ProblemPageView;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Legend;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This is the "landing page" for magnets problems. It displays a list of 
 * buttons corresponding to the code magnet exercises that have been assigned 
 * to the user. If they have successfully completed the problem, the text on 
 * the button will be green. Otherwise, it will simply be black text displaying 
 * the name of the exercise.
 * 
 *
 */
public class ProblemPage extends Composite implements ProblemPageView {
	
	private static ProblemPageUiBinder uiBinder = GWT
			.create(ProblemPageUiBinder.class);

	interface ProblemPageUiBinder extends UiBinder<Widget, ProblemPage> {
	}
	
	@UiField ComplexPanel magnetPanel;
	@UiField ComplexPanel logicalPanel;
    @UiField ComplexPanel databasePanel; //Disabled until database problems are made
    
    @UiField ComplexPanel magnetDuePanel;
    @UiField ComplexPanel magnetCompletedPanel;
    @UiField ComplexPanel magnetReviewPanel;
    
    @UiField ComplexPanel logicalDuePanel;
    @UiField ComplexPanel logicalCompletedPanel;
    @UiField ComplexPanel logicalReviewPanel;
    
    @UiField ListBox subjectListBox;
    @UiField ListBox logicalListBox;
	
	@UiField Button magnetCategory;
	@UiField Button logicalCategory;
	
	@UiField Legend logicalLegend;
	@UiField Legend magnetLegend;
    //@UiField Button databaseCategory; Disabled until database problems are made
	
	private ProblemPagePresenter presenter;
	
	public ProblemPage()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = (ProblemPagePresenter) presenter;
	}

	@Override
	public boolean hasPresenter() {
		return presenter != null;
	}

	@Override
	public Presenter getPresenter() {
		return presenter;
	}

	@Override
	public ComplexPanel getMagnetPanel() {
		return magnetPanel;
	}

	@Override
	public ComplexPanel getLogicalPanel() {
		return logicalPanel;
	}
	
	@Override
	public ComplexPanel getMagnetDuePanel() {
		return magnetDuePanel;
	}
	
	@Override
	public ComplexPanel getMagnetCompletedPanel() {
		return magnetCompletedPanel;
	}
	
	@Override
	public ComplexPanel getMagnetReviewPanel() {
		return magnetReviewPanel;
	}
	
	
	@Override
	public ComplexPanel getLogicalDuePanel() {
		return logicalDuePanel;
	}
	
	@Override
	public ComplexPanel getLogicalCompletedPanel() {
		return logicalCompletedPanel;
	}
	
	@Override
	public ComplexPanel getLogicalReviewPanel() {
		return logicalReviewPanel;
	}
	
	@Override
	public ComplexPanel getDatabasePanel() {
		return databasePanel;
	}
	
	@UiHandler ("magnetCategory")
	public void onMagnetCategoryClick(ClickEvent event) {
		presenter.onMagnetCategoryClick();
	}
	
	@UiHandler ("logicalCategory") 
	public void onLogicalCategoryClick(ClickEvent event) {
		presenter.onLogicalCategoryClick();
	}
	
	@UiHandler ("subjectListBox") 
	public void onListboxCLick(ClickEvent event) {
		presenter.listboxClick();
	}
	
	@UiHandler ("logicalListBox") 
	public void onlogicalListboxCLick(ClickEvent event) {
		presenter.logicalListboxClick();
	}
	
	/** Disabled until database problems are made
	@UiHandler ("databaseCategory")
	public void onDatabaseCategoryClick(ClickEvent event) {
		presenter.onDatabaseCategoryClick();
	}
	*/
	
	@Override
	public ListBox getListBox() {
		return subjectListBox;
	}
	
	@Override
	public ListBox getlogicalListBox() {
		return logicalListBox;
	}
	
	@Override
	public Legend getLogicalLegend() {
		return logicalLegend;
	}
	
	@Override
	public Legend getMagnetLegend() {
		return magnetLegend;
	}

	@Override
	public Button getMagnetCategory() {
		return magnetCategory;
	}

	@Override
	public Button getLogicalCategory() {
		return logicalCategory;
	}

	/** Disabled until database problems are made
	@Override
	public Button getDatabaseCategory() {
		return databaseCategory;
	}
	*/

	@Override
	public boolean isAdmin() {
		return false;
	}
}
