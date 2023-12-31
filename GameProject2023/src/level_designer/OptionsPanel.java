package level_designer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import abstracts.GameObject;
import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class OptionsPanel extends JPanel {

	private static final long serialVersionUID = -1729590889537721572L;

	interface OptionSelectionListener {
		void onGameObjectSelected(BufferedImage selectedImage, Name objectName);
		void onBackgroundColorSelected(Color color);
		void onGridToggle(boolean toggle);
		void onLayerSelect(int index);
		void onTransparencySelect(float transparency);
		void clearGrids();
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
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel("Options", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Calibri", Font.BOLD, 25));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		titlePanel.add(titleLabel, BorderLayout.NORTH);
		
		JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		titlePanel.add(titleSeparator, BorderLayout.SOUTH);
		topPanel.add(titlePanel, BorderLayout.NORTH);
		
		JPanel selectionGroupPanel = new JPanel();
		selectionGroupPanel.setLayout(new BorderLayout());
		
		JCheckBox toggleGridCheckBox = new JCheckBox("Toggle Grid");
		toggleGridCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		toggleGridCheckBox.setSelected(true);
		toggleGridCheckBox.addActionListener(e -> {
			if (optionSelectionListener != null)
				optionSelectionListener.onGridToggle(toggleGridCheckBox.isSelected());
		});
		selectionGroupPanel.add(toggleGridCheckBox, BorderLayout.NORTH);
		
		JPanel layerSelectionPanel = new JPanel();
		layerSelectionPanel.setLayout(new FlowLayout());
		JLabel layerSelectionLabel = new JLabel("Selected Layer: ");
		JComboBox<String> layerComboBox = new JComboBox<>(new String[] {
				"Background Layer",
				"Middle Layer",
				"Foreground Layer",
				"Enemy Wave 1", 
				"Enemy Wave 2",
				"Enemy Wave 3",
				"All Layers (Read only)"});
        layerComboBox.setSelectedIndex(1);
		layerComboBox.addActionListener(e -> {
        	if (optionSelectionListener != null)
				optionSelectionListener.onLayerSelect(layerComboBox.getSelectedIndex());
        });
		layerSelectionPanel.add(layerSelectionLabel);
		layerSelectionPanel.add(layerComboBox);
		selectionGroupPanel.add(layerSelectionPanel, BorderLayout.CENTER);
		
		JPanel transparencyPanel = new JPanel();
		transparencyPanel.setLayout(new FlowLayout());
        JSlider transparencySLider = new JSlider(JSlider.HORIZONTAL, 0, 100, 40);
        transparencySLider.setMajorTickSpacing(10);
        transparencySLider.setMinorTickSpacing(5);
        transparencySLider.setPaintTicks(true);
        transparencySLider.setPaintLabels(true);
		String transparencyBaseText = "Non-Selected Layer Transparency: ";
		JLabel transparencyLabel = new JLabel(transparencyBaseText + " " + String.format("%.2f", transparencySLider.getValue() / 100f));
        transparencySLider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	if (optionSelectionListener != null) {
            		float transparencyValue = transparencySLider.getValue() / 100f;
    				optionSelectionListener.onTransparencySelect(transparencyValue);
    				
    				transparencyLabel.setText(transparencyBaseText + " " + String.format("%.2f", transparencyValue));
            	}
            }
        });
		transparencyPanel.add(transparencyLabel);
		transparencyPanel.add(transparencySLider);
		selectionGroupPanel.add(transparencyPanel, BorderLayout.SOUTH);
		topPanel.add(selectionGroupPanel, BorderLayout.CENTER);
		
		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new FlowLayout());
		colorPanel.add(new JLabel("Background Color: "));
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
		FlowLayout bottomPanelLayout = new FlowLayout();
		bottomPanelLayout.setHgap(30);
		bottomPanel.setLayout(bottomPanelLayout);
		
		JButton clearButton = new JButton("Clear Layers");
		clearButton.addActionListener(e -> {
			if (optionSelectionListener != null)
				optionSelectionListener.clearGrids();
		});
		
		JButton saveButton = new JButton("Save Design");
		saveButton.addActionListener(e -> {
			if (optionSelectionListener != null)
				optionSelectionListener.saveDesign();
		});
		bottomPanel.add(clearButton);
		bottomPanel.add(saveButton);
		
		// Center panel contains the buttons that allow the selection of GameObjects
		// The selected game object can be drawn into the grid in the DrawingPanel
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Name[] enumValues = Name.values();

		int columnCount = 5;
		int buttonSize = (getWidth() - 15) / columnCount;
		GridBagConstraints gbc = new GridBagConstraints();

		for (int i = 0, gridBagIndex = 0; i < enumValues.length; i++) {
		    Name objectName = enumValues[i];
		    // Get objects image by Name
		    BufferedImage objectImage = getImageByObjectName(objectName);

		    ImageIcon scaledIcon = new ImageIcon(objectImage.getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));
		    JButton button = new JButton(scaledIcon);
		    button.setPreferredSize(new Dimension(buttonSize, buttonSize));
		    button.setMargin(new Insets(0, 0, 0, 0));
		    
		    button.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            if (optionSelectionListener != null)
		                optionSelectionListener.onGameObjectSelected(objectImage, objectName);
		        }
		    });
		    
		    gbc.gridx = gridBagIndex % columnCount;
		    gbc.gridy = gridBagIndex / columnCount;
		    gridBagIndex++;
		    gbc.anchor = GridBagConstraints.CENTER;

		    centerPanel.add(button, gbc);
		}

		JScrollPane scrollPane = new JScrollPane(centerPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);

		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private BufferedImage getImageByObjectName(Name objectName) {
		GameObject gameObject = new ObjectHandler(null, null).createObjectByName(objectName, -1, -1);
		return gameObject != null ? gameObject.getTexture() : 
			TextureLoader.getInstance().getTextures(TextureName.Missing)[0];
	}
	
	public void setOptionSelectionListener(OptionSelectionListener listener) {
		this.optionSelectionListener = listener;
	}
	
}
