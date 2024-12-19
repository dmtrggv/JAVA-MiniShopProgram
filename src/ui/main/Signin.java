package ui.main;

import components.User;
// import ui.dialog.VerifyAdmin;
import ui.Panels;
import ui.frame.UserInfo;
import use.Constants;
import use.Files;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// ТОВА НЕ ГО БАРАМ В НИКАКЪВ СЛУЧАЙ :) МЪРЗИ МЕ ДА ГО ОПТИМИЗИРАМ

public class Signin extends Panels implements ActionListener {

    private static boolean isLoggedIn = false;

    public static JFrame frame;
    JButton buttLanguage = new JButton("BG");
    JButton buttLogin = new JButton("Влез");
    JButton buttCreateAccount = new JButton("Създай профил");
    JTextField txUsername = new JTextField();
    JPasswordField txPassword = new JPasswordField();
    JLabel creator = new JLabel("Created by " + Constants.app.DEVELOPER + ", 2101261032");
    JLabel copyright = new JLabel(Constants.app.APP_NAME + " v1.0.0 © Copyright " + Constants.app.RELEASE_YEAR);
    JCheckBox viewPassword = new JCheckBox("показване на паролата");

    public Signin() {

        // Create frame
        frame = initializeFrame(screenSize.width / 2, screenSize.height / 2, 350, 400, "Влез в профил", MainWindow.class);

        // Panel
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setLayout(null);

        // Language button
        buttLanguage.setBounds(270, 10, 50, 30);
        buttLanguage.setFocusable(false);
        buttLanguage.addActionListener(this);

        // Username
        JLabel usernameLabel = new JLabel("Потребителско име:");
        usernameLabel.setBounds(20, 40, 150, 30);
        txUsername.setBounds(20, 65, 300, 35);
        txUsername.setFont(new Font("Sans Serif", 0, 15));
        txUsername.setFocusable(true);

        // Password
        JLabel passwordLabel = new JLabel("Парола:");
        passwordLabel.setBounds(20, 100, 100, 30);
        txPassword.setBounds(20, 125, 300, 35);
        txPassword.setFont(new Font("Sans Serif", 0, 15));

        // View password
        viewPassword.setBounds(20, 160, 300, 30);
        viewPassword.addActionListener(this);
        viewPassword.setBackground(Color.white);

        // Login button
        buttLogin.setBounds(17, 210, 300, 40);
        buttLogin.setFocusable(false);
        buttLogin.addActionListener(this);
        buttLogin.setFocusable(true);

        // Close button
        buttCreateAccount.setBounds(17, 260, 300, 40);
        buttCreateAccount.setFocusable(false);
        buttCreateAccount.addActionListener(this);
        buttCreateAccount.setFocusable(true);

        // Creator
        creator.setFont(new Font("Sans Serif", Font.BOLD, 12));
        creator.setForeground(Color.gray);

        // Copyright
        copyright.setFont(new Font("Sans Serif", Font.BOLD, 12));
        copyright.setForeground(Color.gray);

        // Set panel
        panel.add(buttLanguage);
        panel.add(usernameLabel);
        panel.add(txUsername);
        panel.add(passwordLabel);
        panel.add(txPassword);
        panel.add(viewPassword);
        panel.add(buttLogin);
        panel.add(buttCreateAccount);
        panel.add(creator);
        panel.add(copyright);
        frame.add(panel);
        frame.setVisible(true);
        updateCreatorPosition(panel);

        JOptionPane.showMessageDialog(frame, "Има готов потребител:\n - user: pu.fmi\n - pass: fmi\nНо ако искаш, създай нов потребтел и почни отначало");
    }

    private void updateCreatorPosition(JPanel panel) {

        int pW = panel.getWidth();
        int pH = panel.getHeight();
        int cW = creator.getPreferredSize().width;
        int cpW = copyright.getPreferredSize().width;

        // Change creator position
        creator.setBounds(((pW - cW) / 2) - 5, pH - 40, cW * 2, 15);
        copyright.setBounds(((pW - cpW) / 2) - 5, pH - 25, cpW * 2, 15);

        // Refresh
        panel.revalidate();
        panel.repaint();
    }

    //region Login status

    public static void setLoginStatus(boolean status) {
        isLoggedIn = status;
    }

    public static boolean getLoginStatus() {
        return isLoggedIn;
    }

    //endregion

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == viewPassword) {

            if (txPassword.getEchoChar() == (char) 0) {
                txPassword.setEchoChar('*');
            } else {
                txPassword.setEchoChar((char) 0);
            }

        } if (e.getSource() == buttLogin) {

            if (!txUsername.getText().isEmpty() && !new String(txPassword.getPassword()).isEmpty()) {
                User user = Files.user.LoadFromFile(txUsername.getText(), new String(txPassword.getPassword()));
                if (user != null) {
                    new MainWindow(user);
                    frame.dispose();
                } else JOptionPane.showMessageDialog(frame, "Грешен потребител!");
            } else JOptionPane.showMessageDialog(frame, "Полетата са задължителни!");

        } else if (e.getSource() == buttCreateAccount) {

            int xStart = (frame.getX() + frame.getWidth()) / 2;
            int yStart = ((frame.getY() + frame.getHeight()) / 2) + 50;
            new UserInfo(xStart, yStart, true);

        }

    }

}
