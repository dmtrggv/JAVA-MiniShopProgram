package ui;

import use.*;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public abstract class Panels {

    // Constants
    protected final Font labelFont = new Font("Sans Serif", Font.BOLD, 12);
    protected final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    // Center positions
    protected final int xStart = screenSize.width / 2;  // frame.getX() + (frame.getWidth() / 2);
    protected final int yStart = screenSize.height / 2; // frame.getY() + (frame.getHeight() / 2);

    // Usability
    private static final Map<Class<?>, Boolean> panelExistenceMap = new HashMap<>();
    private boolean editable = false;

    // Is panel...
    public static boolean isPanelExists(Class<?> panelClass) {
        return panelExistenceMap.getOrDefault(panelClass, false);
    }
    public boolean isPanelEditable() {
        return editable;
    }

    // Set panel...
    public static void setPanelExist(Class<?> panelClass, boolean status) {
        panelExistenceMap.put(panelClass, status);
    }
    public void setPanelEditable(boolean status) {
        editable = status;
    }

    // Initialize Frame window - frame on top of mine frame
    public JFrame initializeFrame(int x, int y, int width, int height, String title, Class<?> panelClass) {

        Panels.setPanelExist(panelClass, true);
        int xstart, ystart;

        if (x - (width / 2) < 0) xstart = 0;
        else xstart = x - (width / 2);

        if (y - (height / 2) < 0) ystart = 0;
        else ystart = y - (height / 2);

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setBounds(xstart, ystart, width, height);
        frame.setResizable(false);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                setPanelExist(panelClass, false);
            }
        });

        return frame;

    }

    // Initialize Dialog window - frame on top of mine frame
    public static JDialog initializeDialog(int x, int y, int width, int height, JFrame mainFrame, String title, Class<?> panelClass) {

        // Set initial panel existence to true
        setPanelExist(panelClass, true);

        int xstart, ystart;
        if (x - (width / 2) < 0) xstart = 0;
        else xstart = x - (width / 2);

        if (y - (height / 2) < 0) ystart = 0;
        else ystart = y - (height / 2);

        JDialog frame = new JDialog(mainFrame, title);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setBounds(xstart, ystart, width, height);
        frame.setResizable(false);

        // Add WindowListener to change panel existence when closed
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                setPanelExist(panelClass, false);
            }
        });

        return frame;
    }

    // Create main panel
    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        return panel;
    }

    // Create panel
    public static JPanel createPanelExt(int x, int y, int width, int height, Color color, JPanel mainPanel) {
        JPanel panel = new JPanel();
        panel.setBounds(x, y, width, height);
        panel.setBackground(color);
        panel.setLayout(null);
        if (mainPanel != null) mainPanel.add(panel);
        return panel;
    }

    // Create JTextField - input box with title and formatting
    protected void createTextField(JPanel panel, int x, int y, String title, JTextField inputbox, String inputboxString, boolean editable, int filter, int maxLength, int format) {

        // Get index
        if (inputboxString != null && (!inputboxString.isEmpty())) inputbox.setText(inputboxString);

        // Label
        JLabel label = new JLabel(title);
        label.setFont(labelFont);
            label.setBounds(x, y, 200, 20);
        label.setForeground((editable) ? Color.black : Color.darkGray);

        // Textbox
        inputbox.setBounds(x, y + 20, 237, 25);
        inputbox.setEnabled(editable);
        inputbox.setDisabledTextColor(Color.darkGray);

        // Formatted textbox
        JTextField inputboxformat = new JTextField();
        if (format != -1 && inputboxString != null && (!inputboxString.isEmpty())) {

            inputboxformat.setBounds(x, y + 20, 237, 25);
            inputboxformat.setEnabled(editable);
            inputboxformat.setDisabledTextColor(Color.darkGray);

            if (format == Constants.format.DIGIT_GROUPING) {
                // format.DIGIT_GROUPING - to digit-grouping
                inputboxformat.setText(Format.toDigitGrouping(Integer.parseInt(inputboxString), true));
            } else if (format == Constants.format.PRICE) {
                // format.PRICE - to digits + " лв."
                inputboxformat.setText(inputboxString + " лв.");
            } else if (format == Constants.format.PERCENTAGE) {
                // format.PERCENTAGE - to digits + "%"
                inputboxformat.setText(inputboxString + "%");
            } else {
                // NULL
                inputboxformat.setText(inputboxString);
            }

        }

        // Textbox filter
        ((AbstractDocument) inputbox.getDocument()).setDocumentFilter(new NumericDocumentFilter(filter, maxLength));

        panel.add(label);
        if (editable || format == -1) panel.add(inputbox);
        else panel.add(inputboxformat);

    }

    // Create JTextArea - input box with title and formatting
    protected void createTextArea(JPanel panel, int x, int y, int height, String title, JTextArea inputbox, String inputboxString, boolean editable, int maxLength) {

        // Get index and set text if provided
        if (inputboxString != null && !inputboxString.isEmpty()) {
            inputbox.setText(inputboxString);
        }

        // Label
        JLabel label = new JLabel(title);
        label.setBounds(x, y, 200, 20);
        label.setForeground(editable ? Color.black : Color.darkGray);

        // Textbox
        inputbox.setEnabled(editable);
        inputbox.setDisabledTextColor(Color.darkGray);
        inputbox.setLineWrap(true);
        inputbox.setWrapStyleWord(true);

        inputbox.setRows(5);
        inputbox.setColumns(20);

        // Add JScrollPane
        JScrollPane scrollPane = new JScrollPane(inputbox);
        scrollPane.setBounds(x, y + 20, 237, height);  // Set the position and size of the scrollPane

        // Textbox filter
        ((AbstractDocument) inputbox.getDocument()).setDocumentFilter(new NumericDocumentFilter(Constants.filter.FILTER_NULL, maxLength));

        panel.add(label);
        panel.add(scrollPane);
    }

    // Create JDateField - date box with title and formatting
    protected void createDateField(JPanel panel, int x, int y, String title, JTextField dateDay, JTextField dateMonth, JTextField dateYear, Date dateLoad, boolean editable) {

        // Set textboxes
        if (dateLoad != null) {

            // Day
            dateDay.setText("" + dateLoad.getDay());

            // Month
            dateMonth.setText("" + dateLoad.getMonth());

            // Year
            dateYear.setText("" + dateLoad.getYear());

        }

        // Label - general
        JLabel label = new JLabel(title);
        label.setFont(labelFont);
        label.setBounds(x, y, 200, 20);

        // Label - DD "/" MM
        JLabel labelDot1 = new JLabel("/");
        labelDot1.setFont(labelFont);
        labelDot1.setBounds(x + 57, y + 20, 20, 25);

        // Label - MM "/" YYYY
        JLabel labelDot2 = new JLabel("/");
        labelDot2.setFont(labelFont);
        labelDot2.setBounds(x + 127, y + 20, 20, 25);

        // Day
        dateDay.setBounds(x, y + 20, 50, 25);
        dateDay.setEnabled(editable);
        dateDay.setFocusable(editable);
        dateDay.setDisabledTextColor(Color.darkGray);
        ((AbstractDocument) dateDay.getDocument()).setDocumentFilter(new NumericDocumentFilter(Constants.filter.FILTER_INTEGER, 2));

        // Month
        dateMonth.setBounds(x + 70, y + 20,50, 25);
        dateMonth.setEnabled(editable);
        dateMonth.setFocusable(editable);
        dateMonth.setDisabledTextColor(Color.darkGray);
        ((AbstractDocument) dateMonth.getDocument()).setDocumentFilter(new NumericDocumentFilter(Constants.filter.FILTER_INTEGER, 2));

        // Year
        dateYear.setBounds(x + 140, y + 20,97, 25);
        dateYear.setEnabled(editable);
        dateYear.setFocusable(editable);
        dateYear.setDisabledTextColor(Color.darkGray);
        ((AbstractDocument) dateYear.getDocument()).setDocumentFilter(new NumericDocumentFilter(Constants.filter.FILTER_INTEGER, -1));

        panel.add(label);
        panel.add(labelDot1);
        panel.add(labelDot2);
        panel.add(dateDay);
        panel.add(dateMonth);
        panel.add(dateYear);

    }

    // Create JComboBox - combo box with title and formatting
    protected void createComboBox(JPanel panel, int x, int y, String title, JComboBox<String> combobox, String selectedItem, boolean editable) {

        // Label
        JLabel label = new JLabel(title);
        label.setFont(labelFont);
        label.setBounds(x, y, 200, 20);

        // Combo box
        combobox.setBounds(x, y + 20, 237, 25);
        combobox.setEnabled(editable);

        // Set index
        if (selectedItem != null && isOptionInComboBox(combobox, selectedItem)) combobox.setSelectedItem(selectedItem);

        panel.add(label);
        panel.add(combobox);
    }

    // Is given item part of a combo box?
    protected static boolean isOptionInComboBox(JComboBox<String> comboBox, String searchString) {

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(searchString)) {
                return true;
            }
        }
        return false;

    }

    // Create button with icon
    protected void createButton(int x, int y, int width, JButton button, JPanel panelParent, String icon, int offset) {

        // Button
        button.setBounds(x, y, width, 30);
        button.addActionListener((ActionListener) this);
        button.setFocusable(true);

        // Icon
        if (icon != null) {
            String imagePath = Files.get.ImagesDirectory() + "\\" + icon;
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image resizedImage = originalIcon.getImage().getScaledInstance(button.getHeight() - offset, button.getHeight() - offset, Image.SCALE_SMOOTH); // width=50, height=50
            ImageIcon resizedIcon = new ImageIcon(resizedImage);
            button.setIcon(resizedIcon);
        }

        // Add to panel
        if (panelParent != null) panelParent.add(button);

    }

    // Create button without icon
    protected void createButton(int x, int y, int width, JButton button, JPanel panelParent) {
        createButton(x, y, width, button, panelParent, null, -1);
    }

    // Create and button list
    protected void createButtonList(JButton[] buttons, Dimension prefferedSize) {

        // Set and add buttons
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setPreferredSize(prefferedSize);
            buttons[i].setMaximumSize(prefferedSize);
            buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons[i].addActionListener((ActionListener) this);
        }

    }

    // Set button list icons
    protected void setButtonListIcons(JButton[] buttons, String[] icons, int iconSize) {

        // Set and add buttons
        for (int i = 0; i < buttons.length; i++) {
            if (icons[i] != null) {
                String imagePath = Files.get.ImagesDirectory() + "\\" + icons[i];
                ImageIcon originalIcon = new ImageIcon(imagePath);
                Image resizedImage = originalIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                ImageIcon resizedIcon = new ImageIcon(resizedImage);
                buttons[i].setIcon(resizedIcon);
            }
        }

    }

    // Add button list to panel
    protected void addButtonList(JButton[] buttons, JPanel panelParent) {

        // Set and add buttons
        for (int i = 0; i < buttons.length; i++) {
            panelParent.add(buttons[i]);
        }

    }

}