package group.currencyconverter;

import java.awt.event.*;
import javax.swing.*;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.MaskFormatter;

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

    public GUI() {//creation of window an buttons etc
        try {
            // create database object to populate the comboBox
            CurrencyDB currDB = new CurrencyDB();
            Date d = new java.util.Date(currDB.getLastUpdatedDate() * 1000L);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
            loadDate = dateFormat.format(d);
            
            String[] keys = currDB.getAllCurrencyCodes();
            
            //initilising the elements of the GUI
            convertButton= new JButton("Convert");
            frame = new JFrame();
            from = new JFormattedTextField(createFormatter("####################"));
            to = new JTextField(16);
            date = new JTextField();
            toValue = new JComboBox(keys);
            fromValue = new JComboBox(keys);
            rate = new JLabel("Test Rate: ");
            fromLabel = new JLabel(currDB.getCurrencyCodeDescription(fromValue.getSelectedItem().toString()));
            toLabel = new JLabel(currDB.getCurrencyCodeDescription(toValue.getSelectedItem().toString()));

            //set the frame layout to grid for simplicity
            GridLayout grid = new GridLayout(3, 3);
            frame.setLayout(grid);
            frame.setSize(1000, 200);

        //starting with first row
            frame.add(new JLabel("Starting currency: "));
            frame.add(from);
            frame.add(fromValue);
            frame.add(fromLabel);
            //this action listener is to update the description of the currency key with what is selected in the combo box
            fromValue.addActionListener(
                    new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fromLabel.setText(currDB.getCurrencyCodeDescription(fromValue.getSelectedItem().toString()));
                }});

        //second row
            frame.add(new JLabel("Ending currency: "));
            frame.add(to);
            frame.add(toValue);
            to.setEditable(false); // no reason for being editable
            frame.add(toLabel);
            toValue.addActionListener(
                    new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    toLabel.setText(currDB.getCurrencyCodeDescription(toValue.getSelectedItem().toString()));
                }});
            
        //third row
            frame.add(convertButton);
            frame.add(new JLabel("Last load date: "));
            frame.add(date);
            frame.add(rate);
            date.setEditable(false);

        // finishing touches to the GUI
            convertButton.addActionListener(this);
            frame.setTitle("Currency Converter");
            frame.setVisible(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        } catch (Exception ex) {
// to be done later
        }
    }

    // this is where the GUI does stuff
    public void actionPerformed(ActionEvent event) {
        //update the load date
        date.setText(loadDate);

        //Here is where the conversion takes place
        try {
            // Need the database and converion objects to convert
            CurrencyDB currDB = new CurrencyDB();
            ConversionModule convMod = new ConversionModule(currDB);
            
            //Until the code hashmap is reversed this next line will be this long.
            //find the rate of the two values
            double testRate = convMod.convertCurrency(fromValue.getSelectedItem().toString(),
                                                      toValue.getSelectedItem().toString());
            
            //just converts the textbox value to a double
            double x = Double.parseDouble(from.getText());
            //update the rate for the user to see
            rate.setText("Test Rate: " + Double.toString(testRate));
            //apply the converted value to the unmodifable textbox for the user to see
            to.setText(Double.toString(x * testRate));

        } catch (Exception ex) { // auto generated
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private MaskFormatter createFormatter(String string) {
    MaskFormatter formatter = null;
    try {
        formatter = new MaskFormatter(string);
    }catch (java.text.ParseException exc) {
        System.err.println("formatter is bad: " + exc.getMessage());
        System.exit(-1);
    }
    return formatter;
}

}
