package wags.views.interfaces;

import java.util.List;

import wags.Common.View;

import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.ValueBoxBase;

public interface DefaultPageView extends View{
	public UIObject getProblemsButton();
	ValueBoxBase<String> getUsernameField();
	ValueBoxBase<String> getPasswordField();
	public UIObject getLoginButton();
	void setData(List<String> data);
	public UIObject getLoginScreen();
	public boolean isAdmin();
}
