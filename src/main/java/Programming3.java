/*
  Name: Filipe Pestana Frances
  Course: CNT 4714 Spring 2023
  Assignment title: Project 3 – A Two-tier Client-Server Application
  Date:  March 12, 2023

  Class: Computer Enterprise
*/

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Programming3 extends JFrame {
    public Programming3() {

        // Create the frame
        JPanel panel = new JPanel();
        JFrame frame = new JFrame("SQL Client APP - (MJL - CNT 4714 - Spring 2023 - Project 3)");
        frame.setSize(797, 645);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        // Create the Interface
        JLabel connectionDetails = new JLabel("Connection Details");
        connectionDetails.setForeground(Color.BLUE);
        connectionDetails.setFont(new Font("Times New Roma",Font.BOLD,14));
        connectionDetails.setBounds(10,0,170,20);
        panel.add(connectionDetails);

        JLabel typeCommand = new JLabel("Enter An SQL Command");
        typeCommand.setForeground(Color.BLUE);
        typeCommand.setFont(new Font("Times New Roma",Font.BOLD,14));
        typeCommand.setBounds(400,0,210,20);
        panel.add(typeCommand);

        JLabel execResults = new JLabel("SQL Execution Result Window");
        execResults.setForeground(Color.BLUE);
        execResults.setFont(new Font("Times New Roma",Font.BOLD,14));
        execResults.setBounds(10,250,210,20);
        panel.add(execResults);

        // Create textfield for command
        JTextPane commandText = new JTextPane();
        commandText.setBounds(400,25,370,120);
        panel.add(commandText);

        // Create textfield for execution results
        JTextPane execText = new JTextPane();
        execText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(execText);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(10,275,760,280);
        panel.add(scrollPane);

        // Create properties file Label
        JLabel propFiles = new JLabel("Properties File");
        Border border = BorderFactory.createLineBorder(Color.GRAY);
        propFiles.setBorder(border);
        propFiles.setFont(new Font("Verdana",Font.BOLD,12));
        propFiles.setBackground(Color.DARK_GRAY);
        propFiles.setBounds(10,27,120,30);
        panel.add(propFiles);

        // Create a dropdown list for properties file
        JComboBox<String> propSelect = new JComboBox<>(new String[] {"root.properties", "client.properties","project3app.properties","db3.properties","db4.properties"});
        propSelect.setBorder(null);
        propSelect.setUI(new BasicComboBoxUI());
        propSelect.setBackground(Color.WHITE);
        propSelect.setBounds(140,30,230,25);
        panel.add(propSelect);

        // Create username label
        JLabel userName = new JLabel("Username");
        userName.setBorder(border);
        userName.setFont(new Font("Verdana",Font.BOLD,12));
        userName.setBounds(10,64,120,30);
        panel.add(userName);

        // Create username textfield
        JTextField usernameID = new JTextField();
        usernameID.setBorder(null);
        usernameID.setBounds(140,67,230,25);
        panel.add(usernameID);

        // Create password label
        JLabel userPass = new JLabel("Password");
        userPass.setBorder(border);
        userPass.setFont(new Font("Verdana",Font.BOLD,12));
        userPass.setBounds(10,101,120,30);
        panel.add(userPass);

        // Create password textfield
        JPasswordField passwordID = new JPasswordField();
        passwordID.setBorder(null);
        passwordID.setBounds(140,104,230,25);
        panel.add(passwordID);

        // Create connect database button
        JButton connectDB = new JButton("Connect to Database");
        connectDB.setForeground(Color.BLUE);
        connectDB.setBounds(115,155,155,30);
        panel.add(connectDB);

        // Create clear command button
        JButton clearCommand = new JButton("Clear SQL Command");
        clearCommand.setBackground(Color.WHITE);
        clearCommand.setForeground(Color.RED);
        clearCommand.setBounds(414,155,155,30);
        panel.add(clearCommand);

        // Create executeSQL command button
        JButton execSQL = new JButton("Execute SQL Command");
        execSQL.setBackground(Color.GREEN);
        execSQL.setBounds(585,155,170,30);
        panel.add(execSQL);

        // Create the connection bar
        JTextPane tempConnection = new JTextPane();
        tempConnection.setText("No Connection Now");
        tempConnection.setFont(new Font("Times New Roma",Font.BOLD,13));
        tempConnection.setBackground(Color.BLACK);
        tempConnection.setEnabled(false);
        tempConnection.setBounds(12,205,757,30);
        panel.add(tempConnection);

         // Create clear result button
        JButton clearResult = new JButton("Clear Result Window");
        clearResult.setBackground(Color.YELLOW);
        clearResult.setBounds(585,565,170,30);
        panel.add(clearResult);

        // Set the frame visible
        frame.setVisible(true);

        // Check the connection
        final boolean[] isConnected = {false};

        // Create a connection to connect database
        connectDB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent eAction) {
                String username = usernameID.getText();
                String password = String.valueOf(passwordID.getPassword());
                String selectedPropFile = (String) propSelect.getSelectedItem();

                Properties props = new Properties();
                try {
                    FileInputStream in = new FileInputStream(selectedPropFile);
                    props.load(in);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String driver = props.getProperty("MYSQL_DB_DRIVER_CLASS");
                String url = props.getProperty("MYSQL_DB_URL");
                String dbUsername = props.getProperty("MYSQL_DB_USERNAME");
                String dbPassword = props.getProperty("MYSQL_DB_PASSWORD");

                if(username.equals(dbUsername) && password.equals(dbPassword)){
                    try {
                        // Load the JDBC driver
                        Class.forName(driver);

                        // Create and establish connection
                        Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                        tempConnection.setText("Connected to " + connection);
                        isConnected[0] = true;

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        // Execute SQL Commands and get results
        execSQL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Take command from commandText
                String command = commandText.getText().trim();
                String selectedPropFile = (String) propSelect.getSelectedItem();

                Properties props = new Properties();
                try {
                    FileInputStream in = new FileInputStream(selectedPropFile);
                    props.load(in);
                    in.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                String driver = props.getProperty("MYSQL_DB_DRIVER_CLASS");
                String url = props.getProperty("MYSQL_DB_URL");
                String dbUsername = props.getProperty("MYSQL_DB_USERNAME");
                String dbPassword = props.getProperty("MYSQL_DB_PASSWORD");

                if (isConnected[0]) {
                    try {
                        // Load the JDBC driver
                        Class.forName(driver);

                        // Check the connection
                        Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        if (command.toLowerCase().startsWith("update") || command.toLowerCase().startsWith("delete") || command.toLowerCase().startsWith("insert")) {
                            // Execute the update command
                            int updateInfo = statement.executeUpdate(command);

                            // Show that update went through and how many rows were updated
                            JOptionPane.showMessageDialog(null, "Successful Update..." + updateInfo + " row(s) updated.");
                        } else {
                            // Execute the query and get the result
                            ResultSet resultSet = statement.executeQuery(command);
                            ResultSetMetaData metaData = resultSet.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            // Reset the result set cursor to the first row
                            resultSet.beforeFirst();

                            // Build the table model
                            DefaultTableModel tableModel = new DefaultTableModel();
                            for (int i = 1; i <= columnCount; i++) {
                                tableModel.addColumn(metaData.getColumnName(i));
                            }
                            while (resultSet.next()) {
                                Object[] row = new Object[columnCount];
                                for (int i = 1; i <= columnCount; i++) {
                                    row[i - 1] = resultSet.getObject(i);
                                }
                             tableModel.addRow(row);
                            }

                            // Create the JTable
                            JTable resultTable = new JTable(tableModel);

                            // Create the HTML table string
                            String table = "<html><table border='3'>";

                            // Add header row
                            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                                table += "<td>" + tableModel.getColumnName(i) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>";
                            }
                            // Add data rows
                            for (int i = 0; i < tableModel.getRowCount(); i++) {
                                table += "<tr>";
                                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                                    table += "<td>" + tableModel.getValueAt(i,j) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>";
                                }
                                table += "</tr>";
                            }
                            table += "</table></html>";

                            // Set the HTML table string as the text of the JTextPane
                            execText.setContentType("text/html");
                            execText.setText(table);

                            // Set the font and row height
                            Font font = new Font("Times New Roman", Font.BOLD, 12);
                            resultTable.setFont(font);
                            resultTable.setRowHeight(20);

                            // Close the resources
                            resultSet.close();
                        }
                        statement.close();
                        connection.close();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Create functionality for the clearCommand button
        clearCommand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Set text to empty string
                commandText.setText("");
            }
        });

        // Create functionality for the clearResult button
        clearResult.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Set text to empty string
                execText.setContentType("");
            }
        });
    }

    public static void main(String[] args) {
        new Programming3();
    }

}