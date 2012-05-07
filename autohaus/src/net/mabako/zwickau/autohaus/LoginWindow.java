package net.mabako.zwickau.autohaus;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

/**
 * Ein in das Hauptfenster einbettbare Panel, welches das Anmelden erlaubt.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class LoginWindow extends JPanel
{
	/**
	 * automatisch generierte Serial
	 */
	private static final long serialVersionUID = 3915505281651823285L;
	
	/**
	 * Benutzername.
	 */
	private JTextField textUsername;
	
	/**
	 * Passwort.
	 */
	private JPasswordField textPassword;

	/**
	 * Create the panel.
	 */
	public LoginWindow()
	{
		setLayout(new MigLayout("", "[20%:20%:20%][][grow][20%:20%:20%]", "[20%:20%:20%][][5%:5%:5%][][][][grow]"));
		
		JLabel lblPleaseLogin = new JLabel("Bitte melden Sie sich mit Ihrem Benutzername und Passwort an.");
		add(lblPleaseLogin, "cell 1 1 2 1,alignx center");
		
		JLabel lblUsername = new JLabel("Benutzername:");
		add(lblUsername, "cell 1 3,alignx right");
		
		textUsername = new JTextField();
		add(textUsername, "cell 2 3,growx");
		textUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Passwort:");
		add(lblPassword, "cell 1 4,alignx right");
		
		textPassword = new JPasswordField();
		add(textPassword, "cell 2 4,growx");
		
		JButton btnLogin = new JButton("Anmelden");
		add(btnLogin, "flowx,cell 1 5 2 1,alignx center");

		JButton btnCancel = new JButton("Beenden");
		add(btnCancel, "flowx,cell 1 5 2 1,alignx center");
		
	}
}
