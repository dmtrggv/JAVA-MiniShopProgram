package ui.main;

import components.Product;
import components.User;
import ui.Panels;
import ui.frame.ShoppingCart;
import ui.frame.UserInfo;
import ui.miniframes.CreateSelector;
import ui.miniframes.EditSelector;
import use.Constants;
import use.Files;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;

public class MainWindow extends Panels implements ActionListener {

    public static int shopItemCount = Files.shop.GetCount();
    public static User currentUser;
    public static JFrame frame;

    JLabel hoverLabel;
    JTree categoryTree;
    JPanel panelLeft, panelWelcome, panelProduct;
    JButton createButton, editButton, profileButton, refreshTree, backToMain, myShoppingCart;

    public MainWindow(int x, int y, User user) {

        // Generate user
        currentUser = user;

        int xstart = (x != -1) ? x : (screenSize.width / 2);
        int ystart = (y != -1) ? y : (screenSize.height / 2);

        // Create frame
        frame = initializeFrame(xstart, ystart, 1100, 680, Constants.app.APP_NAME + " - " + Constants.app.DEVELOPER + "@" + Constants.app.DEV_STUDIO, MainWindow.class);
        frame.setLayout(new BorderLayout());

        // Create categories
        createCategories();

        // Main panel
        createWelcomePanel("Добре дошъл, " + currentUser.getNameFirst() + "!");

        // Add to frame
        frame.setVisible(true);

    }

    public MainWindow(User user) {
        this(-1, -1, user);
    }

    // Create product card
    private static JPanel createProductCards(Product[] products) {

        // Parent panel
        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new BorderLayout(0, 10));

        //region Top panel (Buttons for sorting)

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton sortByPriceButton = new JButton("Сортирай по цена");
        JButton sortByNameButton = new JButton("Сортирай по име");

        sortByPriceButton.setPreferredSize(new Dimension(190, 30));
        sortByNameButton.setPreferredSize(new Dimension(190, 30));

        //region Icons

        // Sort by price
        String imagePathByPrice = Files.get.ImagesDirectory() + "\\" + "sort-by-price-icon.png";
        ImageIcon originalIconByPrice = new ImageIcon(imagePathByPrice);
        Image resizedImageByPrice = originalIconByPrice.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon resizedIconByPrice = new ImageIcon(resizedImageByPrice);
        sortByPriceButton.setIcon(resizedIconByPrice);

        // Sort by name
        String imagePathByName = Files.get.ImagesDirectory() + "\\" + "sort-by-alpha-icon.png";
        ImageIcon originalIconByName = new ImageIcon(imagePathByName);
        Image resizedImageByName = originalIconByName.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon resizedIconByName = new ImageIcon(resizedImageByName);
        sortByNameButton.setIcon(resizedIconByName);

        //endregion

        topPanel.add(sortByPriceButton);
        topPanel.add(sortByNameButton);

        //endregion

        //region Main panel (Product cards container)

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        Runnable updateProductCards = () -> {
            mainPanel.removeAll();
            for (Product productnow : products) {
                JPanel productPanel = createProductPanel(productnow);
                mainPanel.add(productPanel);
            }
            mainPanel.revalidate();
            mainPanel.repaint();
        };

        // Sort products by price as default
        Arrays.sort(products, Comparator.comparingDouble(Product::getPrice));
        updateProductCards.run();

        //endregion

        //region Buttons

        // Sort by price
        sortByPriceButton.addActionListener(e -> {
            Arrays.sort(products, Comparator.comparingDouble(Product::getPrice));
            updateProductCards.run();
        });

        // Sort by name
        sortByNameButton.addActionListener(e -> {
            Arrays.sort(products, Comparator.comparing(Product::getName));
            updateProductCards.run();
        });

        //endregion

        // Assemble parent panel
        parentPanel.add(topPanel, BorderLayout.NORTH);
        parentPanel.add(mainPanel, BorderLayout.CENTER);

