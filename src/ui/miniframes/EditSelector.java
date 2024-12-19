package ui.miniframes;

import components.Category;
import components.Product;
import ui.Panels;
import ui.frame.CategoryInfo;
import ui.main.MainWindow;
import ui.frame.ProductInfo;
import use.Files;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditSelector extends Panels implements ActionListener {

    JDialog             frame;
    JButton             btnOpen;
    JComboBox<String>   cbCategoryMain, cbCategorySub, cbItemList;
    JCheckBox           ccCategotyMain, ccCategorySub, ccItemList;

    public EditSelector(int x, int y) {

        // Create frame
        frame = initializeDialog(x, y, 307, 255, MainWindow.frame, "Избери продукт", EditSelector.class);

        // Panel
        JPanel panel = createPanel();

        // Main category
        cbCategoryMain = new JComboBox<>();
        ccCategotyMain = new JCheckBox();
        createList(15, 10, panel, "Избери главна категория:", cbCategoryMain, ccCategotyMain);
        loadMainCategories();
        cbCategoryMain.addActionListener(e -> {
            loadSubCategories();
            loadItemList();
        });
        ccCategotyMain.addItemListener(e -> {
            ccCategorySub.setSelected(false);
            ccItemList.setSelected(false);
        });

        // Sub category
        cbCategorySub = new JComboBox<>();
        ccCategorySub = new JCheckBox();
        createList(15, 60, panel, "Избери под-категория:", cbCategorySub, ccCategorySub);
        cbCategorySub.addActionListener(e -> loadItemList());
        ccCategorySub.addItemListener(e -> {
            ccCategotyMain.setSelected(false);
            ccItemList.setSelected(false);
        });


        // Items list
        cbItemList = new JComboBox<>();
        ccItemList = new JCheckBox();
        createList(15, 110, panel, "Избери продукт:", cbItemList, ccItemList);
        ccItemList.addItemListener(e -> {
            ccCategotyMain.setSelected(false);
            ccCategorySub.setSelected(false);
        });

        // Open button
        createButton(15, 170, 260, btnOpen = new JButton("Отвори"), panel, "open-icon.png", 15);

        // Add panel to frame
        frame.add(panel);
        frame.setVisible(true);
        loadSubCategories();
        loadItemList();

    }

    // Create list with checkbox
    private void createList(int x, int y, JPanel panelParent, String title, JComboBox<String> combobox, JCheckBox checkbox) {

        // Main category list
        createComboBox(panelParent, x, y, title, combobox, null, true);

        // Main category checkbox
        checkbox.setEnabled(false);
        checkbox.setBounds(x + 242, y + 20, 25, 25);
        checkbox.setBackground(panelParent.getBackground());
        checkbox.addActionListener(this);
        panelParent.add(checkbox);

    }

    // Load main categories
    private void loadMainCategories() {

        Object[][] categoryMain = Files.category.LoadListMain();
        cbCategoryMain.removeAllItems();

        for (Object[] main : categoryMain) {
            cbCategoryMain.addItem((String) main[0]);
            ccCategotyMain.setEnabled(true);
        }

    }

    // Load sub categories
    private void loadSubCategories() {

        String selectedMainCategory = (String) cbCategoryMain.getSelectedItem();

        if (selectedMainCategory != null) {
            Object[][] subCategories = Files.category.LoadListSub(selectedMainCategory);
            ccCategorySub.setEnabled(false);
            ccCategorySub.setSelected(false);
            cbCategorySub.removeAllItems();
            for (Object[] sub : subCategories) {
                cbCategorySub.addItem(sub[0].toString());
                ccCategorySub.setEnabled(true);
            }
        } else {
            ccCategorySub.setEnabled(false);
        }

    }

    // Load item list
    private void loadItemList() {

        String selectedMainCategory = (String) cbCategoryMain.getSelectedItem();
        String selectedSubCategory = (String) cbCategorySub.getSelectedItem();

        if (selectedMainCategory != null && selectedSubCategory != null) {
            Object[][] items = Files.category.LoadListItem(selectedMainCategory, selectedSubCategory);
            cbItemList.removeAllItems();
            ccItemList.setEnabled(false);
            ccItemList.setSelected(false);
            for (Object[] item : items) {
                cbItemList.addItem(item[0].toString());
                ccItemList.setEnabled(true);
            }
        } else {
            cbItemList.removeAllItems();
            ccItemList.setEnabled(false);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Edit main category
        if (e.getSource() == btnOpen && ccCategotyMain.isSelected()) {

            Category category = new Category((String) cbCategoryMain.getSelectedItem(), null);
            new CategoryInfo(xStart, yStart, category, "main");

        }

        // Edit sub category
        if (e.getSource() == btnOpen && ccCategorySub.isSelected()) {

            Category category = new Category((String) cbCategoryMain.getSelectedItem(), (String) cbCategorySub.getSelectedItem());
            new CategoryInfo(xStart, yStart, category, "sub");

        }

        // Edit product
        if (e.getSource() == btnOpen && ccItemList.isSelected()) {

            int xStart = (frame.getX() + frame.getWidth()) / 2;
            int yStart = ((frame.getY() + frame.getHeight()) / 2) + 50;

            Product product = Files.product.LoadFromFile(
                (String) cbCategoryMain.getSelectedItem(),
                (String) cbCategorySub.getSelectedItem(),
                (String) cbItemList.getSelectedItem()
            );

            new ProductInfo(xStart, yStart, product, false, false);

        }

    }

}
