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

    public GUI() {//creation of window an buttons etc
        try {
            UIManager.put("TextField.inactiveBackground", new ColorUIResource(new Color(255, 255, 255)));
            // create database object to populate the comboBox
            CurrencyDB currDB = new CurrencyDB();
            Date d = new java.util.Date(currDB.getLastUpdatedDate() * 1000L);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
            loadDate = dateFormat.format(d);
            
            String[] keys = currDB.getAllCurrencyCodes();
            
            //initilising the elements of the GUI
            convertButton= new JButton("Convert");
            frame = new JFrame();
            from = new JTextField();
            
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
            from.addKeyListener(new KeyAdapter() { // this allows for the textfield to only accept numerical values and only one decimal point
                public void keyPressed(KeyEvent e) {
                    from.setEditable(true); // this will allow for new values to be typed in if the previous value was not accepted
                    
                    if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '.' ||
                        e.getKeyCode() == 8 || e.getKeyCode() == 127 || e.getKeyCode() == 10 || 
                        e.getKeyCode() == 37 || e.getKeyCode() == 39)  { // if values are numerical, decimal point,"backspace", "delete", or "enter"
                        
                        if (e.getKeyChar() == '.' && from.getText().contains(".")) {// restricts to only one decimal point by searching if there is one already

                            from.setEditable(false);// frevents character from being typed
                            JOptionPane.showMessageDialog(frame,
                                    "only one decimal point is allowed",
                                    "User error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                        from.setEditable(true); // this allows for the value to be entered
                        }
                    } else {               
                        from.setEditable(false);
                        JOptionPane.showMessageDialog(frame,
                                "only numerical values are allowed",
                                "User error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
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
            frame.addWindowListener(new WindowAdapter() {//---------------------------------added lines of code 
                public void windowClosing(WindowEvent we) {
                    System.exit(0);
                }
            });
            frame.setResizable(false);
        } catch (Exception ex) {
        }
    }

    // this is where the GUI does stuff
    public void actionPerformed(ActionEvent event) {
        //update the load date
        date.setText(loadDate);
        from.setEditable(true); // this allows for the textbox to be modifiable again incase if the last value entered locked it
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
            rate.setText("Test Rate: " + String.format("%.2f", testRate));
            //apply the converted value to the unmodifable textbox for the user to see
            to.setText (String.format("%.2f", (x * testRate)));

        } catch (Exception ex) { // auto generated
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
