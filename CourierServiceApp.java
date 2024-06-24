import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CourierServiceApp extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private JTextField weightField;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Courier> couriers;

    public CourierServiceApp() {
        couriers = new ArrayList<>();

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(20);
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField(20);
        JLabel weightLabel = new JLabel("Weight:");
        weightField = new JTextField(20);

        JButton addButton = new JButton("Add Courier");
        addButton.addActionListener(new AddButtonListener());

        tableModel = new DefaultTableModel(new Object[]{"Name", "Address", "Weight"}, 0);
        table = new JTable(tableModel);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(addressLabel);
        inputPanel.add(addressField);
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
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String address = addressField.getText();
            String weightStr = weightField.getText();

            if (name.isEmpty() || address.isEmpty() || weightStr.isEmpty()) {
                JOptionPane.showMessageDialog(CourierServiceApp.this, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double weight = Double.parseDouble(weightStr);
                addCourier(name, address, weight);
                tableModel.addRow(new Object[]{name, address, weight});
                nameField.setText("");
                addressField.setText("");
                weightField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CourierServiceApp.this, "Weight must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addCourier(String name, String address, double weight) {
        Courier courier = new Courier(name, address, weight);
        couriers.add(courier);
    }

    private class Courier {
        private String name;
        private String address;
        private double weight;

        public Courier(String name, String address, double weight) {
            this.name = name;
            this.address = address;
            this.weight = weight;
        }

        public String getName()
       {
            return name;
        }

        public String getAddress()
       {
            return address;
        }

        public double getWeight() 
        {
            return weight;
        }
    }
public static void main(String[] args)
 {     
        SwingUtilities.invokeLater(new Runnable() 	
	{   
            public void run() 
	{
              	  new CourierServiceApp().setVisible(true);
	}
});  
}
}
