import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourierServiceApp extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private JTextField packageField;
    private JTextField numberField;
    private JTextField weightField;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Courier> couriers;

    // JDBC connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/CourierServiceDB";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASS = "root"; // Replace with your MySQL password

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // Static block to load the JDBC driver
    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found. Please add the JDBC library to the project.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public CourierServiceApp() {
        couriers = new ArrayList<>();

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(20);
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField(20);
        JLabel packageLabel = new JLabel("Package:");
        packageField = new JTextField(20);
        JLabel numberLabel = new JLabel("Number:");
        numberField = new JTextField(20);
        JLabel weightLabel = new JLabel("Weight:");
        weightField = new JTextField(20);

        JButton addButton = new JButton("Add Courier");
        addButton.addActionListener(new AddButtonListener());

        tableModel = new DefaultTableModel(new Object[]{"Name", "Address", "Package", "Number", "Weight"}, 0);
        table = new JTable(tableModel);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(addressLabel);
        inputPanel.add(addressField);
        inputPanel.add(packageLabel);
        inputPanel.add(packageField);
        inputPanel.add(numberLabel);
        inputPanel.add(numberField);
        inputPanel.add(weightLabel);
        inputPanel.add(weightField);
        inputPanel.add(addButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        this.setTitle("Online Courier Service");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        this.setLocationRelativeTo(null);

        // Initialize database and create table
        initializeDatabase();
    }

    // Method to initialize database and create table if necessary
    private void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Create database 'CourierServiceDB' if not exists
            String createDB = "CREATE DATABASE IF NOT EXISTS CourierServiceDB";
            try (PreparedStatement statement = connection.prepareStatement(createDB)) {
                statement.execute();
            }

            // Use database 'CourierServiceDB'
            String useDB = "USE CourierServiceDB";
            try (PreparedStatement statement = connection.prepareStatement(useDB)) {
                statement.execute();
            }

            // Create table 'data1' if not exists
            String createTable = "CREATE TABLE IF NOT EXISTS data1 (" +
                    "name VARCHAR(20) NOT NULL, " +
                    "address VARCHAR(30) NOT NULL, " +
                    "packagestr VARCHAR(20) NOT NULL, " +
                    "numberstr VARCHAR(10) NOT NULL, " +
                    "weightstr INT(10) NOT NULL" +
                    ") ENGINE=InnoDB";
            try (PreparedStatement statement = connection.prepareStatement(createTable)) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String address = addressField.getText();
            String packageStr = packageField.getText();
            String numberStr = numberField.getText();
            String weightStr = weightField.getText();

            if (name.isEmpty() || address.isEmpty() || packageStr.isEmpty() || numberStr.isEmpty() || weightStr.isEmpty()) {
                JOptionPane.showMessageDialog(CourierServiceApp.this, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (name.matches(".*\\d.*")) {
                JOptionPane.showMessageDialog(CourierServiceApp.this, "Enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!numberStr.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(CourierServiceApp.this, "Enter a valid 10-digit number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (containsNumber(address)) {
                JOptionPane.showMessageDialog(CourierServiceApp.this, "Address cannot contain numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int number = Integer.parseInt(numberStr);
                int weight = Integer.parseInt(weightStr);
                Courier courier = new Courier(name, address, packageStr, number, weight);
                addCourier(courier);
                tableModel.addRow(new Object[]{name, address, packageStr, number, weight});
                clearFields();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CourierServiceApp.this, "Number and Weight must be numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean containsNumber(String address) {
        return address.matches(".*\\d.*");
    }

    private void addCourier(Courier courier) {
        couriers.add(courier);
        insertCourierIntoDB(courier);
    }

    private void insertCourierIntoDB(Courier courier) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "INSERT INTO data1 (name, address, packagestr, numberstr, weightstr) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, courier.getName());
                statement.setString(2, courier.getAddress());
                statement.setString(3, courier.getPackageStr());
                statement.setString(4, String.valueOf(courier.getNumber()));
                statement.setInt(5, courier.getWeight());
                statement.executeUpdate();
                JOptionPane.showMessageDialog(CourierServiceApp.this, "Courier added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(CourierServiceApp.this, "Error saving courier to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        addressField.setText("");
        packageField.setText("");
        numberField.setText("");
        weightField.setText("");
    }

    private class Courier {
        private String name;
        private String address;
        private String packageStr;
        private int number;
        private int weight;

        public Courier(String name, String address, String packageStr, int number, int weight) {
            this.name = name;
            this.address = address;
            this.packageStr = packageStr;
            this.number = number;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public String getPackageStr() {
            return packageStr;
        }

        public int getNumber() {
            return number;
        }

        public int getWeight() {
            return weight;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginPage();
            }
        });
    }
}

class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        this.setTitle("Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // For simplicity, we use hardcoded credentials. In a real application, you would check against a database or another secure storage.
            if (authenticate(username, password)) {
                new CourierServiceApp().setVisible(true);
                LoginPage.this.dispose();
            } else {
                JOptionPane.showMessageDialog(LoginPage.this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean authenticate(String username, String password) {
            // This is a simple hardcoded authentication. Replace this with your actual authentication logic.
            return "admin".equals(username) && "password".equals(password);
        }
    }
}
