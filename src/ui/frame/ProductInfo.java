package ui.frame;

import components.Product;
import ui.Panels;
import ui.main.MainWindow;
import use.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ProductInfo extends Panels implements ActionListener {

    private final boolean newProduct;
    private final Product currentProduct;

    JDialog             frame;
    JButton             btnSave, btnEdit, btnDelete, btnGetIcon;
    JTextField          iconURL, txName, txPrice, txDiscount, txAddedBy;
    JTextArea           txInfo;
    JComboBox<String>   cbCategoryMain, cbCategorySub;

    // Panel
    public ProductInfo(int x, int y, Product product, boolean editable, boolean startEmpty) {

        // Current product setup
        currentProduct = product;

        // Set editable
        newProduct = (product == null || startEmpty) ? true : false;
        if (newProduct) editable = true;

        //region Frame & main panel

        // Title
        String frameTitle;
        if (!newProduct) {
            if (editable) frameTitle = "Редактиране на продукт";
            else frameTitle = "Информация за продукт";
        } else frameTitle = "Добави продукт";

        // Create frame
        frame = initializeDialog(x, y, 530, 455, MainWindow.frame, frameTitle, ProductInfo.class);

        // Panel
        JPanel panel = createPanel();

        //endregion

        //region Icon , name , info

        // Icon
        iconURL = new JTextField();
        createIconPicker(
                panel, 15, 10, "Икона на продукт:",
                (!newProduct) ? Paths.get(product.getIconURL()).getFileName().toString() : "example-icon.png",
                btnGetIcon = new JButton("Избери"), editable
        );

        // Name
        createTextField(
                panel, 265, 10, "Име на продукт:",
                txName = new JTextField(), (!newProduct) ? product.getName() : null,
                editable && newProduct, Constants.filter.FILTER_NULL, 20, Constants.format.FORMAT_NULL
        );

        // Info
        createTextArea(
                panel, 265, 60, 75, "Информация за продукт:",
                txInfo = new JTextArea(), (!newProduct) ? product.getInfo() : null,
                editable, -1
        );

        //endregion

        //region Price , discount

        // Price
        createTextField(
                panel, 265, 160, "Цена - в лв.:",
                txPrice = new JTextField(), (!newProduct) ? "" + product.getPrice() : null,
                editable, Constants.filter.FILTER_DOUBLE, 20, Constants.format.PRICE
        );

        // Discount
        createTextField(
                panel, 265, 210, "Намаление - в %:",
                txDiscount = new JTextField(), (!newProduct) ? "" + product.getDiscount() : null,
                editable, Constants.filter.FILTER_INTEGER, 20, Constants.format.PERCENTAGE
        );

        //endregion

        //region Categories

        // Main category
        cbCategoryMain = new JComboBox<>();
        Object[][] categoryMain = Files.category.LoadListMain();
        for (Object[] main : categoryMain) {
            cbCategoryMain.addItem((String) main[0]);
        }
        createComboBox(
                panel, 15, 260, "Категория:",
                cbCategoryMain, (!newProduct) ? product.getCategoryMain() : null, editable && newProduct
        );
        cbCategoryMain.addActionListener(e -> loadSubCategories());

        // Sub category
        createComboBox(
                panel, 265, 260, "Под-категория:",
                cbCategorySub = new JComboBox<>(), (!newProduct) ? product.getCategorySub() : null, editable && newProduct
        );

        //endregion

        //region Dates , user

        // Date of creation
        createTextField(
                panel, 15, 310, "Дата на създаване на продуктът:",
                new JTextField(), (!newProduct) ? product.getDateAdded().toString(true) : null,
                false, Constants.filter.FILTER_NULL, -1, Constants.format.FORMAT_NULL
        );

        // Date of last redaction
        createTextField(
                panel, 265, 310, "Дата на последна редакция:",
                new JTextField(), (!newProduct) ? product.getDateLastChange().toString(true) : null,
                false, Constants.filter.FILTER_NULL, -1, Constants.format.FORMAT_NULL
        );

        // Who added the product
        createTextField(
                panel, 15, 360, "Довавен от потребител:",
                txAddedBy = new JTextField(), (!newProduct) ? product.getUserFrom() : MainWindow.currentUser.getNameFull(), false, Constants.filter.FILTER_NULL, -1, Constants.format.FORMAT_NULL
        );

        //endregion

        //region Buttons

        // Save button
        if (editable) createButton(355, 375, 147, btnSave = new JButton("Запази промените"), panel, "save-icon.png", 10);

        // Edit button
        if (!editable) createButton(355, 375, 147, btnEdit = new JButton("Редактирай"), panel, "edit-icon.png", 10);

        // Delete button
        createButton(266, 375, 80, btnDelete = new JButton("Изтрий"), panel, "delete-icon.png", 10);
        btnDelete.setEnabled(!newProduct);

        //endregion

        // Add frame
        loadSubCategories();
        frame.add(panel);
        frame.setVisible(true);

    }

    // Create Registration number box
    private void createIconPicker(JPanel panel, int x, int y, String title, String iconIndex, JButton button, boolean editable) {

        //region Input box

        JTextField inputbox = new JTextField();
        if (iconIndex != null && !iconIndex.isEmpty()) {
            inputbox.setText(Files.get.ImagesDirectory() + "\\" + iconIndex);
        }

        JLabel label = new JLabel(title);
        label.setBounds(x, y, 250, 20);
        label.setForeground(editable ? Color.BLACK : Color.DARK_GRAY);

        //endregion

        //region Loaded image

        final BufferedImage[] loadedImage = {null};

        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (loadedImage[0] != null) {
                    int imageSize = Math.min(getWidth(), getHeight()) - 8;
                    g.drawImage(loadedImage[0], 4 + ((getWidth() - imageSize) / 2), 4, imageSize, imageSize, null);
                    iconURL.setText(inputbox.getText());
                }
            }
        };

        //endregion

        //region Setup layout

        panel.setLayout(null);
        iconPanel.setBounds(x, y + 20, 235, 195);
        iconPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        inputbox.setBounds(x, y + 220, 130, 25);
        inputbox.setEnabled(false);
        inputbox.setDisabledTextColor(Color.darkGray);

        if (!inputbox.getText().isEmpty()) {

            try {
                File initialFile = new File(inputbox.getText());
                if (initialFile.exists()) {
                    loadedImage[0] = ImageIO.read(initialFile);
                    iconPanel.repaint();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Не можа да се зареди иконата.", "Грешка", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

        }

        //endregion

        //region Button logic

        button.setBounds(x + 135, y + 220, 100, 24);
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select an Image (PNG Only)");
            chooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));

            int returnVal = chooser.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                try {
                    loadedImage[0] = ImageIO.read(selectedFile);
                    inputbox.setText(selectedFile.getAbsolutePath());
                    iconPanel.repaint();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Не можа да се зареди иконата: " + ex.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        button.setEnabled(editable);

        //endregion

        // Add to panel
        panel.add(label);
        panel.add(iconPanel);
        panel.add(inputbox);
        panel.add(button);

    }

    // Load sub categories
    private void loadSubCategories() {

        String selectedMainCategory = (String) cbCategoryMain.getSelectedItem();

        if (selectedMainCategory != null) {
            Object[][] subCategories = Files.category.LoadListSub(selectedMainCategory);

            cbCategorySub.removeAllItems();
            for (Object[] sub : subCategories) {
                cbCategorySub.addItem(sub[0].toString());
            }
            if (currentProduct != null) cbCategorySub.setSelectedItem(currentProduct.getCategorySub());
        }

    }

    // Create delete product confirmation box
    private void deleteProductConfirmation() {

        int confirmation = JOptionPane.showConfirmDialog(frame,
                "Сигурен ли си, че искаш да изтриеш " + currentProduct.getName() + "?",
                "Изтриване на продукт?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {

            Files.product.DeleteFromFile(currentProduct.getCategoryMain(), currentProduct.getCategorySub(), currentProduct.getName());
            frame.dispose();

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Save product
        if (e.getSource() == btnSave) {

            // Create product object
            Product product = new Product(
                    iconURL.getText(),
                    txName.getText(),
                    Float.parseFloat(txPrice.getText()),
                    Integer.parseInt(txDiscount.getText()),
                    (currentProduct != null) ? currentProduct.getDateAdded() : new Date(),
                    new Date(),
                    (String) cbCategoryMain.getSelectedItem(),
                    (String) cbCategorySub.getSelectedItem(),
                    txAddedBy.getText(),
                    txInfo.getText()
            );

            // Check if product exists
            boolean checkExistance;
            if (newProduct) {
                checkExistance = !Files.product.Exists(product);
            } else checkExistance = true;

            // Check textboxes
            boolean checkTextboxes = (
                (!txName.getText().isEmpty()) &&
                (!txPrice.getText().isEmpty()) &&
                (!txDiscount.getText().isEmpty())
            );

            // Check for negative numbers
            boolean checkNegativeNum = (
                (!txPrice.getText().isEmpty()) && (Float.parseFloat(txPrice.getText()) > 0) &&
                (!txDiscount.getText().isEmpty()) && (Integer.parseInt(txDiscount.getText()) >= 0)
            );

            if (checkExistance && checkTextboxes && checkNegativeNum) {

                int xStart = frame.getX() + (frame.getWidth() / 2);
                int yStart = frame.getY() + (frame.getHeight() / 2);
                Files.product.SaveToFile(product);
                new ProductInfo(xStart, yStart, product, false, false);
                frame.dispose();

            } else {

                String txtStart =       "Не може да се запази, поради една от следните причини:" + "\n";
                String txtExistance =   (checkExistance) ? " - Продукт, с това име и категория, съществува ваче!" + "\n" : "";
                String txtTextboxes =   (checkTextboxes) ? " - Продуктът задължително трябва да има въведени [Име], [Цена] и [Отстъпка]!" + "\n" : "";
                String txtNegativeNum = (checkNegativeNum) ? " - Отстъпката не трябва да е негативно число, а цената трябва да е повече от 0!" + "\n" : "";

                JOptionPane.showMessageDialog(frame, txtStart + txtExistance + txtTextboxes + txtNegativeNum);

            }

        }

        // Edit product
        if (e.getSource() == btnEdit) {

            Product product = new Product(
                    iconURL.getText(),
                    txName.getText(),
                    Float.parseFloat(txPrice.getText()),
                    Integer.parseInt(txDiscount.getText()),
                    currentProduct.getDateAdded(),
                    currentProduct.getDateLastChange(),
                    (String) cbCategoryMain.getSelectedItem(),
                    (String) cbCategorySub.getSelectedItem(),
                    txAddedBy.getText(),
                    txInfo.getText()
            );

            int xStart = frame.getX() + (frame.getWidth() / 2);
            int yStart = frame.getY() + (frame.getHeight() / 2);
            new ProductInfo(xStart, yStart, product, true, false);
            frame.dispose();

        }

        // Delete product
        if (e.getSource() == btnDelete) {
            deleteProductConfirmation();
        }

    }

}