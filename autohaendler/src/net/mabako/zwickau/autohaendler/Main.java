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
					Main window = new Main(new LoginWindow());
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

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
	public Main(JPanel content)
	{
		initialize(content);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(JPanel content)
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
		header.setLayout(new MigLayout("fill", "[]", "[]"));
		
		JLabel lblAutohaus = new JLabel(Config.getAppName());
		lblAutohaus.setForeground(Config.getSeparatorColor());
		lblAutohaus.setFont(new Font("Tahoma", Font.PLAIN, 32));
		header.add(lblAutohaus, "cell 0 0,alignx center,aligny bottom");
		
		content.setBackground(Config.getBackgroundColor());
		frame.getContentPane().add(content, "cell 0 1,grow");
	}
}
