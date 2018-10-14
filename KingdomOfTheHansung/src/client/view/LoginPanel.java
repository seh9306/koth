package client.view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.main.KingdomOfTheHansung;
import hash.key.LoginKey;
import hash.key.SignalKey;
import value.ProtocolValue;

@SuppressWarnings("serial")
public class LoginPanel extends JPanel {	
	private Vector<SignalKey> keyVector;
	
	private JTextField idTextField = new JTextField();
	private JTextField pwTextField = new JTextField();
	private Image background, select, logo, logo2, id, pw;
	
	public LoginPanel(KingdomOfTheHansung koth) {
		keyVector 	= koth.getSignalKeyVectorForSendThread();
		select 		= new ImageIcon("images//select.png").getImage();
		background 	= new ImageIcon("images//background.jpg").getImage();
		logo 		= new ImageIcon("images//logo.png").getImage();
		logo2 		= new ImageIcon("images//logo2.png").getImage();
		id			= new ImageIcon("images//id.png").getImage();
		pw			= new ImageIcon("images//pw.png").getImage();
		
		setLayout(null);

		pwTextField.setBounds(500, 400, 100, 20);
		idTextField.setBounds(500, 350, 100, 20);
		

		JButton loginBtn = new JButton();
		JButton cancelBtn = new JButton();

		loginBtn.setBounds(440, 535, 65, 35);
		loginBtn.setBorderPainted(false);
		loginBtn.setContentAreaFilled(false);
		loginBtn.setFocusPainted(false);

		cancelBtn.setBounds(540, 535, 65, 35);
		cancelBtn.setBorderPainted(false);
		cancelBtn.setContentAreaFilled(false);
		cancelBtn.setFocusPainted(false);

		loginBtn.setIcon(new ImageIcon("images//confirm1.png"));
		loginBtn.setPressedIcon(new ImageIcon("images//pressedConfirm.png"));

		cancelBtn.setIcon(new ImageIcon("images//cancel.png"));
		cancelBtn.setPressedIcon(new ImageIcon("images//pressedCancel.png"));

		loginBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String id = idTextField.getText();
				String pw = pwTextField.getText();
				idTextField.setText("");
				pwTextField.setText("");

				if (id.equals("") || pw.equals("") || (id.contains(" ") || pw.contains(" "))) {
					return;
				}
				
				keyVector.add(new LoginKey(ProtocolValue.LOGIN,id,pw));
			}
		});

		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		add(loginBtn);
		add(cancelBtn);
		add(idTextField);
		add(pwTextField);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		g.drawImage(background, 0, 0, 1040, 800, this);
		g.drawImage(select, 180, 140, 668, 490, this);

		g.drawImage(logo, 50, 130, 100, 25, this);
		g.drawImage(logo2, 60, 30, 90, 90, this);

		g.drawImage(id, 400, 350, 47, 13, this);
		g.drawImage(pw, 390, 400, 67, 20, this);
	}
	
}
