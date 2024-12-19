package ui.frame;

import ui.Panels;
import ui.main.MainWindow;
import use.Date;
import use.Files;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ShoppingCart extends Panels implements ActionListener {

    JDialog frame;
    JTable shopTable;
    JPanel panelTable;
    JButton pay, update;

    public ShoppingCart(int x, int y) {

        // Create frame
        frame = initializeDialog(x, y, 450, 370, MainWindow.frame, "Количка", ShoppingCart.class);

        // Create panel
        JPanel panel = createPanel();

        // Panel - table
        panelTable = createPanelExt(0, 0, frame.getWidth(), frame.getHeight() - 80, Color.white, panel);

        // Panel - bottom
        JPanel panelBottom = createPanelExt(0, frame.getHeight() - 80, frame.getWidth(), 60, Color.lightGray, panel);

        // Pay button
        createButton(panelBottom.getWidth() - 145, 5, 120, pay = new JButton("Плати"), panelBottom, "pay-icon.png", 10);

        // Update table button
        createButton(panelBottom.getWidth() - 310, 5, 160, update = new JButton("Актуализиране"), panelBottom, "refresh-icon.png", 10);

        // Add to frame
        frame.add(panel);
        frame.setVisible(true);
        createProductTable();

    }

    // Create table
    private void createProductTable() {

        // Clear the current table state
        if (shopTable != null) {
            shopTable.setModel(new DefaultTableModel());
        }

        // Load Data
        Object[][] tableData = Files.shop.LoadItems();
        String[] tableTitles = { "Име", "Единична цена", "Брой", "Обща цена" };

        if (Files.shop.Exists()) {

            //region Table setup

            // Set up editable column
            DefaultTableModel tableModel = new DefaultTableModel(tableData, tableTitles) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 2;
                }
            };

            shopTable = new JTable(tableModel);
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(shopTable.getModel());
            shopTable.setRowSorter(sorter);
            JScrollPane scrollPane = new JScrollPane(shopTable);

            panelTable.setLayout(null);

            int margin = 15;
            scrollPane.setBounds(
                    margin,
                    margin,
                    panelTable.getWidth() - (margin * 2) - 15,
                    panelTable.getHeight() - (margin * 2) - 80
            );

            scrollPane.setForeground(Color.white);

            //endregion

            //region Calculate totals

            float price = tableColumnSum(shopTable, 1) * tableColumnSum(shopTable, 2);
            float taxes = 45;
            float taxAmount = price * (taxes / 100);
            float total = price + taxAmount;

            String input1 = String.format("Обща сума: %.2f лв.", price);
            String input2 = String.format("ДДС %.0f%%: %.2f лв.", taxes, taxAmount);
            String input3 = String.format("Сума след ДДС: %.2f лв.", total);

            //endregion

            //region Input fields

            JTextField inputField1 = new JTextField(input1);
            inputField1.setEnabled(false);
            inputField1.setDisabledTextColor(Color.black);
            inputField1.setHorizontalAlignment(SwingConstants.RIGHT);

            JTextField inputField2 = new JTextField(input2);
            inputField2.setEnabled(false);
            inputField2.setDisabledTextColor(Color.black);
            inputField2.setHorizontalAlignment(SwingConstants.RIGHT);

            JTextField inputField3 = new JTextField(input3);
            inputField3.setEnabled(false);
            inputField3.setDisabledTextColor(Color.black);
            inputField3.setHorizontalAlignment(SwingConstants.RIGHT);

            int inputHeight = 25;
            int inputWidth = 220;

            inputField1.setBounds(panelTable.getWidth() - (margin * 2) - inputWidth - 1, panelTable.getHeight() - 95, inputWidth, inputHeight);
            inputField2.setBounds(panelTable.getWidth() - (margin * 2) - inputWidth - 1, panelTable.getHeight() - 70, inputWidth, inputHeight);
            inputField3.setBounds(panelTable.getWidth() - (margin * 2) - inputWidth - 1, panelTable.getHeight() - 45, inputWidth, inputHeight);

            //endregion

            //region UI update

            panelTable.removeAll();
            panelTable.add(scrollPane);
            panelTable.add(inputField1);
            panelTable.add(inputField2);
            panelTable.add(inputField3);

            panelTable.revalidate();
            panelTable.repaint();

            //endregion

        }

    }

    // Save invoice
    private void saveInvoiceToFile() {

        Object[] options = {"Искам", "Не искам"};
        int confirmation = JOptionPane.showOptionDialog(frame,
                "Искате ли да направим фактура, за покупката?\nЩе използваме данните от профилът Ви, за да я попълним.",
                "Потвърждение на поръчката",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                options, options[0]
        );

        if (confirmation == 0) {

            try {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Запази фактура");
                fileChooser.setSelectedFile(new java.io.File("invoice_" + new Date().toString(true).replace(", ", "_").replace(":", "") + ".txt"));

                int userSelection = fileChooser.showSaveDialog(null);

                if (userSelection == JFileChooser.APPROVE_OPTION) {

                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                    if (!filePath.endsWith(".txt")) {
                        filePath += ".txt";
                    }

                    //region Write file

                    FileWriter writer = new FileWriter(filePath);

                    writer.write("Фактура\n");
                    writer.write("------------------------------\n");
                    writer.write(String.format("Фирма/ФЛ: %s%n", (MainWindow.currentUser.getCompanyName() != null) ? MainWindow.currentUser.getCompanyName() : MainWindow.currentUser.getNameFull()));
                    writer.write("Адрес: " + MainWindow.currentUser.getAddress().toString() + "\n");
                    writer.write("==============================\n");
                    writer.write(String.format("%-20s%-15s%-10s%-15s\n", "Продукт", "Цена", "Бройка", "Тотал цена"));
                    writer.write("------------------------------\n");

                    for (int i = 0; i < shopTable.getRowCount(); i++) {
                        String itemName = shopTable.getValueAt(i, 0).toString();
                        String unitPrice = shopTable.getValueAt(i, 1).toString();
                        String quantity = shopTable.getValueAt(i, 2).toString();
                        String totalPrice = shopTable.getValueAt(i, 3).toString();
                        writer.write(String.format("%-20s%-15s%-10s%-15s\n", itemName, unitPrice, quantity, totalPrice));
                    }

                    writer.write("\n");

                    float subtotal = tableColumnSum(shopTable, 1) * tableColumnSum(shopTable, 2);
                    float taxes = 45;
                    float taxAmount = subtotal * (taxes / 100);
                    float total = subtotal + taxAmount;

                    writer.write(String.format("Нето: %.2f\n", subtotal));
                    writer.write(String.format("ДДС (%.0f%%): %.2f\n", taxes, taxAmount));
                    writer.write(String.format("Тотал: %.2f\n", total));

                    writer.write("==============================\n");
                    writer.write("Благодарим Ви за поръчката!\n");
                    writer.write("КРАВА КОРПОРЕЙШЪН ООД\n");

                    writer.close();

                    //endregion

                    // Success Dialog
                    JOptionPane.showMessageDialog(frame, "Поздравления! Вие току-що похарчихте безпожна сума пари!\nФактура " + new File(filePath).getName() + " бе запазена успешно!");
                    Files.shop.Destroy();
                    frame.dispose();

                } else {
                    // Cancel Dialog
                    JOptionPane.showMessageDialog(null, "Запазването на фактурата не беше отменено.");
                }
            } catch (IOException e) {
                // Error Dialog
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Грешка при запазване на фактурата.");
            }

        } else if (confirmation == 1) {

            // Success Dialog
            JOptionPane.showMessageDialog(frame, "Поздравления! Вие току-що похарчихте безпожна сума пари!");
            Files.shop.Destroy();
            frame.dispose();

        }

    }

    // Get column sum
    private float tableColumnSum(JTable table, int columnIndex) {

        float sum = 0.0f;
        if (columnIndex >= 0 && columnIndex < table.getColumnCount()) {
            for (int row = 0; row < table.getRowCount(); row++) {
                Object value = table.getValueAt(row, columnIndex);

                if (value instanceof Number) {
                    sum += ((Number) value).doubleValue();
                } else if (value instanceof String) {
                    try {
                        sum += Double.parseDouble((String) value);
                    } catch (NumberFormatException e) {
                        // Skip not a num
                    }
                }
            }
        }
        return sum;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Pay button
        if (e.getSource() == pay) {
            saveInvoiceToFile();
        }

        // Update table
        if (e.getSource() == update) {
            Files.shop.UpdateItems(shopTable);
            MainWindow.shopItemCount = Files.shop.GetCount();
            createProductTable();
        }

    }

}
