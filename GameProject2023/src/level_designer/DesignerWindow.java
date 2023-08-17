package level_designer;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class DesignerWindow extends JFrame {

	private static final long serialVersionUID = 368425270423551144L;
	
	public DesignerWindow(int width, int height) {
		setSize(width, height);
		setTitle("Game Project 2023 - Level Designer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		int leftPanelWidth = (int) (width * 0.7f);
		int rightPanelWidth = width - leftPanelWidth;
		
		DrawingPanel drawingPanel = new DrawingPanel(leftPanelWidth, height);
		OptionsPanel optionsPanel = new OptionsPanel(rightPanelWidth, height);
		optionsPanel.setOptionSelectionListener(drawingPanel);
		
		add(drawingPanel, BorderLayout.CENTER);
		add(optionsPanel, BorderLayout.EAST);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
}
