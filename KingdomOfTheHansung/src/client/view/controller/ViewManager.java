package client.view.controller;

import client.view.MainPanel;

import javax.swing.JPanel;

import client.view.LoginPanel;
import client.view.MainFrame;

public class ViewManager {
	private MainFrame mainFrame;
	private MainPanel mainPanel;
	private LoginPanel loginPanel;
	
	public MainFrame getMainFrame() {
		return mainFrame;
	}
	
	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	public MainPanel getMainPanel() {
		return mainPanel;
	}
	
	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}
	
	public LoginPanel getLoginPanel() {
		return loginPanel;
	}
	
	public void setLoginPanel(LoginPanel loginPanel) {
		this.loginPanel = loginPanel;
	}

	public void setContentPane(JPanel contentPane) {
		mainFrame.setContentPane(contentPane);
	}
	
	public void validate(){
		mainFrame.validate();
	}
	
}
