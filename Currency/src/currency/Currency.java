/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currency;

import java.util.Iterator;
import java.util.Map;

public class Currency {

public static void main(String[] args) {
    
        //set up instance to get the current rates which returns a Map data structure.
        GetCurrentRates currentRates = new GetCurrentRates();
        Map rateMap = currentRates.getRate();
        
        // iterating rates Map
        Iterator<Map.Entry<String, Double>> itr1 = rateMap.entrySet().iterator(); 
        while (itr1.hasNext()) { 
                Map.Entry pair = itr1.next(); 
                System.out.println(pair.getKey() + " : " + pair.getValue()); 
        } 
        
        System.out.println("End of File");
        //gets the time stamp from the Map structure, converts the object to a double and formats it for output.
        Object ts = rateMap.get("TIMESTAMP");
        Double dblValue = Double.parseDouble(ts.toString());
        String str = String.format("%.0f", dblValue);
        System.out.println("Time stamp is: " + str);
    }
    
}
