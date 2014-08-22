package eu.bibl.launcher.gui.login;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import eu.bibl.core.loader.profile.MCProfile;
import eu.bibl.launcher.Launcher;
import eu.bibl.launcher.gui.bottombar.BottomBarPanel;

public class AddProfileDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 2147340172717973550L;
	private static final Dimension DIALOG_DIMENSION = new Dimension(255, 140);
	
	private Launcher launcher;
	private BottomBarPanel bottomBarPanel;
	private JPanel panel;
	private JTextField username;
	private JPasswordField password;
	private JButton addButton;
	
	public AddProfileDialog(Launcher launcher, BottomBarPanel bottomBarPanel) {
		this.launcher = launcher;
		this.bottomBarPanel = bottomBarPanel;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Add Profile");
		setLayout(null);
		setPreferredSize(DIALOG_DIMENSION);
		setSize(DIALOG_DIMENSION);
		setResizable(false);
		
		panel = new JPanel(null);
		panel.setPreferredSize(DIALOG_DIMENSION);
		panel.setSize(DIALOG_DIMENSION);
		panel.setLocation(0, -3);
		
		username = new JTextField();
		username.addActionListener(this);
		username.setPreferredSize(new Dimension(150, 20));
		username.setSize(username.getPreferredSize());
		username.setLocation(75, 17);
		password = new JPasswordField();
		password.addActionListener(this);
		password.setPreferredSize(new Dimension(150, 20));
		password.setSize(password.getPreferredSize());
		password.setLocation(75, 42);
		
		panel.add(username);
		panel.add(password);
		JLabel usernameLabel = new JLabel("Username:");
		JLabel passwordLabel = new JLabel("Password:");
		usernameLabel.setSize(100, 25);
		usernameLabel.setLocation(15, 15);
		passwordLabel.setSize(100, 25);
		passwordLabel.setLocation(15, 40);
		
		panel.add(usernameLabel);
		panel.add(passwordLabel);
		
		addButton = new JButton("Add");
		addButton.setFocusable(false);
		addButton.setPreferredSize(new Dimension(153, 25));
		addButton.setSize(addButton.getPreferredSize());
		addButton.setLocation(73, 70);
		addButton.addActionListener(this);
		
		panel.add(addButton);
		
		add(panel);
		pack();
		
		setLocationRelativeTo(launcher.getWindow());
		setAlwaysOnTop(true);
		setVisible(true);
	}
	
	public Launcher getLauncher() {
		return launcher;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(addButton)) {
			pressed();
		} else if (e.getSource().equals(username) || e.getSource().equals(password)) {
			pressed();
		}
	}
	
	private void pressed() {
		String username = this.username.getText();
		String password = new String(this.password.getPassword());
		if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
			MCProfile profile = new MCProfile(username, password);
			YggdrasilUserAuthentication auth = profile.getUserAuth();
			if (auth.isLoggedIn()) {
				dispose();
				bottomBarPanel.addProfile(profile);
			} else {
				setAlwaysOnTop(false);
				JOptionPane.showMessageDialog(null, "Invalid credentials", "Auth Error", JOptionPane.ERROR_MESSAGE);
				setAlwaysOnTop(true);
			}
		} else {
			setAlwaysOnTop(false);
			JOptionPane.showMessageDialog(null, "Invalid credentials", "Auth Error", JOptionPane.ERROR_MESSAGE);
			setAlwaysOnTop(true);
		}
	}
}