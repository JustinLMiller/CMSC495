package group.currencyconverter;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.ColorUIResource;

public class GUI extends JFrame implements ActionListener {

    // these are the elemtns of the GUI
    JFrame frame;
    JTextField from;
    JTextField to;
    JTextField date;
    JButton convertButton;
    String loadDate;
    JLabel fromLabel;
    JLabel toLabel;
    JLabel rate;
    JComboBox toValue;
    JComboBox fromValue;
    String[] keys;
    CurrencyDB currDB;
    Date d;
    DateFormat dateFormat;
    GridLayout grid;
    String[] description;

    // create database object to populate the comboBox
    private void createDB() {
        try {
            currDB = new CurrencyDB();
        } catch (Exception ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // creats the date object
    private void setupDate() {
        d = new java.util.Date(currDB.getLastUpdatedDate() * 1000L);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        loadDate = dateFormat.format(d);
    }

    // This populates the boxes from the hashmap
    private void populateBoxes() {
        keys = currDB.getAllCurrencyCodes();
        description = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {
            description[i] = currDB.getCurrencyCodeDescription(keys[i].toString());
        }
    }

    // initilising the elements of the GUI
    private void initializeElements() {

        //set the frame layout to grid for simplicity
        frame = new JFrame();
        grid = new GridLayout(3, 3);
        frame.setLayout(grid);
        frame.setSize(1000, 200);

        from = new JTextField();
        to = new JTextField();
        to.setEditable(false); // no reason for being editable

        toValue = new JComboBox(description);
        fromValue = new JComboBox(description);
        //Sets the from value to USD and to value to EUR by default.
        toValue.setSelectedItem(currDB.getCurrencyCodeDescription("EUR"));
        fromValue.setSelectedItem(currDB.getCurrencyCodeDescription("USD"));



        fromLabel = new JLabel(getLabel("from"));
        toLabel = new JLabel(getLabel("to"));

        date = new JTextField();
        date.setEditable(false);
        rate = new JLabel("Test Rate: ");

        convertButton = new JButton("Convert");
        convertButton.addActionListener(this);
    }

    // adds the elements to the frame to be displayed
    private void addToFrame() {
        //starting with first row
        frame.add(new JLabel("Starting currency: "));
        frame.add(from);
        frame.add(fromValue);
        frame.add(fromLabel);
        //second row
        frame.add(new JLabel("Ending currency: "));
        frame.add(to);
        frame.add(toValue);
        frame.add(toLabel);
        //third row
        frame.add(convertButton);
        frame.add(new JLabel("Last load date: "));
        frame.add(date);
        frame.add(rate);

        displayMessage("welcome");
        frame.setTitle("Currency Converter");
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        frame.setResizable(false);

        UIManager.put("TextField.inactiveBackground", new ColorUIResource(new Color(255, 255, 255)));

    }

    // contains the listeners for the dropboxes as well as the textbox
    private void addListeners() {
        //this action listener is to update the description of the currency key with what is selected in the combo box
        fromValue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fromLabel.setText(getLabel("from"));
            }
        });
        toValue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toLabel.setText(getLabel("to"));
            }
        });

        from.addKeyListener(new KeyAdapter() { // this allows for the textfield to only accept numerical values and only one decimal point
            public void keyPressed(KeyEvent e) {
                from.setEditable(true); // this will allow for new values to be typed in if the previous value was not accepted

                if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '.'
                        || e.getKeyCode() == 8 || e.getKeyCode() == 127 || e.getKeyCode() == 10
                        || e.getKeyCode() == 37 || e.getKeyCode() == 39) { // if values are numerical, decimal point,"backspace", "delete", or "enter"

                    if (e.getKeyChar() == '.' && from.getText().contains(".")) {// restricts to only one decimal point by searching if there is one already

                        from.setEditable(false);// frevents character from being typed
                        displayMessage("decimal");

                    } else {
                        from.setEditable(true); // this allows for the value to be entered
                    }
                } else {
                    from.setEditable(false);
                    displayMessage("numerical");
                }
            }
        });
    }

    // calls private methods to form the GUI a method at a time
    public GUI() {
        createDB();
        setupDate();
        populateBoxes();
        initializeElements();
        addToFrame();
        addListeners();

    }
    
    // this is where the GUI does stuff
    public void actionPerformed(ActionEvent event) {

        if (inputCheck()) {
            ConversionModule convMod = new ConversionModule(currDB);

            //update the load date
            date.setText(loadDate);
            from.setEditable(true); // this allows for the textbox to be modifiable again incase if the last value entered locked it
            //Here is where the conversion takes place

            //find the rate of the two values
            double testRate = convMod.convertCurrency(fromLabel.getText(), toLabel.getText());

            //just converts the textbox value to a double
            double x = Double.parseDouble(from.getText());
            //update the rate for the user to see
            rate.setText("Test Rate: " + String.format("%.2f", testRate));
            //apply the converted value to the unmodifable textbox for the user to see
            to.setText(String.format("%.2f", (x * testRate)));
        }
    }

    // last test for input values
    private boolean inputCheck() {
        boolean result = true;

        String[] a;
        if (from.getText().equals("") || 0.00 == Double.parseDouble(from.getText())) { // if nothing is entered or if value is equal to 0
            displayMessage("value");
            result = false;
        }
        if (from.getText().contains(".")) { // if value has too many digits after the decimal point
            a = from.getText().split("[.]");
            if (a.length == 2) {
                if (a[1].length() > 2) {
                    displayMessage("digit");
                    result = false;
                }
            }
        }
        return result;
    }
    
    // Gets the label to match the combobox selection
    private String getLabel(String origin) {
        String label = "";
        if (origin.equalsIgnoreCase("from")) {
            label = currDB.getCurrencyCodeFromDescription(fromValue.getSelectedItem().toString());
        }
        if (origin.equalsIgnoreCase("to")) {
            label = currDB.getCurrencyCodeFromDescription(toValue.getSelectedItem().toString());
        }
        return label;
    }

    // this is just a method to keep the JOptionPane messages
    private void displayMessage(String type) {
        switch (type) {
            case "welcome":
                JOptionPane.showMessageDialog(frame,
                        "Welcome to Currency Converter!\n"
                        + "Please start by selecting your starting and ending currency.\n"
                        + "Then input the amount you would like to convert.\n"
                        + "However only numerical values are allowed and only one decimal point.\n"
                        + "Once you are ready click the convert button and see your conversion.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case "decimal":
                JOptionPane.showMessageDialog(frame,
                        "only one decimal point is allowed",
                        "User error",
                        JOptionPane.ERROR_MESSAGE);
                break;

            case "numerical":
                JOptionPane.showMessageDialog(frame,
                        "only numerical values are allowed",
                        "User error",
                        JOptionPane.ERROR_MESSAGE);
                break;

            case "value":
                JOptionPane.showMessageDialog(frame,
                        "Please enter a value greater than 0.00",
                        "User error",
                        JOptionPane.ERROR_MESSAGE);
                break;

            case "digit":
                JOptionPane.showMessageDialog(frame,
                        "Please enter a value with only 2 digits after decimal point",
                        "User error",
                        JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

}
