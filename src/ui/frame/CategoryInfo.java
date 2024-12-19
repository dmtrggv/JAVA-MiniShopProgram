package ui.frame;

import components.Category;
import ui.Panels;
import ui.main.MainWindow;
import use.Files;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CategoryInfo extends Panels implements ActionListener {

    private final boolean newCategory;
    private final Category currentCategory;
    private final boolean newMain;

    JDialog             frame;
    JComboBox<String>   cbNameMain;
    JTextField          txNameMain, txNameSub;
    JButton             btnSave, btnDelete;

    public CategoryInfo(int x, int y, Category category, String type) {

        // Current product setup
        currentCategory = category;

        // Editing main category
        newMain = (type.equals("main")) ? true : false;

        // Set editable
        newCategory = (category == null) ? true : false;

        // Create frame
        frame = initializeDialog(x, y, 280, 235, MainWindow.frame, (category != null) ? "Редактиране на категория" : "Създаване на категория", CategoryInfo.class);

        // Panel
        JPanel panel = createPanel();

        // Main category name
        if (category != null || type.equals("main")) {
            createTextField(panel, 15, 10, "Име на категорията:", txNameMain = new JTextField(), (category != null) ? category.getMain() : null, type.equals("main"), -1, -1, -1);
        } else {
            cbNameMain = new JComboBox<>();
            loadMainCategories();
            createComboBox(panel, 15, 10, "Главна категория:", cbNameMain, (category != null) ? category.getMain() : null, true);
        }

        // Sub category name
        createTextField(panel, 15, 60, "Име на под-категорията:", txNameSub = new JTextField(), (category != null) ? category.getSub() : null, type.equals("sub"), -1, -1, -1);

        // Save button
        createButton(15, 115, 237, btnSave = new JButton("Запази промените"), panel, "save-icon.png", 10);

        // Save button
        createButton(15, 150, 237, btnDelete = new JButton("Изтрий категорията"), panel, "delete-icon.png", 10);
        btnDelete.setEnabled(category != null);

        // Add panel to frame
        frame.add(panel);
        frame.setVisible(true);

    }

    // Load main categories
    private void loadMainCategories() {

        Object[][] categoryMain = Files.category.LoadListMain();
        cbNameMain.removeAllItems();

        for (Object[] main : categoryMain) {
            cbNameMain.addItem((String) main[0]);
        }

    }

    // Delete main category confirmation
    private void deleteMainCategoryConfirmation() {

        int confirmation = JOptionPane.showConfirmDialog(frame,
                "Сигурен ли си, че искаш да изтриеш " + currentCategory.getMain() + "?" + "\n" + "Това ще изтрие всички под-категории и всички продукти в тях!",
                "Изтриване на главна категория?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {

            Files.category.DeleteFromFile(currentCategory);
            frame.dispose();

        }

    }

    // Delete sub category confirmation
    private void deleteSubCategoryConfirmation() {

        int confirmation = JOptionPane.showConfirmDialog(frame,
                "Сигурен ли си, че искаш да изтриеш " + currentCategory.getSub() + "?" + "\n" + " Това ще изтрие и продуктите в категорията!",
                "Изтриване на под-категория?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {

            Files.category.DeleteFromFile(currentCategory);
            frame.dispose();

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnSave) {

            if (newCategory) {

                Category category = new Category(
                    (newMain) ? txNameMain.getText() : (String) cbNameMain.getSelectedItem(),
                    (!newMain) ? txNameSub.getText() : null
                );

                if (!Files.category.Exists(category)) {

                    Files.category.SaveToFile(category, null);
                    int xStart = frame.getX() + (frame.getWidth() / 2);
                    int yStart = frame.getY() + (frame.getHeight() / 2);
                    new CategoryInfo(xStart, yStart, category, (newMain) ? "main" : "sub");
                    frame.dispose();

                } else JOptionPane.showMessageDialog(frame, "Категорията, която се опитваш да въведеш, вече съществува!");

            } else {

                Category category = new Category(currentCategory.getMain(), (!newMain) ? currentCategory.getSub() : null);
                Category categoryCheck = new Category(txNameMain.getText(), txNameSub.getText());

                if (!Files.category.Exists(categoryCheck)) {

                    Files.category.SaveToFile(category, (newMain) ? txNameMain.getText() : txNameSub.getText());
                    int xStart = frame.getX() + (frame.getWidth() / 2);
                    int yStart = frame.getY() + (frame.getHeight() / 2);
                    new CategoryInfo(xStart, yStart, categoryCheck, (newMain) ? "main" : "sub");
                    frame.dispose();

                } else JOptionPane.showMessageDialog(frame, "Новото име на категорията не може да съвпада с име на друга категория!");

            }

        }

        if (e.getSource() == btnDelete) {

            if (newMain) deleteMainCategoryConfirmation();
            else deleteSubCategoryConfirmation();

        }

    }
}
