package ui.miniframes;

import ui.Panels;
import ui.frame.CategoryInfo;
import ui.main.MainWindow;
import ui.frame.ProductInfo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateSelector extends Panels implements ActionListener {

    JDialog frame;
    JButton createCategoryMain, createCategorySub, createProduct;

    public CreateSelector(int x, int y) {

        // Create frame
        frame = initializeDialog(x, y, 280, 160, MainWindow.frame, "Какво ще създадеш", CreateSelector.class);

        // Panel
        JPanel panel = createPanel();

        // Create main category
        createButton(15, 10, 237, createCategoryMain = new JButton("Създай категория"), panel, "category-icon.png", 10);

        // Create sub category
        createButton(15, 45, 237, createCategorySub = new JButton("Създай под-категория"), panel, "category-icon.png" , 10);

        // Create main category
        createButton(15, 80, 237, createProduct = new JButton("Създай продукт"), panel, "product-icon.png", 10);

        // Add panel to frame
        frame.add(panel);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Create main category
        if (e.getSource() == createCategoryMain) {
            new CategoryInfo(xStart, yStart, null, "main");
        }

        // Create sub category
        if (e.getSource() == createCategorySub) {
            new CategoryInfo(xStart, yStart, null, "sub");
        }

        // Create product
        if (e.getSource() == createProduct) {
            new ProductInfo(xStart, yStart, null, true, true);
        }

    }
}
