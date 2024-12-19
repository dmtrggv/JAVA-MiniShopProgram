package use;

import java.io.*;
import components.Category;
import components.Product;
import components.User;
import javax.swing.*;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;

public class Files {

    // Get directories
    public static class get {

        // Files directory
        public static String FilesDirectory() {
            String root = System.getProperty("user.dir");
            return Paths.get(root, "files").toString();
        }

        // Users directory
        public static String UsersDirectory() {
            return Paths.get(FilesDirectory(), "users").toString();
        }

        // Images directory
        public static String ImagesDirectory() {
            return Paths.get(FilesDirectory(), "images").toString();
        }

    }

    // User
    public static class user {

        // If user exists
        public static boolean UsernameExists(String username) {

            File file = new File(get.UsersDirectory() + "\\users.txt");

            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] fields = line.split(";\\s*");
                        String usernameCheck = fields[0];
                        if (usernameCheck.equals(username)) {
                            return true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return false;

        }

        // Save user info
        public static void SaveToFile(User user) {

            // File path
            String filePath = get.UsersDirectory() + "\\users.txt";

            // Read all users from the file
            List<String> usersList = new ArrayList<>();
            boolean usernameExists = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    usersList.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Prepare the new user data
            String userData = String.format(
                    "%s; %s; %s; %s; %s; %s; %s%n",
                    user.getUsername(), user.getPassword(), user.getNameFirst(), user.getNameLast(),
                    (user.getAddress() != null) ? user.getAddress().toString() : "N/A",
                    (user.getInfo() != null) ? user.getInfo().replace("\n", "\\n") : "N/A",
                    (user.getCompanyName() != null) ? user.getCompanyName() : "N/A"
            );

            // Check if the username already exists
            for (int i = 0; i < usersList.size(); i++) {
                String currentUserData = usersList.get(i);
                String currentUsername = currentUserData.split(";")[0].trim(); // Assuming usernames are the first part of the string
                if (currentUsername.equals(user.getUsername())) {
                    usersList.set(i, userData);
                    usernameExists = true;
                    break;
                }
            }

            // If the username doesn't exist
            if (!usernameExists) {
                usersList.add(userData);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (String userLine : usersList) {
                    writer.write(userLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // Load user info
        public static User LoadFromFile(String username, String password) {

            // File path
            String filePath = get.UsersDirectory() + "\\users.txt";

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

                String line;
                while ((line = reader.readLine()) != null) {

                    // Split
                    String[] parts = line.split("; ");

                    // Username and password
                    String fileUsername = parts[0];
                    String filePassword = parts[1];

                    // If username and password match
                    if (fileUsername.equals(username) && filePassword.equals(password)) {
                        if (parts.length == 7) {
                            String nameFirst = parts[2];
                            String nameLast = parts[3];
                            Address address = new Address(parts[4]);
                            String info = parts[5].replace("//n", "/n");
                            String companyName = parts[6];
                            return new User(fileUsername, filePassword, nameFirst, nameLast, address, info, companyName);
                        } else if (parts.length == 6) {
                            String nameFirst = parts[2];
                            String nameLast = parts[3];
                            Address address = new Address(parts[4]);
                            String info = parts[5].replace("//n", "/n");
                            return new User(fileUsername, filePassword, nameFirst, nameLast, address, info, null);
                        } else {
                            String nameFirst = parts[2];
                            String nameLast = parts[3];
                            Address address = new Address(parts[4]);
                            return new User(fileUsername, filePassword, nameFirst, nameLast, address, null, null);
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            // User doesn't exists
            return null;

        }

    }

    // Categories
    public static class category {

        // Load Tree from file
        public static JTree LoadJTree(String filePath, String title) {

            // Root name
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(title);

            // File
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                DefaultMutableTreeNode currentMainCategory = null;
                DefaultMutableTreeNode currentSubCategory = null;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.startsWith("<") && line.endsWith(">") && !line.startsWith("</")) {
                        String mainCategory = line.substring(1, line.length() - 1);
                        mainCategory = inclass.formatCategoryName(mainCategory);
                        currentMainCategory = new DefaultMutableTreeNode(mainCategory);
                        root.add(currentMainCategory);
                    } else if (line.startsWith("</") && line.endsWith(">")) {
                        currentMainCategory = null;
                    } else if (line.startsWith("_") && currentMainCategory != null) {
                        String subCategory = line.substring(1);
                        subCategory = inclass.formatCategoryName(subCategory);
                        currentSubCategory = new DefaultMutableTreeNode(subCategory);
                        currentMainCategory.add(currentSubCategory);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Return tree
            return new JTree(root);
        }

        // If category exists
        public static boolean Exists(Category category) {

            // Categories
            String mainCategory = (category.getMain() != null) ? category.getMain().toLowerCase().replace(" ", "-") : null;
            String subCategory = (category.getSub() != null) ? category.getSub().toLowerCase().replace(" ", "-") : null;

            // File path
            String filePath = get.FilesDirectory() + "\\data.txt";
            File file = new File(filePath);

            // If file doesn't exist
            if (!file.exists()) return false;

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

                String line;
                boolean mainCategoryFound = false;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    //
                    if (mainCategory != null && subCategory == null) {
                        if (line.equals("<" + mainCategory + ">")) {
                            return true;
                        }
                    }

                    //
                    if (mainCategory != null && subCategory != null) {
                        if (line.equals("<" + mainCategory + ">")) {
                            mainCategoryFound = true;
                        } else if (mainCategoryFound && line.equals("_" + subCategory)) {
                            return true;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;

        }

        // Save category to file
        public static void SaveToFile(Category category, String replace) {

            String mainCategory = (category.getMain() != null) ? category.getMain().toLowerCase().replace(" ", "-") : null;
            String subCategory = (category.getSub() != null) ? category.getSub().toLowerCase().replace(" ", "-") : null;
            String newCategoryName = (replace != null) ? replace.toLowerCase().replace(" ", "-") : null;
            String filePath = get.FilesDirectory() + "\\data.txt";

            try {

                File file = new File(filePath);
                if (!file.exists()) file.createNewFile();

                StringBuilder fileContent = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }
                }

                String content = fileContent.toString();

                if (replace == null) {

                    //region Create new

                    // New main category
                    if (mainCategory != null && subCategory == null) {
                        if (!content.contains("<" + mainCategory + ">")) {
                            content += "<" + mainCategory + ">\n</" + mainCategory + ">\n";
                        }
                    }

                    // New sub category
                    if (mainCategory != null && subCategory != null) {
                        if (content.contains("<" + mainCategory + ">")) {
                            if (!content.contains("_" + subCategory)) {
                                content = content.replaceFirst(
                                        "(<" + mainCategory + ">)",
                                        "$1\n    _" + subCategory
                                );
                            }
                        }
                    }

                    //endregion

                } else {

                    //region Rename

                    // Rename main
                    if (mainCategory != null && subCategory == null) {

                        // Replace main category name
                        content = content.replaceAll("<" + mainCategory + ">", "<" + newCategoryName + ">");
                        content = content.replaceAll("</" + mainCategory + ">", "</" + newCategoryName + ">");

                    }

                    // Rename sub
                    if (mainCategory != null && subCategory != null) {

                        // Find <main-category>...</main-category>
                        Pattern pattern = Pattern.compile("(<" + mainCategory + ">.*?)(" + subCategory + ")(.*?</" + mainCategory + ">)", Pattern.DOTALL);
                        Matcher matcher = pattern.matcher(content);

                        // Replace only inside <main-category>...</main-category>
                        StringBuilder result = new StringBuilder();
                        while (matcher.find()) {
                            String modifiedBlock = matcher.group(1) + matcher.group(2).replace(subCategory, newCategoryName) + matcher.group(3);
                            matcher.appendReplacement(result, modifiedBlock);
                        }

                        // Apply changes
                        matcher.appendTail(result);
                        content = result.toString();

                    }

                    //endregion

                }

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write(content);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // Delete category from file
        public static void DeleteFromFile(Category category) {

            String mainCategory = (category.getMain() != null) ? category.getMain().toLowerCase().replace(" ", "-") : null;
            String subCategory = (category.getSub() != null) ? category.getSub().toLowerCase().replace(" ", "-") : null;
            String filePath = get.FilesDirectory() + "\\data.txt";

            try {

                File file = new File(filePath);
                if (!file.exists()) return;

                StringBuilder fileContent = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }
                }

                String content = fileContent.toString();

                if (mainCategory != null && subCategory != null) {
                    String productAndSubCategoryPattern = "(?m)\\s*_" + subCategory + ".*|\\s*\\*.*";
                    content = content.replaceAll(productAndSubCategoryPattern, "");
                }

                if (mainCategory != null && subCategory == null) {
                    content = content.replaceAll("(?s)<" + mainCategory + ">.*?</" + mainCategory + ">", "");
                    content = content.replaceAll("(?m)^[\\s]*\n", "");
                }

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write(content);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // Load main categories
        public static Object[][] LoadListMain() {

            // Variables
            List<Object[]> mainCategories = new ArrayList<>();
            String filePath = get.FilesDirectory() + "\\data.txt";

            //region Read file

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

                // Lines
                String line;

                //region Add main category to the list

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    // Add main category
                    if (line.startsWith("<") && line.endsWith(">") && !line.startsWith("</")) {
                        String mainCategory = inclass.formatCategoryName(line.substring(1, line.length() - 1));
                        mainCategories.add(new Object[]{mainCategory});
                    }
                }

                //endregion

            } catch (IOException e) {
                e.printStackTrace();
            }

            //endregion

            // Return result
            return mainCategories.toArray(new Object[0][]);

        }

        // Load subcategories for a specific main category
        public static Object[][] LoadListSub(String mainCategory) {

            // Variables
            List<Object[]> subCategories = new ArrayList<>();
            String filePath = get.FilesDirectory() + "\\data.txt";

            //region Read file

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

                // Lines and main category checker
                String line;
                boolean inMainCategory = false;

                while ((line = reader.readLine()) != null) {

                    // Lines
                    line = line.trim();

                    //region Main category

                    // Start check
                    if (line.startsWith("<") && line.endsWith(">") && !line.startsWith("</")) {
                        String currentMainCategory = inclass.formatCategoryName(line.substring(1, line.length() - 1));
                        inMainCategory = currentMainCategory.equals(mainCategory);
                    }

                    // End check
                    if (line.startsWith("</") && line.endsWith(">")) {
                        inMainCategory = false;
                    }

                    //endregion

                    //region Sub category

                    if (inMainCategory && line.startsWith("_")) {
                        subCategories.add(new Object[]{inclass.formatCategoryName(line.substring(1))});
                    }

                    //endregion
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //endregion

            // Return result
            return subCategories.toArray(new Object[0][]);

        }

        // Load products from a specific main and sub category
        public static Object[][] LoadListItem(String mainCategory, String subCategory) {

            // Create variable
            List<Object[]> productNames = new ArrayList<>();

            try {

                //region Load file content

                File dataFile = new File(get.FilesDirectory() + "\\data.txt");
                if (!dataFile.exists()) {
                    JOptionPane.showMessageDialog(null, "Dev: Файлът не съществува :(");
                    return new Object[0][];
                }

                List<String> fileLines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileLines.add(line);
                    }
                }

                //endregion

                //region Get item from the categories

                boolean inMainCategory = false;
                boolean inSubCategory = false;

                for (String line : fileLines) {

                    // Lines
                    line = line.trim();

                    // Main category
                    if (line.equalsIgnoreCase("<" + mainCategory.toLowerCase().replace(" ", "-") + ">")) {
                        inMainCategory = true;
                    } else if (line.startsWith("<") && line.endsWith(">")) {
                        inMainCategory = false;
                    }

                    // Sub category
                    if (inMainCategory && line.equalsIgnoreCase("_" + subCategory.toLowerCase().replace(" ", "-"))) {
                        inSubCategory = true;
                    } else if (inMainCategory && line.startsWith("_")) {
                        inSubCategory = false;
                    }

                    // Item - product
                    if (inSubCategory && line.startsWith("*")) {
                        String[] parts = line.split(";");
                        if (parts.length > 0) {
                            String productName = parts[0].replace("*", "").trim();
                            productNames.add(new Object[]{productName});
                        }
                    }

                }

                //endregion

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Product names
            return productNames.toArray(new Object[0][]);

        }

    }

    // Product
    public static class product {

        // If product exists
        public static boolean Exists(Product product) {

            try {

                //region File setup

                File dataFile = new File(get.FilesDirectory() + "\\data.txt");
                if (!dataFile.exists()) {
                    return false;
                }

                List<String> fileLines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileLines.add(line);
                    }
                }

                //endregion

                //region Parse data

                String categoryMain = product.getCategoryMain();
                String categorySub = product.getCategorySub();

                for (int i = 0; i < fileLines.size(); i++) {
                    if (fileLines.get(i).trim().equals("<" + categoryMain.toLowerCase().replace(" ", "-") + ">")) {
                        while (i + 1 < fileLines.size() && fileLines.get(i + 1).startsWith("    ")) {
                            String currentSubcategory = fileLines.get(i + 1).trim();
                            if (currentSubcategory.equals("_" + categorySub.toLowerCase().replace(" ", "-"))) {
                                for (int j = i + 2; j < fileLines.size() && fileLines.get(j).startsWith("        *"); j++) {
                                    if (fileLines.get(j).contains("*" + product.getName() + ";")) {
                                        return true;
                                    }
                                }
                                break;
                            }
                            i++;
                        }
                    }
                }

                //endregion

                return false;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        // Save product to file
        public static void SaveToFile(Product product) {

            try {

                //region File setup

                File dataFile = new File(get.FilesDirectory() + "\\data.txt");
                if (!dataFile.exists()) {
                    dataFile.getParentFile().mkdirs();
                    dataFile.createNewFile();
                }

                List<String> fileLines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileLines.add(line);
                    }
                }

                //endregion

                //region Handle icon

                // Variables
                String categoryMain = product.getCategoryMain();
                String categorySub = product.getCategorySub();
                String iconURL = product.getIconURL();
                String iconFileName = Paths.get(iconURL).getFileName().toString();
                String destinationIconPath = get.ImagesDirectory() + File.separator + iconFileName;
                File iconFile = new File(iconURL);

                if (!iconFile.getAbsolutePath().startsWith(get.ImagesDirectory())) {
                    try {
                        java.nio.file.Files.copy(iconFile.toPath(), new File(destinationIconPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Dev: Не можа да се премести иконата :(");
                        e.printStackTrace();
                        return;
                    }
                }

                //endregion

                //region Save format

                String productLine = String.format(
                        "        *%s; %.2f; %s; %d; %s; %s; %s; %s",
                        product.getName(),
                        product.getPrice(),
                        iconFileName,
                        product.getDiscount(),
                        product.getUserFrom(),
                        product.getInfo().replace("\n", "\\n"),
                        product.getDateAdded().toString(true),
                        product.getDateLastChange().toString(true)
                );

                //endregion

                //region Search for subcategory and replace or add product

                boolean subCategoryFound = false;
                boolean productReplaced = false;

                for (int i = 0; i < fileLines.size(); i++) {
                    String currentLine = fileLines.get(i).trim();
                    String categoryMainTag = "<" + categoryMain.toLowerCase().replace(" ", "-") + ">";

                    if (currentLine.equals(categoryMainTag)) {

                        // Search for subcategory
                        int subcategoryIndex = i + 1;
                        while (subcategoryIndex < fileLines.size() && fileLines.get(subcategoryIndex).startsWith("    ")) {
                            String subcategoryLine = fileLines.get(subcategoryIndex).trim();
                            String expectedSubcategoryTag = "_" + categorySub.toLowerCase().replace(" ", "-");

                            if (subcategoryLine.equals(expectedSubcategoryTag)) {
                                subCategoryFound = true;

                                // Search for product and replace it if found
                                int productIndex = subcategoryIndex + 1;
                                while (productIndex < fileLines.size() && fileLines.get(productIndex).startsWith("        *")) {
                                    String productLineInFile = fileLines.get(productIndex).trim();

                                    // If the product name exists in the file, replace it
                                    if (productLineInFile.contains("*" + product.getName() + ";")) {
                                        fileLines.set(productIndex, productLine);
                                        productReplaced = true;
                                        break;
                                    }

                                    productIndex++;
                                }

                                // If the product was not found and replaced, add the new product
                                if (!productReplaced) {
                                    // Add product after the last subcategory item
                                    fileLines.add(subcategoryIndex + 1, productLine);
                                }

                                break;
                            }
                            subcategoryIndex++;
                        }

                        break;
                    }
                }

                //endregion

                //region When subcategory not found

                if (!subCategoryFound) {
                    JOptionPane.showMessageDialog(null, "Dev: Няма такава категория: " + categorySub);
                } else {
                    // Write the updated file
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
                        for (String line : fileLines) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }

                }

                //endregion

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // Load product to file
        public static Product LoadFromFile(String categoryMain, String categorySub, String productName) {

            try {

                //region File and data preparation

                File dataFile = new File(get.FilesDirectory() + "\\data.txt");
                if (!dataFile.exists()) {
                    JOptionPane.showMessageDialog(null, "Dev: Файлът не съществува.");
                    return null;
                }

                List<String> fileLines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileLines.add(line);
                    }
                }

                //endregion

                //region Search for product

                for (int i = 0; i < fileLines.size(); i++) {
                    String currentLine = fileLines.get(i).trim();
                    String categoryMainTag = "<" + categoryMain.toLowerCase().replace(" ", "-") + ">";

                    if (currentLine.equals(categoryMainTag)) {
                        int subcategoryIndex = i + 1;
                        while (subcategoryIndex < fileLines.size() && fileLines.get(subcategoryIndex).startsWith("    ")) {
                            String subcategoryLine = fileLines.get(subcategoryIndex).trim();
                            String expectedSubcategoryTag = "_" + categorySub.toLowerCase().replace(" ", "-");

                            if (subcategoryLine.equals(expectedSubcategoryTag)) {
                                int productIndex = subcategoryIndex + 1;
                                while (productIndex < fileLines.size() && fileLines.get(productIndex).startsWith("        *")) {
                                    String productLine = fileLines.get(productIndex).trim();
                                    if (productLine.contains("*")) {
                                        String[] parts = productLine.split(";");
                                        if (parts.length >= 8) {
                                            String name = parts[0].replace("*", "").trim();
                                            if (name.equalsIgnoreCase(productName)) {
                                                float price = Float.parseFloat(parts[1].trim().replace(",", "."));
                                                String iconURL = parts[2].trim();
                                                int discount = Integer.parseInt(parts[3].trim());
                                                String userFrom = parts[4].trim();
                                                String info = parts[5].trim().replace("\\n", "\n");
                                                String dateAdded = parts[6].trim();
                                                String dateLastChange = parts[7].trim();

                                                return new Product(
                                                        iconURL,
                                                        name,
                                                        price,
                                                        discount,
                                                        new Date(dateAdded),
                                                        new Date(dateLastChange),
                                                        categoryMain,
                                                        categorySub,
                                                        userFrom,
                                                        info
                                                );
                                            }
                                        }
                                    }
                                    productIndex++;
                                }
                            }
                            subcategoryIndex++;
                        }
                    }
                }

                //endregion

                // Product not found
                JOptionPane.showMessageDialog(null, "Dev: Не бе намерен продукт с дадените характеристики.");

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        // Load all products from file
        public static Product[] loadFromFileList(String categoryMain, String categorySub) {

            // Product list
            List<Product> productList = new ArrayList<>();

            try {

                //region File setup

                File dataFile = new File(get.FilesDirectory() + "\\data.txt");
                if (!dataFile.exists()) {
                    JOptionPane.showMessageDialog(null, "Dev: Файлът не съществува.");
                    return new Product[0];
                }

                List<String> fileLines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileLines.add(line);
                    }
                }

                //endregion

                //region Search for products

                for (int i = 0; i < fileLines.size(); i++) {
                    String currentLine = fileLines.get(i).trim();
                    String categoryMainTag = "<" + categoryMain.toLowerCase().replace(" ", "-") + ">";

                    if (currentLine.equals(categoryMainTag)) {
                        int subcategoryIndex = i + 1;
                        while (subcategoryIndex < fileLines.size() && fileLines.get(subcategoryIndex).startsWith("    ")) {
                            String subcategoryLine = fileLines.get(subcategoryIndex).trim();
                            String expectedSubcategoryTag = "_" + categorySub.toLowerCase().replace(" ", "-");

                            if (subcategoryLine.equals(expectedSubcategoryTag)) {
                                int productIndex = subcategoryIndex + 1;
                                while (productIndex < fileLines.size() && fileLines.get(productIndex).startsWith("        *")) {
                                    String productLine = fileLines.get(productIndex).trim();
                                    if (productLine.contains("*")) {
                                        String[] parts = productLine.split(";");
                                        if (parts.length >= 8) {
                                            String name = parts[0].replace("*", "").trim();
                                            float price = Float.parseFloat(parts[1].trim().replace(",", "."));
                                            String iconURL = parts[2].trim();
                                            int discount = Integer.parseInt(parts[3].trim());
                                            String userFrom = parts[4].trim();
                                            String info = parts[5].trim().replace("\\n", "\n");
                                            String dateAdded = parts[6].trim();
                                            String dateLastChange = parts[7].trim();

                                            Product product = new Product(
                                                    iconURL,
                                                    name,
                                                    price,
                                                    discount,
                                                    new Date(dateAdded),
                                                    new Date(dateLastChange),
                                                    categoryMain,
                                                    categorySub,
                                                    userFrom,
                                                    info
                                            );
                                            productList.add(product);
                                        }
                                    }
                                    productIndex++;
                                }
                            }
                            subcategoryIndex++;
                        }
                    }
                }

                //endregion

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Return array
            return productList.toArray(new Product[0]);

        }

        // Delete product from file
        public static void DeleteFromFile(String categoryMain, String categorySub, String productName) {

            try {

                //region File preparation

                File dataFile = new File(get.FilesDirectory() + "\\data.txt");
                if (!dataFile.exists()) {
                    JOptionPane.showMessageDialog(null, "Dev: Файлът не съществува.");
                }

                List<String> fileLines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileLines.add(line);
                    }
                }

                //endregion

                //region Search and remove product

                // Product is deleted checker
                boolean productDeleted = false;

                for (int i = 0; i < fileLines.size(); i++) {
                    String currentLine = fileLines.get(i).trim();
                    String categoryMainTag = "<" + categoryMain.toLowerCase().replace(" ", "-") + ">";

                    if (currentLine.equals(categoryMainTag)) {
                        int subcategoryIndex = i + 1;
                        while (subcategoryIndex < fileLines.size() && fileLines.get(subcategoryIndex).startsWith("    ")) {
                            String subcategoryLine = fileLines.get(subcategoryIndex).trim();
                            String expectedSubcategoryTag = "_" + categorySub.toLowerCase().replace(" ", "-");

                            if (subcategoryLine.equals(expectedSubcategoryTag)) {
                                int productIndex = subcategoryIndex + 1;
                                while (productIndex < fileLines.size() && fileLines.get(productIndex).startsWith("        *")) {
                                    String productLine = fileLines.get(productIndex).trim();
                                    if (productLine.contains("*")) {
                                        String[] parts = productLine.split(";");
                                        if (parts.length >= 8) {
                                            String name = parts[0].replace("*", "").trim();
                                            if (name.equalsIgnoreCase(productName)) {
                                                fileLines.remove(productIndex);
                                                productDeleted = true;
                                                break;
                                            }
                                        }
                                    }
                                    productIndex++;
                                }
                                if (productDeleted) {
                                    break;
                                }
                            }
                            subcategoryIndex++;
                        }
                        if (productDeleted) {
                            break;
                        }
                    }
                }

                //endregion

                //region Only if product was deleted

                if (productDeleted) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
                        for (String line : fileLines) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Dev: Продуктът не е открит.");
                }

                //endregion

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    // Shop
    public static class shop {

        // Check if shopping list exists
        public static boolean Exists() {

            // File path
            String filePath = get.FilesDirectory() + "\\shopping-cart.txt";
            File file = new File(filePath);

            return file.exists();

        }

        // Delete shopping list
        public static void Destroy() {

            // File path
            String filePath = get.FilesDirectory() + "\\shopping-cart.txt";
            File file = new File(filePath);

            if (file.exists()) file.delete();

        }

        // Add product to card
        public static void AddToCart(Product product, int count) {

            // File Setup
            String filePath = get.FilesDirectory() + "\\shopping-cart.txt";
            File file = new File(filePath);
            List<String> shopList = new ArrayList<>();
            boolean productExists = false;

            //region Read Existing Cart

            try {

                if (file.createNewFile()) {
                    JOptionPane.showMessageDialog(null, "Създаен бе нов списък с продукти.");
                }

                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(";");
                        if (parts.length < 3) continue;

                        String currentProductName = parts[0].trim();
                        if (currentProductName.equals(product.getName())) {
                            // Update existing item's count
                            int currentItemCount = Integer.parseInt(parts[2].trim());
                            currentItemCount += count;
                            line = String.format(
                                    "%s; %.2f; %d", product.getName(),
                                    product.getPrice() - (product.getPrice() * product.getDiscount() / 100),
                                    currentItemCount
                            );
                            productExists = true;
                        }

                        shopList.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            //endregion

            // Add product if not exists
            if (!productExists) {
                String cartData = String.format("%s; %.2f; %d", product.getName(), product.getPrice() - (product.getPrice() * product.getDiscount() / 100), count);
                shopList.add(cartData);
            }

            //region Write updated data

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (String shopLine : shopList) {
                    writer.write(shopLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //endregion

        }

        // Load list from file
        public static Object[][] LoadItems() {

            // File path
            String filePath = get.FilesDirectory() + "\\shopping-cart.txt";
            File file = new File(filePath);

            // Create list
            List<Object[]> itemList = new ArrayList<>();

            // If file exists
            if (!file.exists()) {
                System.err.println("File does not exist: " + filePath);
                return new Object[0][0];
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length >= 3) {

                        String name = parts[0].trim();
                        double price = Double.parseDouble(parts[1].trim().replace(",", "."));
                        int count = Integer.parseInt(parts[2].trim());
                        double total = price * count;

                        // Add
                        itemList.add(new Object[]{name, price, count, total});

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Convert to [][] array
            return itemList.toArray(new Object[0][0]);

        }

        // Update list
        public static void UpdateItems(JTable table) {

            //region Extract table

            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            int rowCount = tableModel.getRowCount();
            List<String> shopList = new ArrayList<>();

            //endregion

            //region Read data

            for (int i = 0; i < rowCount; i++) {

                // Name
                String name = tableModel.getValueAt(i, 0).toString();

                // Price
                double price = Double.parseDouble(tableModel.getValueAt(i, 1).toString());

                // Count
                int count = Integer.parseInt(tableModel.getValueAt(i, 2).toString());

                // Write in file
                String shopLine = String.format("%s; %.2f; %d", name, price, count);
                shopList.add(shopLine);

            }

            //endregion

            //region Write updated data

            String filePath = get.FilesDirectory() + "\\shopping-cart.txt";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (String shopLine : shopList) {
                    writer.write(shopLine);
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(null, "Списъкът беше успешно актуализиран.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Неуспешно актуализиране на списъка.");
                e.printStackTrace();
            }

            //endregion

        }

        // Get items count
        public static int GetCount() {

            String filePath = get.FilesDirectory() + "\\shopping-cart.txt";
            File file = new File(filePath);

            if (!file.exists()) return 0;

            int totalCount = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length >= 3) {
                        try {
                            int count = Integer.parseInt(parts[2].trim());
                            totalCount += count;
                        } catch (NumberFormatException ignored) {
                            // Ignore invalid numbers
                        }
                    }
                }
            } catch (IOException ignored) {
                // Ignore IO exceptions for simplicity
            }

            return totalCount;

        }

    }

    // In-class usage - a private methods only for here
    private static class inclass {

        // Format category name
        private static String formatCategoryName(String categoryName) {

            // Replace the delimiters ('-' and '_') with spaces
            categoryName = categoryName.replace('-', ' ').replace('_', ' ');

            // Split string into words
            String[] words = categoryName.split(" ");
            StringBuilder formattedName = new StringBuilder();

            if (words.length > 0) {
                // Capitalize only the first word's first letter, the rest lowercase
                formattedName.append(Character.toUpperCase(words[0].charAt(0)))
                        .append(words[0].substring(1).toLowerCase());
            }

            // Append remaining words in lowercase
            for (int i = 1; i < words.length; i++) {
                formattedName.append(" ")
                        .append(words[i].toLowerCase());
            }

            return formattedName.toString().trim();
        }

    }

}
