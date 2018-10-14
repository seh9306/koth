package client.view;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import client.main.KingdomOfTheHansung;
import client.view.controller.ViewManager;
import value.FrameValue;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	
	public MainFrame(KingdomOfTheHansung koth) {
		super("한성의나라");
		setSize(FrameValue.WINDOW_WIDTH, FrameValue.WINDOW_HEIGHT);
		setIconImage(new ImageIcon("images//logo2.png").getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		ViewManager viewManager = koth.getViewManager();
		viewManager.setMainFrame(this);
		viewManager.setLoginPanel(new LoginPanel(koth));
		viewManager.setContentPane(viewManager.getLoginPanel());
		viewManager.validate();
		setVisible(true);
	}

}