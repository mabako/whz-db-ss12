package net.mabako.zwickau.autohaendler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import static net.mabako.zwickau.autohaendler.G.db;
import static net.mabako.zwickau.autohaendler.G.main;

/**
 * Ein Panel zum Anmelden an der Datenbank.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class Login extends JPanel
{
	/**
	 * automatisch generierte Serial
	 */
	private static final long serialVersionUID = 3915505281651823285L;

	/**
	 * Create the panel.
	 */
	public Login()
	{
		setLayout(new MigLayout("", "[20%:20%:20%][][grow][20%:20%:20%]", "[20%:20%:20%][][5%:5%:5%][][][][grow]"));
		
		JLabel lblError = new JLabel("");
		add(lblError, "cell 1 2 2 1,alignx center");
		
		if(Config.getDatabaseAuthenticationMethods().contains("windows"))
		{
			try
			{
				db.connectWindowsAuth(Config.getServer());
				
				successfulLogin();
				return;
			}
			catch(Exception e)
			{
				lblError.setText("Windows-Anmeldung: " + e.getMessage());
			}
		}
		
		if(Config.getDatabaseAuthenticationMethods().contains("sql"))
		{
			initializeClassicLogin(lblError);
		}
		else
		{
			String oldText = lblError.getText();
			// Zeilenumbrüche -> HTML erforderlich.
			if(oldText.length() > 0)
				oldText += "<br/><br/>";
			
			lblError.setText("<html><body align='center'>" + oldText + "Es sind keine Authentifizierungsmethoden verfügbar.</body></html>");
		}
	}

	/**
	 * Erstellt ein Login mit Benutzername und Passwort im aktuellen Frame.
	 * @param lblError Label, in welches Fehlermeldungen ausgegeben werden.
	 */
	private void initializeClassicLogin(final JLabel lblError)
	{
		JLabel lblPleaseLogin = new JLabel("Bitte melden Sie sich mit Ihrem Benutzername und Passwort an.");
		add(lblPleaseLogin, "cell 1 1 2 1,alignx center");
		
		JLabel lblUsername = new JLabel("Benutzername:");
		add(lblUsername, "cell 1 3,alignx right");
		
		final JTextField textUsername = new JTextField();
		add(textUsername, "cell 2 3,growx");
		textUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Passwort:");
		add(lblPassword, "cell 1 4,alignx right");
		
		final JPasswordField textPassword = new JPasswordField();
		add(textPassword, "cell 2 4,growx");
		
		JButton btnLogin = new JButton("Anmelden");
		btnLogin.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try {
					db.connectSQLAuth(Config.getServer(), textUsername.getText(), new String(textPassword.getPassword()));
					successfulLogin();
				} catch(Exception ex) {
					lblError.setText(ex.getMessage());
				}
			}
		});
		add(btnLogin, "flowx,cell 1 5 2 1,alignx center");

		JButton btnCancel = new JButton("Beenden");
		btnCancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				close();
			}
		});
		add(btnCancel, "flowx,cell 1 5 2 1,alignx center");
	}
	
	/**
	 * Wird nach einem erfolgreichen Login aufgerufen.
	 */
	protected void successfulLogin()
	{
		// TODO Auto-generated method stub
	}

	/**
	 * Schließt das Fenster, welches dieses Loginfenster beinhaltet.
	 */
	protected void close()
	{
		main.getWindow().dispose();
	}
}
