package net.mabako.zwickau.autohaendler;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

import java.awt.Font;
import java.util.Stack;

import static net.mabako.zwickau.autohaendler.G.main;

/**
 * Grafisches Hauptfenster.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class Main
{
	/**
	 * Fenster
	 */
	private JFrame frame;
	
	/**
	 * Liste aller Unterfenster.
	 * 
	 * Das Element 0 ist entweder das Login-Fenster ODER das Hauptfenster.
	 * Sämtliche weitere Fenster werden auf den Stack gepusht, mittels goUp() gelangt man zurück
	 * und mit goToHome() zur Hauptseite.
	 * 
	 * @see Main#setHomeWindow(JPanel)
	 * @see Main#addContent(JPanel)
	 * @see Main#goToHome()
	 * @see Main#goUp()
	 */
	private Stack<JPanel> content = new Stack<JPanel>( );
	
	/**
	 * Label, in dem steht, wer angemeldet ist.
	 */
	private JLabel lblLoggedInAs;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		updateLookAndFeel();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					main = new Main();
					main.setHomeWindow(new Login());
					main.frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Versucht, das auf dem System übliche Look- and Feel zu verwenden.
	 */
	private static void updateLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			
		}
	}

	/**
	 * Create the application.
	 */
	public Main()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setTitle(Config.getAppName());
		frame.getContentPane().setBackground(Config.getSeparatorColor());
		frame.setBounds(100, 100, 450, 300);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("fill,insets 0,gap 3", "[grow]", "[100px:100px:100px][grow]"));
		
		JPanel header = new JPanel();
		header.setBackground(Color.WHITE);
		frame.getContentPane().add(header, "cell 0 0,grow");
		header.setLayout(new MigLayout("fill", "[]", "[][]"));
		
		lblLoggedInAs = new JLabel("");
		lblLoggedInAs.setForeground(Config.getSeparatorColor());
		setLoggedInAs(null);
		header.add(lblLoggedInAs, "cell 0 0,alignx right,aligny top");
		
		JLabel lblAutohaus = new JLabel(Config.getAppName());
		lblAutohaus.setForeground(Config.getSeparatorColor());
		lblAutohaus.setFont(new Font(lblAutohaus.getFont().getFontName(), lblAutohaus.getFont().getStyle(), 32));
		header.add(lblAutohaus, "cell 0 1,alignx center,aligny bottom");
	}
	
	/**
	 * Liefert das Hauptfenster zurück.
	 * @return
	 */
	public JFrame getWindow()
	{
		return frame;
	}
	
	/**
	 * Setzt das Hauptfenster innerhalb der Navigation.
	 * @param newContent
	 */
	public void setHomeWindow(JPanel newContent)
	{
		if(content.size() > 0)
		{
			frame.getContentPane().remove(content.lastElement());
			content.clear();
		}
		
		addContent(newContent);
	}
	
	/**
	 * Fügt einen neuen Unterbildschirm hinzu und zeigt diesen an.
	 * @param newContent
	 */
	public void addContent(JPanel newContent)
	{
		// Alten Inhalt gegebenenfalls entfernen.
		if(content.size() > 0)
			frame.getContentPane().remove(content.lastElement());
		content.push(newContent);
		
		// Im Fenster hinzufügen.
		newContent.setBackground(Config.getBackgroundColor());
		displayContent();
	}
	
	/**
	 * Zeigt den aktuellen Unterbildschirm an.
	 */
	private void displayContent()
	{
		frame.getContentPane().add(content.lastElement(), "cell 0 1,grow");
	}
	
	/**
	 * Springt zur Hauptseite des Programms.
	 */
	public void goToHome()
	{
		if(content.size() > 1)
		{
			frame.getContentPane().remove(content.pop());
			while(content.size() > 1)
				content.pop();
			
			displayContent();
		}
	}
	
	/**
	 * Navigiert eine Ebene nach oben.
	 */
	public void goUp()
	{
		if(content.size() > 1)
		{
			frame.getContentPane().remove(content.pop());
			displayContent();
		}
	}
	
	/**
	 * Setzt den Namen, der als angemeldet angezeigt wird.
	 * @param username Benutzername oder <code>null</code>, falls niemand angemeldet ist.
	 */
	public void setLoggedInAs(String username)
	{
		lblLoggedInAs.setText(username == null ? "Nicht angemeldet" : ("<html>Angemeldet als <strong>" + username + "</strong>.</html>"));
	}
}
