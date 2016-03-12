package cz.slanyj.pdfriend;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Graphics2D;

import cz.slanyj.swing.Canvas;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JToolBar;
import javax.swing.JButton;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.CompoundBorder;

public class GUI {
	
	public static final JFrame window = new JFrame();
	private static Canvas canvas;
	private static JMenuBar menuBar;
	private static JMenu mnMenu;
	private static JMenuItem mntmExit;
	private static JPanel left;
	private static JLabel lblNewLabel;
	private static JLabel lblNewLabel_1;
	private static JPanel panel;
	private static JPanel panel_1;
	private static JPanel right;
	private static JPanel footer;
	private static JLabel statusBar;
	private static Component rigidArea;
	
	/** The active document. */
	private static Document doc;
	private static Component rigidArea_2;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void build() {
				
		// Set look and feel
		
//		UIManager.put("nimbusBase", Color.WHITE);
//		UIManager.put("nimbusBlueGrey", Color.LIGHT_GRAY);
		UIManager.put("control", new Color(0xee, 0xee, 0xee));
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		left = new JPanel();
		left.setBorder(null);
		window.getContentPane().add(left, BorderLayout.WEST);
		GridBagLayout gbl_left = new GridBagLayout();
		gbl_left.columnWidths = new int[]{82, 0};
		gbl_left.rowHeights = new int[]{0, 16, 0, 0};
		gbl_left.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_left.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		left.setLayout(gbl_left);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		left.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{82, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		lblNewLabel_1 = new JLabel("New label");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		left.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{55, 0};
		gbl_panel_1.rowHeights = new int[]{16, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		lblNewLabel = new JLabel("New label");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_1.add(lblNewLabel, gbc_lblNewLabel);
		
		rigidArea_2 = Box.createRigidArea(new Dimension(20, 20));
		rigidArea_2.setPreferredSize(new Dimension(20, 0));
		rigidArea_2.setMinimumSize(new Dimension(20, 0));
		rigidArea_2.setMaximumSize(new Dimension(20, 0));
		GridBagConstraints gbc_rigidArea_2 = new GridBagConstraints();
		gbc_rigidArea_2.gridx = 0;
		gbc_rigidArea_2.gridy = 0;
		left.add(rigidArea_2, gbc_rigidArea_2);
		
		right = new JPanel();
		window.getContentPane().add(right, BorderLayout.EAST);
		GridBagLayout gbl_right = new GridBagLayout();
		gbl_right.columnWidths = new int[]{0};
		gbl_right.rowHeights = new int[]{0};
		gbl_right.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_right.rowWeights = new double[]{Double.MIN_VALUE};
		right.setLayout(gbl_right);
		
		canvas = new Canvas();
		canvas.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		canvas.setPreferredSize(new Dimension(600, 600));
		canvas.setMinimumSize(new Dimension(200, 200));
		window.getContentPane().add(canvas, BorderLayout.CENTER);
		canvas.setDoubleClickBehaviour(()->canvas.fitRectangle(0, 0, 500, 750));
		
		footer = new JPanel();
		footer.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		window.getContentPane().add(footer, BorderLayout.SOUTH);
		footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
		
		rigidArea = Box.createRigidArea(new Dimension(20, 20));
		rigidArea.setMaximumSize(new Dimension(4, 20));
		rigidArea.setMinimumSize(new Dimension(4, 20));
		rigidArea.setPreferredSize(new Dimension(4, 20));
		footer.add(rigidArea);
		
		statusBar = new JLabel("New label");
		footer.add(statusBar);
		
		window.pack();
		window.setLocationRelativeTo(null);
		window.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		
		menuBar = new JMenuBar();
		window.setJMenuBar(menuBar);
		
		mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		
		mntmExit = new JMenuItem("New menu item");
		mnMenu.add(mntmExit);
		window.setVisible(true);
		window.revalidate();
	}
	
	public static void openDoc(Document doc) {
		GUI.doc = doc;
		canvas.addCanvasPainter(doc);
	}
	
	public static void open() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				build();
			}
		});
	}
	
}
