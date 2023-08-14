package level_designer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import framework.ObjectId.Name;
import framework.TextureLoader;

public class OptionsPanel extends JPanel {

	private static final long serialVersionUID = -1729590889537721572L;

	interface OptionSelectionListener {
		void onGameObjectSelected(BufferedImage selectedImage, Name objectName);
		void onBackgroundColorSelected(Color color);
		void onGridToggle(boolean toggle);
		void deleteDesign();
		void saveDesign();
	}
	
	private OptionSelectionListener optionSelectionListener;
	
	public OptionsPanel(int width, int height) {
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.GRAY);
		setLayout(new BorderLayout());

		setupGUI();

		setVisible(true);
	}
	
	private void setupGUI() {
		// Top panel contains the panel title, background selection
		// and enable/disable grid check box
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		
		JLabel titleLabel = new JLabel("Options", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Calibri", Font.BOLD, 20));
		topPanel.add(titleLabel, BorderLayout.NORTH);
		
		JCheckBox toggleGridCheckBox = new JCheckBox("Toggle Grid");
		toggleGridCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		toggleGridCheckBox.setSelected(true);
		toggleGridCheckBox.addActionListener(e -> {
			if (optionSelectionListener != null)
				optionSelectionListener.onGridToggle(toggleGridCheckBox.isSelected());
		});
		topPanel.add(toggleGridCheckBox, BorderLayout.CENTER);
		
		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new FlowLayout());
		colorPanel.add(new JLabel("Choose Background Color: "));
		JButton colorPickerButton = new JButton();
		colorPickerButton.setPreferredSize(new Dimension(25, 25));
		colorPickerButton.setBackground(new Color(51, 51, 51));
		colorPickerButton.addActionListener(e -> {
			Color color = JColorChooser.showDialog(this, "Select a color", Color.WHITE);   
			if (color != null) {	
				colorPickerButton.setBackground(color);
				if (optionSelectionListener != null)
					optionSelectionListener.onBackgroundColorSelected(color);
			}
		});
		colorPanel.add(colorPickerButton);
		topPanel.add(colorPanel, BorderLayout.SOUTH);
		
		// Bottom panel contains the Delete and Save buttons
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		
		JButton deleteButton = new JButton("Delete Design");
		deleteButton.addActionListener(e -> {
			if (optionSelectionListener != null)
				optionSelectionListener.deleteDesign();
		});
		
		JButton saveButton = new JButton("Save Design");
		saveButton.addActionListener(e -> {
			if (optionSelectionListener != null)
				optionSelectionListener.saveDesign();
		});
		bottomPanel.add(deleteButton);
		bottomPanel.add(saveButton);
		
		// Center panel contains the buttons that allow the selection of GameObjects
		// The selected game object can be drawn into the grid in the DrawingPanel
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Name[] enumValues = Name.values();
		JButton[] buttons = new JButton[enumValues.length];

		int buttonSize = (getWidth() - 30) / 3;
		GridBagConstraints gbc = new GridBagConstraints();

		for (int i = 0; i < enumValues.length; i++) {
			Name objectName = enumValues[i];
			BufferedImage objectImage = getImageByObjectName(objectName);
			
			buttons[i] = new JButton(); 
			buttons[i] = new JButton(new ImageIcon(objectImage));
			buttons[i].setPreferredSize(new Dimension(buttonSize, buttonSize));
			buttons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (optionSelectionListener != null)
						optionSelectionListener.onGameObjectSelected(objectImage, objectName);
				}
			});

			gbc.gridx = i % 3;
			gbc.gridy = i / 3;
			centerPanel.add(buttons[i], gbc);
		}

		JScrollPane scrollPane = new JScrollPane(centerPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private BufferedImage getImageByObjectName(Name objectName) {
		BufferedImage image = null;
		TextureLoader textureLoader = TextureLoader.getInstance();

		switch (objectName) {
		default:
			image = textureLoader.missingSprite;
			break;
		}
		
		return image;
	}
	
	public void setOptionSelectionListener(OptionSelectionListener listener) {
		this.optionSelectionListener = listener;
	}
	
}