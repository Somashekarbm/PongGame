import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminLoginSignupPage extends JFrame {
    private DatabaseManager rankingsManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    // private String jdbcURL = "jdbc:mysql://localhost:3306/ponggamedb";
    private String username;
    private String password;

    public AdminLoginSignupPage(DatabaseManager rankingsManager) {
        this.rankingsManager = rankingsManager;

        setTitle("Admin Login/Signup");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        panel.add(usernameLabel);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        panel.add(passwordLabel);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                password = new String(passwordField.getPassword());
                if (validateLogin(username, password)) {
                    openAdminModule();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(AdminLoginSignupPage.this, "Invalid username or password.");
                }
            }
        });
        panel.add(loginButton);

        JButton signupButton = new JButton("Signup");
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (!username.isEmpty() && !password.isEmpty()) {
                    signupAdmin(username, password);
                } else {
                    JOptionPane.showMessageDialog(AdminLoginSignupPage.this, "Username and password cannot be empty.");
                }
            }
        });
        panel.add(signupButton);

        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private boolean validateLogin(String username, String password) {
        return username.equals("root") && password.equals("Bandisomu2@");
    }

    private void signupAdmin(String username, String password) {
        // You can implement admin signup functionality if needed
        JOptionPane.showMessageDialog(AdminLoginSignupPage.this, "Admin signup feature not implemented.");
    }

    private void openAdminModule() {
        Admin.main(new String[] { "jdbc:mysql://localhost:3306/ponggamedb", username, password });
    }
}
