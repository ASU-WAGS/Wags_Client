package wags.presenters.interfaces;

import wags.Common.Presenter;

public interface WagsPresenter extends Presenter
{
	void onHomeOutClick();
	void onHomeClick();
	void onProblemsClick();
	void onAdminClick();
	void onLogoutClick();
	void onLogicalManagementClick();
	void onLogicalCreationClick();
	void onMagnetManagementClick();
	void onMagnetCreationClick();
	void onReviewClick();
	void onProgrammingManagementClick();
	void onStudentManagementClick();
}
