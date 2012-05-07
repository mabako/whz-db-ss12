package net.mabako.zwickau.autohaus;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import java.awt.SystemColor;
import javax.swing.JLabel;
import java.awt.Font;

/**
 * Grafisches Hauptfenster.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class Main
{

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Main window = new Main();
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
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
		frame.setTitle("Datenbanken II - Autohaus");
		frame.getContentPane().setBackground(SystemColor.inactiveCaption);
		frame.setBounds(100, 100, 450, 300);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("fill,insets 0,gap 3", "[grow]", "[100px:100px:100px][grow]"));
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		frame.getContentPane().add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("fill", "[]", "[]"));
		
		JLabel lblAutohaus = new JLabel("Autohaus");
		lblAutohaus.setForeground(SystemColor.activeCaption);
		lblAutohaus.setFont(new Font("Tahoma", Font.PLAIN, 32));
		panel.add(lblAutohaus, "cell 0 0,alignx center,aligny bottom");
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.inactiveCaptionBorder);
		frame.getContentPane().add(panel_1, "cell 0 1,grow");
	}
}