        return parentPanel;

    }

    // Create products panel
    private static JPanel createProductPanel(Product productnow) {

        //region Product panel

        JPanel productPanel = new JPanel();
        productPanel.setPreferredSize(new Dimension(170, 250));
        productPanel.setMaximumSize(new Dimension(170, 250));
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        //endregion

        //region Icon

        JPanel iconPanel = new JPanel();
        iconPanel.setMaximumSize(new Dimension(170, 120));
        JLabel iconLabel;

        try {
            ImageIcon icon = new ImageIcon(Files.get.ImagesDirectory() + "\\" + productnow.getIconURL());
            Image scaledIcon = icon.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
            iconLabel = new JLabel(new ImageIcon(scaledIcon));
        } catch (Exception e) {
            iconLabel = new JLabel("[Не бе открита икона]");
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        iconPanel.setLayout(new BorderLayout());
        iconPanel.setBackground(Color.white);
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        productPanel.add(iconPanel);

        //endregion

        //region Product name

        JPanel productNamePanel = new JPanel();
        productNamePanel.setMaximumSize(new Dimension(170, 30));
        productNamePanel.setLayout(new BorderLayout());
        productNamePanel.setBackground(Color.white);
        productNamePanel.add(new JLabel(productnow.getName(), SwingConstants.CENTER), BorderLayout.CENTER);
        productPanel.add(productNamePanel);

        //endregion

        //region Description

        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setMaximumSize(new Dimension(170, 75));
        descriptionPanel.setLayout(new BorderLayout());
        descriptionPanel.setBackground(Color.white);
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JTextPane descriptionPane = new JTextPane();
        descriptionPane.setEditable(false);
        descriptionPane.setText(productnow.getInfo());
        descriptionPane.setOpaque(false);
        descriptionPane.setPreferredSize(new Dimension(170, 30));
        descriptionPane.setBorder(null);

        descriptionPanel.add(descriptionPane, BorderLayout.CENTER);
        productPanel.add(descriptionPanel);

        //endregion

        //region Price

        String priceStr = productnow.getDiscount() > 0
                ? String.format("Цена:  %.2f лв. | -%d%%", productnow.getPrice() - (productnow.getPrice() * productnow.getDiscount() / 100), productnow.getDiscount())
                : String.format("Цена:  %.2f лв.", productnow.getPrice());

        JPanel pricePanel = new JPanel();
        pricePanel.setMaximumSize(new Dimension(170, 30));
        pricePanel.setBackground(Color.white);
        pricePanel.setLayout(new BorderLayout());
        pricePanel.add(new JLabel(priceStr, SwingConstants.CENTER), BorderLayout.CENTER);
        productPanel.add(pricePanel);

        //endregion

        //region Add to shopping list

        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(170, 30));
        buttonPanel.setLayout(new BorderLayout());

        JSpinner quantitySelector = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySelector.setPreferredSize(new Dimension(65, 30));
        buttonPanel.add(quantitySelector, BorderLayout.WEST);

        JButton addButton = new JButton("Добави");
        addButton.setPreferredSize(new Dimension(100, 30));
        String imagePath = Files.get.ImagesDirectory() + "\\" + "shopping-cart-icon.png";
        ImageIcon resizedIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        addButton.setIcon(resizedIcon);
        buttonPanel.add(addButton, BorderLayout.EAST);

        productPanel.add(buttonPanel);
        addButton.addActionListener(ActionListener -> Files.shop.AddToCart(productnow, (int) quantitySelector.getValue()));
        MainWindow.shopItemCount = Files.shop.GetCount();

        //endregion

        return productPanel;

    }

    // Create categories
    private void createCategories() {

        if (categoryTree == null) {

            categoryTree = Files.category.LoadJTree(Files.get.FilesDirectory() + "\\data.txt", "Магазин - " + currentUser.getNameFull());

            categoryTree.addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) categoryTree.getLastSelectedPathComponent();

                    if (selectedNode != null && isLeafNode(selectedNode)) {
                        String command = selectedNode.getUserObject().toString();
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
                        if (parentNode != null) {
                            String parentName = parentNode.toString();
                            createPage(parentName, command);
                        }
                    }
                }
            });

            JScrollPane treeScrollPane = new JScrollPane(categoryTree);
            treeScrollPane.setPreferredSize(new Dimension(250, frame.getHeight() - 80));

            panelLeft = new JPanel();
            panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
            panelLeft.add(treeScrollPane);

            backToMain = new JButton("Връщане към началото");
            String b1imagePath = Files.get.ImagesDirectory() + "\\" + "home-icon.png";
            ImageIcon b1originalIcon = new ImageIcon(b1imagePath);
            Image b1resizedImage = b1originalIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            ImageIcon b1resizedIcon = new ImageIcon(b1resizedImage);
            backToMain.setIcon(b1resizedIcon);
            backToMain.addActionListener(this);

            String b2imagePath = Files.get.ImagesDirectory() + "\\" + "shopping-cart-icon.png";
            ImageIcon b2originalIcon = new ImageIcon(b2imagePath);
            Image b2resizedImage = b2originalIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            ImageIcon b2resizedIcon = new ImageIcon(b2resizedImage);
            myShoppingCart = new JButton("Количка");
            myShoppingCart.setIcon(b2resizedIcon);
            myShoppingCart.addActionListener(this);

            // Creating JLabel for hover text
            hoverLabel = new JLabel("Имаш " + shopItemCount + " неща в количката.");
            hoverLabel.setVisible(false);
            hoverLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            hoverLabel.setOpaque(true);
            hoverLabel.setBackground(Color.LIGHT_GRAY);

            myShoppingCart.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    updateShoppingCartCount();
                    hoverLabel.setVisible(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoverLabel.setVisible(false);
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
            buttonPanel.add(backToMain);
            buttonPanel.add(myShoppingCart);
            buttonPanel.add(hoverLabel);

            panelLeft.add(buttonPanel);

            frame.add(panelLeft, BorderLayout.WEST);

            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            bottomPanel.add(backToMain);
            bottomPanel.add(myShoppingCart);

            frame.add(bottomPanel, BorderLayout.SOUTH);
            frame.revalidate();
            frame.repaint();

            updateShoppingCartCount();

        }

    }

    // Update shopping cart count
    public void updateShoppingCartCount() {
        shopItemCount = Files.shop.GetCount();
        if (shopItemCount > 0) {
            hoverLabel.setText(" Имаш " + shopItemCount + " неща в количката. ");
        } else hoverLabel.setText(" Количката е празна. ");
        hoverLabel.revalidate();
        hoverLabel.repaint();
    }

    // Create welcome panel
    private void createWelcomePanel(String title) {

        panelWelcome = new JPanel(new BorderLayout()); // Initialize the panelWelcome reference

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel(title, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center in BoxLayout

        // Buttons
        JButton[] buttons = {
                createButton = new JButton("Създаване"),
                editButton = new JButton("Редактиране"),
                profileButton = new JButton("Профил"),
                refreshTree = new JButton("Обнови страничното меню")
        };
        createButtonList(buttons, new Dimension(250, 35));

        // Button icons
        String[] iconPaths = {
                "create-icon.png",
                "edit-icon.png",
                "user-icon.png",
                "refresh-file-icon.png"
        };
        setButtonListIcons(buttons, iconPaths, 20);

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        addButtonList(buttons, contentPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 100)));
        contentPanel.add(Box.createVerticalGlue());

        panelWelcome.add(contentPanel, BorderLayout.CENTER);
        frame.add(panelWelcome, BorderLayout.CENTER);

    }

    // Create shop items
    private void createShopPanel(String mainCategory, String subCategory) {

        Product[] products = Files.product.loadFromFileList(mainCategory, subCategory);

        panelProduct = createProductCards(products);
        frame.add(panelProduct, BorderLayout.CENTER);

    }

    // Check if node is not a parent
    private boolean isLeafNode(DefaultMutableTreeNode node) {

        if (node.getChildCount() > 0) return false;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        if (parent != null && parent.getParent() == null) return false;
        return true;

    }

    // Refresh menus
    private void createPage(String pageId, String category) {

        // Remove
        if (panelWelcome != null) frame.getContentPane().remove(panelWelcome);
        if (panelProduct != null) frame.getContentPane().remove(panelProduct);

        // Make menus
        if (pageId.equals("home")) createWelcomePanel("Добре дошъл, " + currentUser.getNameFirst() + "!");
        else createShopPanel(pageId, category);

        // Revalidate
        frame.revalidate();
        frame.repaint();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Profile menu
        if (e.getSource() == profileButton) {
            new UserInfo(xStart, yStart, false);
        }

        // Create ..
        if (e.getSource() == createButton) {
            new CreateSelector(xStart, yStart);
        }

        // Edit ..
        if (e.getSource() == editButton) {
            new EditSelector(xStart, yStart);
        }

        // Refresh tree section
        if (e.getSource() == refreshTree) {

            int xStart = frame.getX() + (frame.getWidth() / 2);
            int yStart = frame.getY() + (frame.getHeight() / 2);
            frame.dispose();
            new MainWindow(xStart, yStart, currentUser);

        }

        // Go to main menu
        if (e.getSource() == backToMain) {
            createPage("home", null);
        }

        // Show shopping cart
        if (e.getSource() == myShoppingCart) {
            if (Files.shop.Exists()) {
                if (!isPanelExists(ShoppingCart.class)) new ShoppingCart(xStart, yStart);
            } else JOptionPane.showMessageDialog(frame, "Няма продукти в кошницата!");
        }

    }

}